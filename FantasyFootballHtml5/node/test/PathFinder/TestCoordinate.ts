import { IPathFinderCoordinate, IPathFinderModel } from "../../../common/modules/PathFinder/PathFinder";

export class TestCoordinate implements IPathFinderCoordinate {
    public x: number;
    public y: number;
    public hasUsedLeap: boolean;

    private dataModel: IPathFinderModel;

    constructor(dataModel: IPathFinderModel, x: number, y: number, hasUsedLeap: boolean) {
        this.dataModel = dataModel;
        this.x = x;
        this.y = y;
        this.hasUsedLeap = hasUsedLeap;
    }

    public create(x: number, y: number, hasUsedLeap: boolean): IPathFinderCoordinate {
        return new TestCoordinate(this.dataModel, x, y, hasUsedLeap);
    }

    get neighbours(): IPathFinderCoordinate[] {
        return [
            [this.x - 1, this.y], // This ordering ensures that we try linear movements prior to diagonal ones
            [this.x,     this.y - 1],
            [this.x + 1, this.y],
            [this.x,     this.y + 1],
            [this.x - 1, this.y - 1],
            [this.x - 1, this.y + 1],
            [this.x + 1, this.y - 1],
            [this.x + 1, this.y + 1],
        ]
        // Convert coordinate tuples to Coordinate objects
        .map((c) => this.create(c[0], c[1], this.hasUsedLeap))
        // Filter out coordinates that are not in bounds or blocked
        .filter((coord) => this.dataModel.inBounds(coord) && !this.dataModel.isBlocked(coord));
    }
    get leapMoves(): IPathFinderCoordinate[] {
        let result: IPathFinderCoordinate[] = [];
        for (let y = this.y - 2; y <= this.y + 2; y++) {
            for (let x = this.x - 2; x <= this.x + 2; x++) {
                if ((x !== this.x || y !== this.y)) {
                    let coord = this.create(x, y, true);
                    result.push(this.create(x, y, true));
                }
            }
        }
        result = result.sort((a, b) => { // This sort ensures that we try linear leaps first.
            let aInline = a.x === this.x || a.y === this.y;
            let bInline = b.x === this.x || b.y === this.y;

            return aInline && !bInline ? -1 : (bInline && !aInline ? 1 : 0);
        });
        return result
            .filter((c) => this.dataModel.inBounds(c) && !this.dataModel.isBlocked(c));
    }
    public equals(other: IPathFinderCoordinate, distinguishLeap: boolean) {
        return other && other.x === this.x && other.y === this.y &&
            (!distinguishLeap || other.hasUsedLeap === this.hasUsedLeap);
    }
    public hashcode(includeFlags: boolean) {
        return this.x * 1000 + this.y + (includeFlags ? (this.hasUsedLeap ? 0.5 : 0) : 0);
    }
    public toString() {
        return this.x + "," + this.y + ":" + this.hasUsedLeap;
    }
}
