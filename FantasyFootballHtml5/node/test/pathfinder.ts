import { IPathFinderCoordinate, PathFinder, PathFinderOptions } from "../../common/modules/PathFinder/PathFinder";
import { TestCoordinate } from "./PathFinder/TestCoordinate";
import { TestDataModel } from "./PathFinder/TestDataModel";

export function testPathFinder(req, res) {
    /*
        Very simple test app for the path finder.
    */

    function coord(dataModel: TestDataModel, x: number, y: number): IPathFinderCoordinate {
        return new TestCoordinate(dataModel, x, y, false);
    }
    function opponent(dataModel: TestDataModel, x: number, y: number): void {
        dataModel.addOpponent(coord(dataModel, x, y));
    }
    function teamMate(dataModel: TestDataModel, x: number, y: number): void {
        dataModel.addTeamMate(coord(dataModel, x, y));
    }

    type PathFinderTest = {
        name: string,
        start: [number, number],
        target: Array<[number, number]>,
        opponents?: Array<[number, number]>,
        teamMates?: Array<[number, number]>,
        options: PathFinderOptions,
        test: (path: IPathFinderCoordinate[] | undefined) => boolean,
    };

    let tests: PathFinderTest[] = [
        {
            name: "Only right",
            options: { maxDistance: 10 },
            start: [5, 10],
            target: [[15, 10]],
            test: (path: IPathFinderCoordinate[] | undefined) => {
                return path !== undefined && path.length === 11 && path.filter((c) => c.y === 10).length === 11;
            },
        },
        {
            name: "Only left",
            options: { maxDistance: 10 },
            start: [15, 10],
            target: [[5, 10]],
            test: (path: IPathFinderCoordinate[] | undefined) => {
                return path !== undefined && path.length === 11 && path.filter((c) => c.y === 10).length === 11;
            },
        },
        {
            name: "Short diagonal",
            options: { maxDistance: 10 },
            start: [5, 10],
            target: [[15, 12]],
            test: (path) => {
                return path !== undefined && path.length === 11 && path.filter((c) => c.y === 12).length === 9;
            },
        },
        {
            name: "Long diagonal",
            options: { maxDistance: 10 },
            start: [5, 5],
            target: [[15, 13]],
            test: (path) => {
                return path !== undefined && path.length === 11 && path.filter((c) => c.y === 13).length === 3;
            },
        },
        {
            name: "Attempt leap through wall",
            opponents: [[10, 8], [10, 10], [10, 12]],
            options: { maxDistance: 6, allowLeap: true, allowLeaveTackleZone: false },
            start: [8, 10],
            target: [[12, 8]],
            test: (path) => {
                return path === undefined;
            },
        },
        {
            name: "Dodge through wall",
            opponents: [[10, 8], [10, 10], [10, 12]],
            options: { maxDistance: 6, allowLeap: false, allowLeaveTackleZone: true },
            start: [8, 10],
            target: [[12, 8]],
            test: (path) => {
                return path !== undefined && path.length === 5;
            },
        },
    ];

    let output: string = "";
    for (let test of tests) {
        let dm = new TestDataModel();
        if (test.opponents !== undefined) {
            for (let opponent of test.opponents) {
                dm.addOpponent(coord(dm, opponent[0], opponent[1]));
            }
        }
        if (test.teamMates !== undefined) {
            for (let opponent of test.teamMates) {
                dm.addTeamMate(coord(dm, opponent[0], opponent[1]));
            }
        }
        let start = coord(dm, test.start[0], test.start[1]);
        let target = test.target.map((c) => coord(dm, c[0], c[1]));
        let path = (new PathFinder(dm, test.options)).findPath(start, target);
        let result = test.test(path);
        output += ((result ? "Success" : "FAIL  ") + " Test \"" + test.name + "\"") + "\n";
    }
    res.header("Content-type", "text/plain");
    res.send(output);
};
