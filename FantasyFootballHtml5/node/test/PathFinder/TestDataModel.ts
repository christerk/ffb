import { IPathFinderCoordinate, IPathFinderModel } from "../../../common/modules/PathFinder/PathFinder";

export class TestDataModel implements IPathFinderModel {
    private tackleZones: Set<number>;
    private blocked: Set<number>;

    public constructor() {
        this.tackleZones = new Set<number>();
        this.blocked = new Set<number>();
    }

    public inBounds(coord: IPathFinderCoordinate) {
        return coord.x >= 0 && coord.x <= 25 && coord.y >= 0 && coord.y <= 14;
    }
    public isEndZone(coord: IPathFinderCoordinate): boolean {
        return coord.x === 0 || coord.x === 25;
    }

    public addOpponent(coordinate: IPathFinderCoordinate) {
        this.blocked.add(coordinate.hashcode(false));

        for (let tacklezone of coordinate.neighbours) {
            this.tackleZones.add(tacklezone.hashcode(false));
        }
    }

    public addTeamMate(coordinate: IPathFinderCoordinate) {
        this.blocked.add(coordinate.hashcode(false));
    }

    public hasTackleZone(coordinate: IPathFinderCoordinate): boolean {
        return this.tackleZones.has(coordinate.hashcode(false));
    }

    public isBlocked(coordinate: IPathFinderCoordinate): boolean {
        return this.blocked.has(coordinate.hashcode(false));
    }
}
