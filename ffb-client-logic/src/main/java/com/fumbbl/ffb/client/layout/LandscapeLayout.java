package com.fumbbl.ffb.client.layout;

import java.awt.Dimension;
import java.awt.Rectangle;

class LandscapeLayout {

	static LayoutAreas arrange(Rectangle content, int railWidth, Dimension dockSize) {
		Rectangle centerColumn = new Rectangle(content.x + railWidth, content.y,
			Math.max(1, content.width - (2 * railWidth)), content.height);
		int pitchAreaHeight = Math.max(1, centerColumn.height - dockSize.height);

		return new LayoutAreas(
			new Rectangle(content.x, content.y, railWidth, content.height),
			new Rectangle(content.x + content.width - railWidth, content.y, railWidth, content.height),
			new Rectangle(centerColumn.x, centerColumn.y, centerColumn.width, pitchAreaHeight),
			new Rectangle(centerColumn.x, centerColumn.y + pitchAreaHeight, centerColumn.width, dockSize.height)
		);
	}

	static Dimension naturalSize(Dimension rail, Dimension pitch, Dimension dock) {
		int centerWidth = Math.max(pitch.width, dock.width);
		int centerHeight = pitch.height + dock.height;
		return new Dimension(rail.width + centerWidth + rail.width, Math.max(rail.height, centerHeight));
	}
}
