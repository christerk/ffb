// tslint:disable:no-console
export enum DebugLevel {
    Debug = 1,
    Info = 2,
    Warn = 3,
    Error = 4,
}

export class Debug {
    public static log(level: DebugLevel, message: string) {
        console.log(DebugLevel[level] + ": " + message);
    }
}
