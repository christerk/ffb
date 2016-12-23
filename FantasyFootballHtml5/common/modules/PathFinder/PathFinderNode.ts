import { IPathFinderCoordinate } from "./PathFinder";

export class PathFinderNode {
    public coordinate: IPathFinderCoordinate;
    public cameFrom: PathFinderNode; // Breadcrumb to the previous node where the shortest path passes through.
    public gScore: number; // Cost of getting from the start node to this node
    public fScore: number; // Total expected cost to get from the start node to the end node (G + heuristic value)

    constructor(coordinate: IPathFinderCoordinate) {
        this.coordinate = coordinate;
        this.fScore = 0;
        this.gScore = 0;
    }

    public toString() {
        return this.coordinate.toString() + "(" + this.fScore + ")";
    }
}
