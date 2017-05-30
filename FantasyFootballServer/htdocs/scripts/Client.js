define(["require", "exports", "./IconCache"], function (require, exports, IconCache_1) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    var Client = (function () {
        function Client() {
            this.xPos = 32;
            this.yPos = 32;
            this.xInc = 2;
        }
        Client.prototype.start = function () {
            this.ctxPitch = $('#field #pitch')[0].getContext("2d");
            this.ctxPlayers = $('#field #players')[0].getContext("2d");
            this.iconCache = new IconCache_1.default();
            setTimeout(function () {
                this.iconCache.init(this.init.bind(this));
            }.bind(this), 10000);
        };
        Client.prototype.drawImage = function (ctx, name, pos) {
            if (ctx && name && pos) {
                var img = this.iconCache.getImg(name);
                if (img) {
                    ctx.drawImage(img, pos.x, pos.y, img.width, img.height);
                }
            }
        };
        Client.prototype.init = function () {
            var _this = this;
            console.log('init');
            $('#loading')[0].style.opacity = '0';
            $('#loading')[0].style.width = '0';
            $('#loading')[0].style.height = '0';
            this.drawImage(this.ctxPitch, 'pitch', { x: 0, y: 0 });
            this.ctxPlayers.clearRect(0, 0, 782, 452);
            $('#inputChat')[0].onkeypress = function (event) {
                var charCode = event.which || event.keyCode;
                if (charCode === 13) {
                    var inputChat = $('#inputChat')[0];
                    _this.addToChat(inputChat.value);
                    inputChat.value = '';
                    return false;
                }
            };
            this.gameLoop();
            this.connect();
        };
        Client.prototype.gameLoop = function () {
            window.requestAnimationFrame(this.gameLoop.bind(this));
            this.redrawPlayer();
        };
        Client.prototype.redrawPlayer = function () {
            this.ctxPlayers.clearRect(this.xPos, this.yPos, 28, 28);
            this.xPos += this.xInc;
            if ((this.xPos >= 720) || (this.xPos <= 32)) {
                this.xInc *= -1;
            }
            var img = this.iconCache.getImg('amblitzer1');
            if (this.xInc > 0) {
                this.ctxPlayers.drawImage(img, 1 * 28, 0, 28, 28, this.xPos, this.yPos, 28, 28);
            }
            else {
                this.ctxPlayers.drawImage(img, 3 * 28, 0, 28, 28, this.xPos, this.yPos, 28, 28);
            }
        };
        Client.prototype.connect = function () {
            var _this = this;
            var command = { "netCommandId": "clientRequestVersion" };
            var connection = new WebSocket("ws://localhost:2224/command");
            connection.onopen = function () {
                _this.addToLog('Connection open');
                var jsonString = JSON.stringify(command);
                connection.send(LZString.compressToUTF16(jsonString));
            };
            connection.onclose = function () {
                _this.addToLog('Connection closed');
            };
            connection.onerror = function (error) {
                _this.addToLog('WebSocket Error: ' + error);
            };
            connection.onmessage = function (message) {
                var jsonString = LZString.decompressFromUTF16(message.data);
                _this.addToLog('Server: ' + jsonString);
            };
        };
        Client.prototype.addToLog = function (message) {
            if (message) {
                var divLog = $('#divLog');
                divLog.append($('<div class="line">')).append(message);
            }
        };
        Client.prototype.addToChat = function (message) {
            if (message) {
                var divChat = $('#divChat');
                divChat.append($('<div class="line">')).append(message);
                divChat[0].scrollTop = divChat[0].scrollHeight;
            }
        };
        return Client;
    }());
    function start() {
        new Client().start();
    }
    exports.start = start;
});
