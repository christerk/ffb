/// <reference path="./lib/jquery.d.ts" />
/// <reference path="./lib/lzstring.d.ts" />

import IconCache from './IconCache';

interface Position {
	x: number;
	y: number;
}

class Client {

	ctxPitch: CanvasRenderingContext2D;
	ctxPlayers: CanvasRenderingContext2D;
	iconCache: IconCache;
	xPos: number = 32;
	yPos: number = 32;
	xInc: number = 2;

	start(): void {

		this.ctxPitch = (<HTMLCanvasElement> $('#field #pitch')[0]).getContext("2d");
		this.ctxPlayers = (<HTMLCanvasElement> $('#field #players')[0]).getContext("2d");
		this.iconCache = new IconCache();

		setTimeout(
			function() {
				this.iconCache.init(this.init.bind(this));
			}.bind(this),
			10000
		);

	}

	drawImage(ctx: CanvasRenderingContext2D, name: string, pos: Position): void {
		if (ctx && name && pos) {
			var img = this.iconCache.getImg(name);
			if (img) {
				ctx.drawImage(img, pos.x, pos.y, img.width, img.height);
			}
		}
	}

	init(): void {
		console.log('init');
		// fade-out via transition
		$('#loading')[0].style.opacity = '0';
		// transition delayed to after fade-out
		$('#loading')[0].style.width = '0';
		$('#loading')[0].style.height = '0';
		// init pitch layer
		this.drawImage(this.ctxPitch, 'pitch', { x:0, y:0 });
		// init player layer
		this.ctxPlayers.clearRect(0, 0, 782, 452);
		// init chat input
		$('#inputChat')[0].onkeypress = (event: KeyboardEvent) => {
			var charCode: number = event.which || event.keyCode;
			if (charCode === 13) {
				var inputChat = <HTMLInputElement> $('#inputChat')[0];
				this.addToChat(inputChat.value);
				inputChat.value = '';
				return false;
			}
		}
		// start the game loop
		this.gameLoop();
		// open connection to FFB server
		this.connect();
	}

	gameLoop(): void {
		window.requestAnimationFrame(this.gameLoop.bind(this));
		this.redrawPlayer();
	}

	redrawPlayer(): void {
		this.ctxPlayers.clearRect(this.xPos, this.yPos, 28, 28);
		this.xPos += this.xInc;
		if ((this.xPos >= 720) || (this.xPos <= 32)) {
			this.xInc *= -1;
		}
		var img = this.iconCache.getImg('amblitzer1');
		if (this.xInc > 0) {
			this.ctxPlayers.drawImage(img, 1 * 28, 0, 28, 28, this.xPos, this.yPos, 28, 28);
		} else {
			this.ctxPlayers.drawImage(img, 3 * 28, 0, 28, 28, this.xPos, this.yPos, 28, 28);
		}
	}

	connect(): void {

		var command = { "netCommandId" : "clientRequestVersion" };
		var connection = new WebSocket("ws://localhost:2224/command");

		connection.onopen = () => {
			this.addToLog('Connection open');
			var jsonString = JSON.stringify(command);
			connection.send(LZString.compressToUTF16(jsonString));
		};

		connection.onclose = () => {
			this.addToLog('Connection closed');
		};

		connection.onerror = (error) => {
			this.addToLog('WebSocket Error: ' + error);
		};

		connection.onmessage = (message) => {
			var jsonString = LZString.decompressFromUTF16(message.data);
			this.addToLog('Server: ' + jsonString);
		};

	}

	addToLog(message: string): void {
		if (message) {
			var divLog = $('#divLog');
			divLog.append($('<div class="line">')).append(message);
		}
	}

	addToChat(message: string): void {
		if (message) {
			var divChat = $('#divChat');
			divChat.append($('<div class="line">')).append(message);
			divChat[0].scrollTop = divChat[0].scrollHeight;
		}
	}

}

export function start() {
	new Client().start();
}
