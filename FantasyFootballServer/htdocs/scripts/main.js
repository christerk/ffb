require(['lib/domReady', 'lib/lzString'], function (domReady, lzString) {
	
	// This function is called once the DOM is ready.
	// It will be safe to query the DOM and manipulate DOM nodes in this function.
	domReady(function () {
		
		var images = { };

		var ctxClient = document.getElementById('canvasClient').getContext("2d");
		var ctxPlayers = document.getElementById('canvasPlayers').getContext("2d");

		var imagesToBeLoaded = 11;

		// client icons
		loadImage('playerDetailsRed', '/icons/client/bg_player_details_red.png');
		loadImage('boxButtonsRed', '/icons/client/bg_box_buttons_red.png');
		loadImage('turnDiceStatusRed', '/icons/client/bg_turn_dice_status_red.png');
		loadImage('resourcesRed', '/icons/client/bg_resources_red.png');
		loadImage('scorebar', '/icons/client/bg_scorebar.png');
		loadImage('playerDetailsBlue', '/icons/client/bg_player_details_blue.png');
		loadImage('boxButtonsBlue', '/icons/client/bg_box_buttons_blue.png');
		loadImage('turnDiceStatusBlue', '/icons/client/bg_turn_dice_status_blue.png');
		loadImage('resourcesBlue', '/icons/client/bg_resources_blue.png');

		// pitch
		loadImage('pitch', '/icons/pitches/default/nice.jpg');

		// player icon
		loadImage('amblitzer1', '/icons/players/iconsets/amazon_blitzer.png');

		function loadImage(name, path) {
			images[name] = new Image();
			images[name].onload = function() {
				imageOnLoad();
			}
			images[name].src = path;
		}

		function imageOnLoad() {
			imagesToBeLoaded--;
			if (!imagesToBeLoaded) {
				init();
			}
		}

		function drawImage(name, pos) {
			if (name && pos) {
				var img = images[name];
				if (img) {
					ctxClient.drawImage(img, pos.x, pos.y, img.width, img.height);
				}
			}
		}
		
		function init() {
			// fade-out via transition
			document.getElementById('loading').style.opacity = 0; 
			// transition delayed to after fade-out
			document.getElementById('loading').style.width = 0;
			document.getElementById('loading').style.height = 0;
			// init client
			drawImage('playerDetailsRed', { x:0, y:0 });
			drawImage('boxButtonsRed', { x:0, y:430 });
			drawImage('turnDiceStatusRed', { x:0, y:452 });
			drawImage('resourcesRed', { x:0, y:544 });
			drawImage('pitch', { x:116, y:0 });
			drawImage('scorebar', { x:116, y:452 });
			drawImage('playerDetailsBlue', { x:897, y:0 });
			drawImage('boxButtonsBlue', { x:897, y:430 });
			drawImage('turnDiceStatusBlue', { x:897, y:452 });
			drawImage('resourcesBlue', { x:897, y:544 });
			ctxPlayers.clearRect(0, 0, 782, 452);
			// start the game loop
			gameLoop();
			// open connection to FFB server
			connect();
		}
		
		function gameLoop() {
			window.requestAnimationFrame(gameLoop);
			redrawPlayer();
		}
		
		var xPos = 32;
		var yPos = 32;
		var xInc = 2;

		function redrawPlayer() {
			ctxPlayers.clearRect(xPos, yPos, 28, 28);
			xPos += xInc;
			if ((xPos >= 720) || (xPos <= 32)) {
				xInc *= -1;
			}
			if (xInc > 0) {
				ctxPlayers.drawImage(images['amblitzer1'], 1 * 28, 0, 28, 28, xPos, yPos, 28, 28);
			} else {
				ctxPlayers.drawImage(images['amblitzer1'], 3 * 28, 0, 28, 28, xPos, yPos, 28, 28);
			}
		}
		
		function connect() {
			
			var command = { "netCommandId" : "clientRequestVersion" };
			
			var connection = new WebSocket("ws://localhost:2224/command");
			
			connection.onopen = function() {
				console.log('Connection open');
				var jsonString = JSON.stringify(command);
				connection.send(lzString.compressToUTF16(jsonString));
			};
			
			connection.onclose = function() {
				console.log('Connection closed');
			};
			
			connection.onerror = function(error) {
				console.log('WebSocket Error: ' + error);
			};
			
			connection.onmessage = function(message) {
				var jsonString = lzString.decompressFromUTF16(message.data);
				console.log('Server: ' + jsonString);
			};	
					
		}
	
	});
	
});