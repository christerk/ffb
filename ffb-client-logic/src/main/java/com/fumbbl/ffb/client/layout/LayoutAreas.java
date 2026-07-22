package com.fumbbl.ffb.client.layout;

import com.fumbbl.ffb.client.ClientLayout;

import java.awt.Dimension;
import java.awt.Rectangle;

class LayoutAreas {

	private static final int LOG_CHAT_GAP = 2;
	private static final int PANEL_BORDER = 1;

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

	static LayoutAreas arrange(ClientLayout layout, Rectangle content, int railWidth, Dimension score, Dimension log,
		Dimension chat) {
		switch (layout) {
			case PORTRAIT:
				return portrait(content, railWidth, bottomDockSize(score, log, chat));
			case SQUARE:
				return square(content, railWidth, rightDockSize(score, log, chat));
			default:
				return landscape(content, railWidth, bottomDockSize(score, log, chat));
		}
	}

	static Dimension naturalSize(ClientLayout layout, Dimension rail, Dimension pitch, Dimension score, Dimension log,
		Dimension chat) {
		switch (layout) {
			case PORTRAIT:
				return portraitNaturalSize(rail, pitch, bottomDockSize(score, log, chat));
			case SQUARE:
				return squareNaturalSize(rail, pitch, rightDockSize(score, log, chat));
			default:
				return landscapeNaturalSize(rail, pitch, bottomDockSize(score, log, chat));
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

	Rectangle finalDockBounds(Rectangle pitchBounds) {
		if (dockPosition == DockPosition.BOTTOM) {
			return new Rectangle(dock.x, pitchBounds.y + pitchBounds.height, dock.width, dock.height);
		}
		return new Rectangle(dock);
	}

	private static Dimension bottomDockSize(Dimension score, Dimension log, Dimension chat) {
		Dimension logChat = logChatSize(log, chat);
		return new Dimension(Math.max(score.width, logChat.width), score.height + logChat.height);
	}

	private static Dimension rightDockSize(Dimension score, Dimension log, Dimension chat) {
		return new Dimension(Math.max(log.width, Math.max(score.width, chat.width)) + (2 * PANEL_BORDER),
			log.height + score.height + chat.height + (2 * PANEL_BORDER));
	}

	private static Dimension logChatSize(Dimension log, Dimension chat) {
		return new Dimension(log.width + LOG_CHAT_GAP + chat.width + (2 * PANEL_BORDER),
			Math.max(log.height, chat.height) + (2 * PANEL_BORDER));
	}
}
