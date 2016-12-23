 // tslint:disable:no-console
import { Preloader } from "./modules/preloader";
import { ISoundOptions, SoundEngine } from "./modules/soundengine";

// tslint:disable-next-line:no-string-literal
window["preloadTest"] = () => {
    let imageProgress = 0;
    let soundProgress = 0;

    function updateTotalProgress() {
        let progress = soundProgress * 0.5 + imageProgress * 0.5;
        $("#totalprogress").width(progress + "%");
    }

    let p: Preloader = new Preloader((loaded, imageCount) => {
        let progress = 100 * (loaded / imageCount);
        $("#imageprogress").width(progress + "%");
        imageProgress = progress;
        updateTotalProgress();
    });

    p.registerImage("media/images/pitches/basic", "blizzard.png");
    p.registerImage("media/images/pitches/basic", "heat.png");
    p.registerImage("media/images/pitches/basic", "nice.png");
    p.registerImage("media/images/pitches/basic", "rain.png");
    p.registerImage("media/images/pitches/basic", "sunny.png");

    $("#preloadImagesSerial").on("click", () => {
        p.preload(false)
         .then(() => {
             console.log("Images loaded");
         });
    });

    $("#preloadImagesParallel").on("click", () => {
         p.preload(true)
         .then(() => {
             console.log("Images loaded");
         });
    });

    $("#preloadAllSerial").on("click", async () => {
        await p.preload(false);
        await se.preload(false);
    });

    $("#preloadAllParallel").on("click", async () => {
        p.preload(true);
        se.preload(true);
    });

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

    let se = new SoundEngine((loaded, soundCount) => {
        let progress = 100 * (loaded / soundCount);
        $("#soundprogress").width(progress + "%");
        soundProgress = progress;
        updateTotalProgress();
    });

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
};
