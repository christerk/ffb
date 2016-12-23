 // tslint:disable:no-console
import { ISoundOptions, SoundEngine } from "./modules/soundengine";
// tslint:disable-next-line:no-string-literal
window["soundTest"] = (progressCallback?: (loaded: number, soundCount: number) => void) => {
    let sounds: { [propName: string]: ISoundOptions } = {
        Aaah: { volume: 0.1 },
        Booing: { volume: 0.1 },
        Cheering: { volume: 0.1 },
        ClapBoo: { volume: 0.1 },
        Clapping: { volume: 0.1 },
        Crickets: { volume: 0.1 },
        Giggling: { volume: 0.1 },
        Hysterical: { volume: 0.1 },
        Laughter: { volume: 0.1 },
        Oooh: { volume: 0.1 },
        Shocked: { volume: 0.1 },
        Stomping: { volume: 0.1 },
    };

    if (!progressCallback) {
        progressCallback = (loaded, soundCount) => {
            let progress = 100 * (loaded / soundCount);
            $("#soundprogress").width(progress + "%");
        }
    }

    let se = new SoundEngine(progressCallback);

    $.each(sounds, (sound, options) => {
        se.registerSound("media/sounds", sound, options);
    });

    $(".soundbutton").click((evt) => {
        se.play($(evt.target).attr("file"));
    });

    $("#globalvolume").on("change", (evt) => {
       se.globalVolume($(evt.target).val() / 100);
    });

    $("#stopbutton").on("click", () => {
        se.stop();
    });

    $("#preloadSoundsSerial").on("click", () => {
        se.preload(false)
        .then(() => {
            console.log("Sounds loaded.");
        });
    });

    $("#preloadSoundsParallel").on("click", () => {
        se.preload(true)
        .then(() => {
            console.log("Sounds loaded.");
        });
    });
}
