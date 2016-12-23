/* tslint:disable */
var gulp        = require("gulp");
var browserify  = require("browserify");
var source      = require("vinyl-source-stream");
var buffer      = require("vinyl-buffer");
var tsify       = require("tsify");
var less        = require("gulp-less");

var sourcemaps  = require("gulp-sourcemaps");
var uglify      = require("gulp-uglify");
var exec        = require("child_process").exec;

gulp.task("bundle", ["less"], function() {
    // Compile and bundle FFB typescript.
    var mainTsFilePath = ["client/src/js/soundtest.ts", "client/src/js/preloadtest.ts"];
    var outputFolder = "client/htdocs/js/";
    var outputFileName = "ffb.min.js";

    var bundler = browserify({
        debug: true,
    });

    return bundler.add(mainTsFilePath)
        .plugin(tsify, { })
        .bundle()
        .pipe(source(outputFileName))
        .pipe(buffer())
        .pipe(sourcemaps.init({ loadMaps: true }))
       // .pipe(uglify())
        .pipe(sourcemaps.write('./'))
        .pipe(gulp.dest(outputFolder));
});

gulp.task("less", function() {
    // Compile LESS files to CSS
    gulp.src("./client/htdocs/style/**/*.less")
        .pipe(less())
        .pipe(gulp.dest(function(f) {
            return f.base;
        }));
});

gulp.task("buildNode", function(cb) {
    exec('tsc -p node', function(err, stdout, stderr) {
        console.log(stdout);
        console.log(stderr);
        cb(err);
    });
});
