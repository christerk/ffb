export class Util {
    public static extend(...args) {
        for (let i = 1; i < args.length; i++) {
            for (let key in args[i]) {
                if (args[i].hasOwnProperty(key)) {
                    args[0][key] = args[i][key];
                }
            }
        }
        return args[0];
    }

    public static eachOf<T>(list: T[], func: (T) => void) {
        for (let item of list) {
            func(item);
        }
    }

    public static eachIn<T>(list: {}, func: (T) => void) {
        for (let item in list) {
            if (list.hasOwnProperty(item)) {
                func(item);
            }
        }
    }

}
