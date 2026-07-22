package com.fumbbl.ffb.client.layout;

import com.fumbbl.ffb.client.ClientLayout;

import java.awt.Dimension;
import java.awt.Rectangle;

class LayoutAreas {

	enum DockPosition {
		BOTTOM, RIGHT
	}

	final Rectangle homeRail;
	final Rectangle awayRail;
	final Rectangle pitchArea;
	final Rectangle dock;
	final DockPosition dockPosition;

	private LayoutAreas(Rectangle homeRail, Rectangle awayRail, Rectangle pitchArea, Rectangle dock,
		DockPosition dockPosition) {
		this.homeRail = homeRail;
		this.awayRail = awayRail;
		this.pitchArea = pitchArea;
		this.dock = dock;
		this.dockPosition = dockPosition;
	}

	static LayoutAreas arrange(ClientLayout layout, Rectangle content, int railWidth, Dimension bottomDockSize,
		Dimension rightDockSize) {
		switch (layout) {
			case PORTRAIT:
				return portrait(content, railWidth, bottomDockSize);
			case SQUARE:
				return square(content, railWidth, rightDockSize);
			default:
				return landscape(content, railWidth, bottomDockSize);
		}
	}

	static Dimension naturalSize(ClientLayout layout, Dimension rail, Dimension pitch, Dimension bottomDock,
		Dimension rightDock) {
		switch (layout) {
			case PORTRAIT:
				return portraitNaturalSize(rail, pitch, bottomDock);
			case SQUARE:
				return squareNaturalSize(rail, pitch, rightDock);
			default:
				return landscapeNaturalSize(rail, pitch, bottomDock);
		}
	}

	private static LayoutAreas landscape(Rectangle content, int railWidth, Dimension dockSize) {
		Rectangle centerColumn = new Rectangle(content.x + railWidth, content.y,
			Math.max(1, content.width - (2 * railWidth)), content.height);
		int pitchAreaHeight = Math.max(1, centerColumn.height - dockSize.height);

		return new LayoutAreas(
			new Rectangle(content.x, content.y, railWidth, content.height),
			new Rectangle(content.x + content.width - railWidth, content.y, railWidth, content.height),
			new Rectangle(centerColumn.x, centerColumn.y, centerColumn.width, pitchAreaHeight),
			new Rectangle(centerColumn.x, centerColumn.y + pitchAreaHeight, centerColumn.width, dockSize.height),
			DockPosition.BOTTOM
		);
	}

	private static LayoutAreas portrait(Rectangle content, int railWidth, Dimension dockSize) {
		int gameHeight = Math.max(1, content.height - dockSize.height);
		Rectangle gameArea = new Rectangle(content.x, content.y, content.width, gameHeight);

		return new LayoutAreas(
			new Rectangle(gameArea.x, gameArea.y, railWidth, gameArea.height),
			new Rectangle(gameArea.x + gameArea.width - railWidth, gameArea.y, railWidth, gameArea.height),
			new Rectangle(gameArea.x + railWidth, gameArea.y, Math.max(1, gameArea.width - (2 * railWidth)), gameArea.height),
			new Rectangle(content.x, content.y + gameHeight, content.width, dockSize.height),
			DockPosition.BOTTOM
		);
	}

	private static LayoutAreas square(Rectangle content, int railWidth, Dimension dockSize) {
		int gameWidth = Math.max(1, content.width - dockSize.width);
		Rectangle gameArea = new Rectangle(content.x, content.y, gameWidth, content.height);

		return new LayoutAreas(
			new Rectangle(gameArea.x, gameArea.y, railWidth, gameArea.height),
			new Rectangle(gameArea.x + gameArea.width - railWidth, gameArea.y, railWidth, gameArea.height),
			new Rectangle(gameArea.x + railWidth, gameArea.y, Math.max(1, gameArea.width - (2 * railWidth)), gameArea.height),
			new Rectangle(content.x + gameWidth, content.y, dockSize.width, content.height),
			DockPosition.RIGHT
		);
	}

	private static Dimension landscapeNaturalSize(Dimension rail, Dimension pitch, Dimension dock) {
		int centerWidth = Math.max(pitch.width, dock.width);
		int centerHeight = pitch.height + dock.height;
		return new Dimension(rail.width + centerWidth + rail.width, Math.max(rail.height, centerHeight));
	}

	private static Dimension portraitNaturalSize(Dimension rail, Dimension pitch, Dimension dock) {
		int gameWidth = rail.width + pitch.width + rail.width;
		int gameHeight = Math.max(rail.height, pitch.height);
		return new Dimension(Math.max(gameWidth, dock.width), gameHeight + dock.height);
	}

	private static Dimension squareNaturalSize(Dimension rail, Dimension pitch, Dimension dock) {
		int gameWidth = rail.width + pitch.width + rail.width;
		int gameHeight = Math.max(rail.height, pitch.height);
		return new Dimension(gameWidth + dock.width, Math.max(gameHeight, dock.height));
	}
}
