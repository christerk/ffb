define(["require", "exports"], function (require, exports) {
    "use strict";
    var Icon = (function () {
        function Icon(name, url) {
            this.name = null;
            this.url = null;
            this.image = null;
            this.loaded = false;
            this.name = name;
            this.url = url;
        }
        Icon.prototype.load = function (callback) {
            var _this = this;
            if (!this.loaded) {
                this.image = new Image();
                this.image.onload = function () {
                    _this.loaded = true;
                    console.log('loaded ' + _this.name);
                    if (callback) {
                        callback();
                    }
                };
                this.image.src = this.url;
            }
        };
        return Icon;
    }());
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.default = Icon;
});
//# sourceMappingURL=Icon.js.map