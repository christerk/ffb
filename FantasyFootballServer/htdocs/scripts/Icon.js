define(function () {
	'use strict';

	return function(name, url) {
		
		this.name = name;
		this.url = url;
		this.image = null;
		this.loaded = false;
		
		this.load = function(callback) {
			if (!this.loaded) {
				this.image = new Image();
				var myIcon = this;
				this.image.onload = function() {
	    			myIcon.loaded = true;
	    			// console.log('loaded ' + that.name);
	    			if (callback) {
	    				callback();
	    			}
	    		}
	    		this.image.src = this.url;
			}
		}
		
	};
	
});