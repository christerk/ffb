define(["require", "exports", './Icon'], function (require, exports, Icon_1) {
    "use strict";
    var IconCache = (function () {
        function IconCache() {
            this.icons = {};
        }
        IconCache.prototype.hasUnloadedIcons = function () {
            var name;
            for (name in this.icons) {
                if (this.icons.hasOwnProperty(name)) {
                    if (!this.icons[name].loaded) {
                        return true;
                    }
                }
            }
            return false;
        };
        IconCache.prototype.init = function (callback) {
            this.addIcon('pitch', '/icons/pitches/default/nice.jpg');
            this.addIcon('amblitzer1', '/icons/players/iconsets/amazon_blitzer.png');
            this.refresh(callback);
        };
        IconCache.prototype.addIcon = function (name, url) {
            this.icons[name] = new Icon_1.default(name, url);
        };
        IconCache.prototype.getImg = function (name) {
            return this.icons[name] ? this.icons[name].image : null;
        };
        IconCache.prototype.refresh = function (callback) {
            console.log('refresh');
            var name, myself = this;
            for (name in this.icons) {
                if (this.icons.hasOwnProperty(name)) {
                    this.icons[name].load(function () {
                        if (!myself.hasUnloadedIcons() && callback) {
                            callback();
                        }
                    });
                }
            }
        };
        return IconCache;
    }());
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.default = IconCache;
});
