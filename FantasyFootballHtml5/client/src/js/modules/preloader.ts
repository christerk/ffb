import { Debug, DebugLevel } from "../../../../common/util/debug";

export class Preloader {
    private images: string[] = [];
    private loadedCount: number;
    private progressCallback: (loaded: number, imageCount: number) => void;

    public constructor(progressCallback?: (loaded: number, imageCount: number) => void) {
        this.loadedCount = 0;
        if (progressCallback) {
            this.progressCallback = progressCallback;
        }
    }

    public registerImage(imagePath: string, imageFile: string) {
        this.images.push(imagePath + "/" + imageFile);
    }

    public async preload(parallel: boolean): Promise<void> {
        this.loadedCount = 0;
        return new Promise<void>(async (resolve) => {
            let list: Array<Promise<void>> = [];
            for (let imagePath of this.images) {
                if (parallel) {
                    list.push(this.loadImage(imagePath));
                } else {
                    await this.loadImage(imagePath);
                }
            }
            await Promise.all(list);

            resolve();
        });
    }

    private loadImage(imagePath: string): Promise<void> {
        return new Promise<void>((resolve) => {
            Debug.log(DebugLevel.Debug, "Loading Image: " + imagePath);
            let image = new Image();
            image.onload = () => {
                this.loadedCount++;
                if (this.progressCallback) {
                    this.progressCallback(this.loadedCount, this.images.length);
                }
                Debug.log(DebugLevel.Debug, "Loaded Image: " + imagePath);
                resolve();
            };
            image.src = imagePath;
        });
    }
}
