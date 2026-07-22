package com.fumbbl.ffb.client.layout;

import java.awt.Dimension;
import java.awt.Rectangle;

class PortraitLayout {

	static LayoutAreas arrange(Rectangle content, int railWidth, Dimension dockSize) {
		int gameHeight = Math.max(1, content.height - dockSize.height);
		Rectangle gameArea = new Rectangle(content.x, content.y, content.width, gameHeight);

		return new LayoutAreas(
			new Rectangle(gameArea.x, gameArea.y, railWidth, gameArea.height),
			new Rectangle(gameArea.x + gameArea.width - railWidth, gameArea.y, railWidth, gameArea.height),
			new Rectangle(gameArea.x + railWidth, gameArea.y, Math.max(1, gameArea.width - (2 * railWidth)), gameArea.height),
			new Rectangle(content.x, content.y + gameHeight, content.width, dockSize.height)
		);
	}

	static Dimension naturalSize(Dimension rail, Dimension pitch, Dimension dock) {
		int gameWidth = rail.width + pitch.width + rail.width;
		int gameHeight = Math.max(rail.height, pitch.height);
		return new Dimension(Math.max(gameWidth, dock.width), gameHeight + dock.height);
	}
}
