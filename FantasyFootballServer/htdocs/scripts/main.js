require(['lib/domReady', 'lib/lzString', 'iconCache'], function (domReady, lzString, iconCache) {
	'use strict';
	
	// This function is called once the DOM is ready.
	// It will be safe to query the DOM and manipulate DOM nodes in this function.
	domReady(function () {
		
		var ctxClient = document.getElementById('cvsClient').getContext("2d");
		var ctxPitch = document.getElementById('cvsPitch').getContext("2d");
		var ctxPlayers = document.getElementById('cvsPlayers').getContext("2d");

		iconCache.init(init);
		
		function drawImage(ctx, name, pos) {
			if (ctx && name && pos) {
				var img = iconCache.getImg(name);
				if (img) {
					ctx.drawImage(img, pos.x, pos.y, img.width, img.height);
				}
			}
		}
		
		function init() {
			// console.log('init');
			// fade-out via transition
			document.getElementById('divLoading').style.opacity = 0; 
			// transition delayed to after fade-out
			document.getElementById('divLoading').style.width = 0;
			document.getElementById('divLoading').style.height = 0;
			// init client
			drawImage(ctxClient, 'playerDetailsRed', { x:0, y:0 });
			drawImage(ctxClient, 'boxButtonsRed', { x:0, y:430 });
			drawImage(ctxClient, 'turnDiceStatusRed', { x:0, y:452 });
			drawImage(ctxClient, 'resourcesRed', { x:0, y:544 });
			drawImage(ctxClient, 'scorebar', { x:116, y:452 });
			drawImage(ctxClient, 'playerDetailsBlue', { x:897, y:0 });
			drawImage(ctxClient, 'boxButtonsBlue', { x:897, y:430 });
			drawImage(ctxClient, 'turnDiceStatusBlue', { x:897, y:452 });
			drawImage(ctxClient, 'resourcesBlue', { x:897, y:544 });
			// init pitch layer
			drawImage(ctxPitch, 'pitch', { x:0, y:0 });
			// init player layer
			ctxPlayers.clearRect(0, 0, 782, 452);
			// init chat input
			document.getElementById('inputChat').onkeypress = function(e) {
			    var event = e || window.event;
			    var charCode = event.which || event.keyCode;
			    if (charCode == '13') {
			    	var inputChat = document.getElementById('inputChat');
			    	addToChat(inputChat.value);
			    	inputChat.value = '';
			    	return false;
			    }
			}
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
			var img = iconCache.getImg('amblitzer1');
			if (xInc > 0) {
				ctxPlayers.drawImage(img, 1 * 28, 0, 28, 28, xPos, yPos, 28, 28);
			} else {
				ctxPlayers.drawImage(img, 3 * 28, 0, 28, 28, xPos, yPos, 28, 28);
			}
		}
		
		function connect() {
			
			var command = { "netCommandId" : "clientRequestVersion" };
			
			var connection = new WebSocket("ws://localhost:2224/command");
			
			connection.onopen = function() {
				addToLog('Connection open');
				var jsonString = JSON.stringify(command);
				connection.send(lzString.compressToUTF16(jsonString));
			};
			
			connection.onclose = function() {
				addToLog('Connection closed');
			};
			
			connection.onerror = function(error) {
				addToLog('WebSocket Error: ' + error);
			};
			
			connection.onmessage = function(message) {
				var jsonString = lzString.decompressFromUTF16(message.data);
				addToLog('Server: ' + jsonString);
			};	
					
		}
		
		function addToLog(message) {
			if (message) {
				var divLog = document.getElementById('divLog');
				divLog.insertAdjacentHTML('beforeend', '<div class="line">' + message + '</div>');
			}
		}
		
		function addToChat(message) {
			if (message) {
				var divChat = document.getElementById('divChat');
				divChat.insertAdjacentHTML('beforeend', '<div class="line">' + message + '</div>');
			}
		}

	});
	
});