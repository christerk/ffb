/* Sound Engine implementation
 *
 * Dependencies: 
 *  - Howler.js - Underlying audio library.
 *  - es6-shim - Provides Promises for browsers not yet implementing es6.
 *
 */

import { Debug, DebugLevel } from "../../../../common/util/debug";
import { Util } from "../../../../common/util/util";

export interface ISoundOptions {
    allowOverlap?: boolean;
    volume?: number;
}

interface ISoundState extends ISoundOptions {
    playing?: number;
    path?: string;
    files?: string[];
    howl?: Howl;
    loaded?: boolean;
}

export class SoundEngine {
    private progressCallback: (loaded: number, soundCount: number) => void;
    private soundCount: number;
    private loadedCount: number;
    private sounds: { [propName: string]: ISoundState } = { };

    private defaultOptions: ISoundState = {
        allowOverlap: true,
        volume: 1.0,
    };

    public constructor(progressCallback?: (loaded: number, soundCount: number) => void) {
        Howler.volume(1.0);
        if (progressCallback) {
            this.progressCallback = progressCallback;
        }
        this.loadedCount = 0;
        this.soundCount = 0;
    };

    public registerSound(path: string, sound: string, options: ISoundOptions): void {
        let state: ISoundState = Util.extend({ }, this.defaultOptions, options);
        state.path = path;
        state.playing = 0;
        state.files = [path + "/" + sound + ".webm", path + "/" + sound + ".mp3"];

        if (this.sounds[sound] === undefined) {
            this.soundCount++;
        }
        this.sounds[sound] = state;
    }

    public preload(parallel: boolean): Promise<void> {
        this.loadedCount = 0;
        return new Promise<void>(async (resolve) => {
            let list: Array<Promise<void>> = [];
            for (let sound in this.sounds) {
                if (parallel) {
                    list.push(this.loadFile(sound));
                } else {
                    await this.loadFile(sound);
                }
            }
            await Promise.all(list);

            resolve();
        });
    }

    public async play(sound: string, options?: ISoundOptions) {
        if (!this.sounds[sound].loaded) {
            await this.loadFile(sound);
        }

        let opts: ISoundState = Util.extend({ }, this.defaultOptions, this.sounds[sound], options);

        if (!opts.allowOverlap && opts.playing) {
            return;
        }

        let snd = this.sounds[sound].howl;

        if (snd !== undefined) {
            snd.volume(opts.volume);

            this.sounds[sound].playing++;
            snd.on("end", () => {
                this.sounds[sound].playing--;
            });
            snd.play();
        }
    }

    public globalVolume(volume: number): void {
        Howler.volume(volume);
    }

    public stop(): void {
        Howler.unload();
        Util.eachIn(this.sounds, (sound) => {
            this.sounds[sound].howl = undefined;
            this.sounds[sound].loaded = false;
        });
    }

    private loadFile(sound: string): Promise<void> {
        let opts: ISoundState = Util.extend({ }, this.defaultOptions, this.sounds[sound]);
        Debug.log(DebugLevel.Debug, "Loading sound: " + sound);
        return new Promise<void>((resolve) => {
            let file = new Howl({
                onload: () => {
                    this.sounds[sound].howl = file;
                    if (!this.sounds[sound].loaded) {
                        this.sounds[sound].loaded = true;
                        this.loadedCount++;
                        if (this.progressCallback) {
                            this.progressCallback(this.loadedCount, this.soundCount);
                        }
                    }
                    Debug.log(DebugLevel.Debug, "Sound loaded: " + sound);
                    resolve();
                },
                src: opts.files,
                volume: opts.volume,
            });
        });
    }
}
