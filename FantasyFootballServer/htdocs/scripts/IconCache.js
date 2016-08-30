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
            this.addIcon('playerDetailsRed', '/icons/client/bg_player_details_red.png');
            this.addIcon('boxButtonsRed', '/icons/client/bg_box_buttons_red.png');
            this.addIcon('turnDiceStatusRed', '/icons/client/bg_turn_dice_status_red.png');
            this.addIcon('resourcesRed', '/icons/client/bg_resources_red.png');
            this.addIcon('scorebar', '/icons/client/bg_scorebar.png');
            this.addIcon('playerDetailsBlue', '/icons/client/bg_player_details_blue.png');
            this.addIcon('boxButtonsBlue', '/icons/client/bg_box_buttons_blue.png');
            this.addIcon('turnDiceStatusBlue', '/icons/client/bg_turn_dice_status_blue.png');
            this.addIcon('resourcesBlue', '/icons/client/bg_resources_blue.png');
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
//# sourceMappingURL=IconCache.js.map