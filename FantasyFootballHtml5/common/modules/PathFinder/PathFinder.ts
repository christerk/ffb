/* Path finder implementation
 *
 * Dependencies: BinaryHeap, es6-shim
 */
import { BinaryHeap } from "../../util/BinaryHeap";
import { PathFinderNode } from "./PathFinderNode";

export interface IPathFinderCoordinate {
    x: number;
    y: number;
    // This is an extra coordinate we use to search if leap moves are allowed.
    hasUsedLeap: boolean;

    // Should be a list of neighbour coordinates for which the search can pass through.
    neighbours: IPathFinderCoordinate[];

    // Should be a list of coordinates possible to leap to. This should include the single step squares.
    leapMoves: IPathFinderCoordinate[];

    equals(other: IPathFinderCoordinate, distinguishLeap: boolean): boolean;
    hashcode(includeFlags: boolean): number; // Must be implemented and unique for each possible coordinate.
}

export interface IPathFinderModel {
    hasTackleZone(coord: IPathFinderCoordinate): boolean;
    isEndZone(coord: IPathFinderCoordinate): boolean;
    inBounds(coord: IPathFinderCoordinate): boolean;
    isBlocked(coord: IPathFinderCoordinate): boolean;
}

export type PathFinderOptions = {
    allowLeap?: boolean,
    allowLeaveTackleZone?: boolean,
    allowMoveFromEndZone?: boolean,
    maxDistance?: number,
};

export class PathFinder {
    private dataModel: IPathFinderModel;

    private settings: PathFinderOptions = {
        allowLeap: false,
        allowLeaveTackleZone: false,
        allowMoveFromEndZone: false,
        maxDistance: 10,
    };

    public constructor(dataModel: IPathFinderModel, options: PathFinderOptions) {
        this.dataModel = dataModel;
        // Somewhat inelegant way to read options.
        for (let setting in this.settings) {
            if (this.settings.hasOwnProperty(setting) && options.hasOwnProperty(setting)) {
                this.settings[setting] = options[setting];
            }
        }
    }

    public findPath(start: IPathFinderCoordinate, targets: IPathFinderCoordinate[]):
        IPathFinderCoordinate[] | undefined {
        // The closed set contains nodes for which the shortest path is already known
        // ie, the system has already processed it.
        let closedSet = new Set<number>();
        // The open set contains the squares we are currently planning to processes,
        // ordered by the expected distance to the target node(s)
        let openSet = new BinaryHeap<PathFinderNode>((a, b) =>
            a.coordinate.hasUsedLeap !== b.coordinate.hasUsedLeap ?
            (!a.coordinate.hasUsedLeap && b.coordinate.hasUsedLeap) :
            a.fScore < b.fScore);
        // We start by adding the start coordinate to the open set.
        let startNode = new PathFinderNode(start);
        startNode.fScore = this.heuristicDistance(start, targets);
        openSet.push(startNode);

        while (openSet.size() > 0) {
            // Get the next node to process. This is the node with the closest estimated total distance
            let current = openSet.pop();

            // "Are we there yet?". If so, return the path found.
            for (let target of targets) {
                if (current.coordinate.equals(target, false)) {
                    return this.retracePath(current);
                }
            }

            // Mark the current node as processed.
            closedSet.add(current.coordinate.hashcode(true));

            if (!this.settings.allowLeaveTackleZone && this.dataModel.hasTackleZone(current.coordinate)) {
                continue; // We're in a tackle zone and don't allow pathfinding out of it
            }

            if (!this.settings.allowMoveFromEndZone && this.dataModel.isEndZone(current.coordinate)) {
                continue; // We're in the endzone and don't allow moving from it
            }

            // Find possible next steps
            let neighbours = current.coordinate.neighbours;
            if (this.settings.allowLeap && !current.coordinate.hasUsedLeap) {
                neighbours = [...neighbours, ...current.coordinate.leapMoves];
            }

            neighbours.forEach((neighbourCoordinate) => {
                // The neighbour we're looking at has already been processed before (with a shorter or equal distance),
                // so skip to the next one
                if (closedSet.has(neighbourCoordinate.hashcode(true))) {
                    return; // Note that this is in a lambda, so this is the equivalent of a "continue" for a for loop.
                }

                // Calculate the new actual distance to the neighbour through the current node.
                let tentativeGScore = current.gScore + this.absoluteDistance(current.coordinate, neighbourCoordinate);

                if (tentativeGScore > this.settings.maxDistance) {
                    return; // Limit search to max distance
                }

                // Is the neighbour already in the open set (ie, did we find another potential path to it)?
                let neighbourNode = openSet.find(neighbourCoordinate, (a: PathFinderNode, b: IPathFinderCoordinate) => {
                    return a.coordinate.equals(b, true);
                });

                // Because of the way the open set works, we don't want to immediately push the node to it.
                // So we simply use a flag to indicate that the node needs to be pushed.
                let pushNode = false;
                if (neighbourNode === undefined) { // This is a new node
                    neighbourNode = new PathFinderNode(neighbourCoordinate);
                    pushNode = true;
                } else if (neighbourCoordinate.hasUsedLeap && !neighbourNode.coordinate.hasUsedLeap) {
                    return; // The previously found path doesn't use leap, so it's better.
                } else if (tentativeGScore >= neighbourNode.gScore) {
                    return; // This is not a better path.
                }

                // We found a better path, so set the various properties.
                neighbourNode.cameFrom = current;
                neighbourNode.gScore = tentativeGScore;
                neighbourNode.fScore = tentativeGScore + this.heuristicDistance(neighbourCoordinate, targets);
                if (pushNode) {
                    // This is a new node that we haven't considered before, so add it to the open set.
                    openSet.push(neighbourNode);
                } else {
                    // The node has a better fScore, so we need to signal a priority change
                    openSet.rebalance(neighbourNode, (a: PathFinderNode, b: IPathFinderCoordinate) => {
                        return a.coordinate.equals(b, true);
                    });
                }
            });
        }
    }

    // The absolute distance between two coordinates.
    private absoluteDistance(a: IPathFinderCoordinate, b: IPathFinderCoordinate) {
        let dX = Math.abs(a.x - b.x);
        let dY = Math.abs(a.y - b.y);
        return Math.max(dX, dY);
    }

    // The expected distance between two coordinates. This method must not return a value larger
    // than the actual distance.
    private heuristicDistance(a: IPathFinderCoordinate, targets: IPathFinderCoordinate[]): number {
        let minDistance = -1;

        for (let b of targets) {
            let dX = Math.abs(a.x - b.x);
            let dY = Math.abs(a.y - b.y);
            let diagonal = Math.min(dX, dY); // The natural number of diagonal steps to take
            let linear = Math.max(dX, dY) - diagonal; // The linear distance remaining after moving diagonally
            let distance = diagonal + linear * 0.999; // Prefer moving in a straight line
            if (minDistance === -1 || distance < minDistance) {
                minDistance = distance;
            }
        }
        return minDistance;
    }

    // Returns the path taken from the start node.
    private retracePath(node: PathFinderNode): IPathFinderCoordinate[] {
        let current = node;
        let result = [current.coordinate];
        while (current.cameFrom !== undefined) {
            result.unshift(current.cameFrom.coordinate); // We unshift in order to reverse the path.
            current = current.cameFrom;
        }
        return result;
    }
}
