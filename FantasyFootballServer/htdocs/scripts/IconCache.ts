import Icon from './Icon';

export default class IconCache {

	private icons: { [id: string]: Icon } = { };
	
	constructor() {
	}

	hasUnloadedIcons(): boolean {
		let name: string;
		for (name in this.icons) {
            if (this.icons.hasOwnProperty(name)) {
            	if (!this.icons[name].loaded) {
            		return true;
            	}
            }
		}
		return false;
	}

	init(callback: Function): void {
			
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
		
	}

	addIcon(name: string, url: string): void {
		this.icons[name] = new Icon(name, url);
	}

	getImg(name: string): HTMLImageElement {
		return this.icons[name] ? this.icons[name].image : null;
	}
	
	refresh(callback: Function): void {
		console.log('refresh');
		var name: string,
			myself = this;
		for (name in this.icons) {
			if (this.icons.hasOwnProperty(name)) {
				this.icons[name].load(function() {
					if (!myself.hasUnloadedIcons() && callback) {
						callback();
					}
				});
			}
		}
	}
	
}