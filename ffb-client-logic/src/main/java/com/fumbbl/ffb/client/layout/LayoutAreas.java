package com.fumbbl.ffb.client.layout;

import java.awt.Rectangle;

class LayoutAreas {

	final Rectangle homeRail;
	final Rectangle awayRail;
	final Rectangle pitchArea;
	final Rectangle dock;

	LayoutAreas(Rectangle homeRail, Rectangle awayRail, Rectangle pitchArea, Rectangle dock) {
		this.homeRail = homeRail;
		this.awayRail = awayRail;
		this.pitchArea = pitchArea;
		this.dock = dock;
	}
}
