/// <reference path="./lib/lz-string.d.ts" />

import IconCache from './IconCache';

class Client {

	ctxClient: CanvasRenderingContext2D;
	ctxPitch: CanvasRenderingContext2D;
	ctxPlayers: CanvasRenderingContext2D;
	iconCache: IconCache;
	xPos: number = 32;
	yPos: number = 32;
	xInc: number = 2;

	start(): void {

		this.ctxClient = (<HTMLCanvasElement> document.getElementById('cvsClient')).getContext("2d");
		this.ctxPitch = (<HTMLCanvasElement> document.getElementById('cvsPitch')).getContext("2d");
		this.ctxPlayers = (<HTMLCanvasElement> document.getElementById('cvsPlayers')).getContext("2d");

		this.iconCache = new IconCache();
		this.iconCache.init(this.init.bind(this));

	}

	drawImage(ctx, name, pos): void {
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
		document.getElementById('divLoading').style.opacity = '0'; 
		// transition delayed to after fade-out
		document.getElementById('divLoading').style.width = '0';
		document.getElementById('divLoading').style.height = '0';
		// init client
		this.drawImage(this.ctxClient, 'playerDetailsRed', { x:0, y:0 });
		this.drawImage(this.ctxClient, 'boxButtonsRed', { x:0, y:430 });
		this.drawImage(this.ctxClient, 'turnDiceStatusRed', { x:0, y:452 });
		this.drawImage(this.ctxClient, 'resourcesRed', { x:0, y:544 });
		this.drawImage(this.ctxClient, 'scorebar', { x:116, y:452 });
		this.drawImage(this.ctxClient, 'playerDetailsBlue', { x:897, y:0 });
		this.drawImage(this.ctxClient, 'boxButtonsBlue', { x:897, y:430 });
		this.drawImage(this.ctxClient, 'turnDiceStatusBlue', { x:897, y:452 });
		this.drawImage(this.ctxClient, 'resourcesBlue', { x:897, y:544 });
		// init pitch layer
		this.drawImage(this.ctxPitch, 'pitch', { x:0, y:0 });
		// init player layer
		this.ctxPlayers.clearRect(0, 0, 782, 452);
		// init chat input
		document.getElementById('inputChat').onkeypress = (event: KeyboardEvent) => {
			var charCode: number = event.which || event.keyCode;
			if (charCode === 13) {
				let inputChat = <HTMLInputElement> document.getElementById('inputChat');
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
			var divLog = document.getElementById('divLog');
			divLog.insertAdjacentHTML('beforeend', '<div class="line">' + message + '</div>');
		}
	}

	addToChat(message: string): void {
		if (message) {
			var divChat = document.getElementById('divChat');
			divChat.insertAdjacentHTML('beforeend', '<div class="line">' + message + '</div>');
		}
	}

}

export function start() {
	new Client().start();
}