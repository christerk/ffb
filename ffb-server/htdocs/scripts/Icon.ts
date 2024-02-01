export default class Icon {

	name: string = null;
	url: string = null;
	image: HTMLImageElement = null;
	loaded: boolean = false;

	constructor(name: string, url: string) {
		this.name = name;
		this.url = url;
	}

	load(callback): void {
		if (!this.loaded) {
			this.image = new Image();
			this.image.onload = () => {
				this.loaded = true;
				console.log('loaded ' + this.name);
				if (callback) {
					callback();
				}
			}
			this.image.src = this.url;
		}
	}
	
}