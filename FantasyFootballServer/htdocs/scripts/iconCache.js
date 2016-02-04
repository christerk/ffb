define(['Icon'], function(Icon) {
	'use strict';

	var icons = { };
	
	function hasUnloadedIcons() {
		var name;
		for (name in icons) {
            if (icons.hasOwnProperty(name)) {
            	if (!icons[name].loaded) {
            		return true;
            	}
            }
		}
		return false;
	}

	return {
		
		init: function(callback) {
			
			this.addIcon('playerDetailsRed',   '/icons/client/bg_player_details_red.png');
			this.addIcon('boxButtonsRed',      '/icons/client/bg_box_buttons_red.png');
			this.addIcon('turnDiceStatusRed',  '/icons/client/bg_turn_dice_status_red.png');
			this.addIcon('resourcesRed',       '/icons/client/bg_resources_red.png');
			this.addIcon('scorebar',           '/icons/client/bg_scorebar.png');
			this.addIcon('playerDetailsBlue',  '/icons/client/bg_player_details_blue.png');
			this.addIcon('boxButtonsBlue',     '/icons/client/bg_box_buttons_blue.png');
			this.addIcon('turnDiceStatusBlue', '/icons/client/bg_turn_dice_status_blue.png');
			this.addIcon('resourcesBlue',      '/icons/client/bg_resources_blue.png');
			this.addIcon('pitch',              '/icons/pitches/default/nice.jpg');
			this.addIcon('amblitzer1',         '/icons/players/iconsets/amazon_blitzer.png');

			this.refresh(callback);
			
		},

		addIcon: function(name, url) {
			var icon = new Icon(name, url);
			icons[name] = icon;
		},

		getImg: function(name) {
			var icon = icons[name];
			return icon ? icon.image : null;
		},
		
		refresh: function(callback) {
			var name;
			for (name in icons) {
	            if (icons.hasOwnProperty(name)) {
            		icons[name].load(function() {
            			if (!hasUnloadedIcons() && callback) {
            				callback();
            			}
        			});
	            }
			}
		}
		
	};
	
});