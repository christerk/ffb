package com.fumbbl.ffb.client.layout;

import com.fumbbl.ffb.client.ClientLayout;

import java.awt.Dimension;
import java.awt.Rectangle;

/**
 * Describes the major regions of a client layout.
 *
 * This class owns layout topology: side rails, pitch area, and panel area.
 * It does not place individual Swing components inside those regions.
 */

class LayoutAreas {

	private static final int LOG_CHAT_GAP = 2;
	private static final int PANEL_BORDER = 1;

	enum InfoPosition {
		BOTTOM, RIGHT
	}

	final Rectangle homeRail;
	final Rectangle awayRail;
	final Rectangle pitchArea;
	final Rectangle infoArea;
	final InfoPosition infoPosition;

	private LayoutAreas(Rectangle homeRail, Rectangle awayRail, Rectangle pitchArea, Rectangle infoArea,
		InfoPosition infoPosition) {
		this.homeRail = homeRail;
		this.awayRail = awayRail;
		this.pitchArea = pitchArea;
		this.infoArea = infoArea;
		this.infoPosition = infoPosition;
	}

	static LayoutAreas arrange(ClientLayout layout, Rectangle content, int railWidth, Dimension score, Dimension log,
		Dimension chat) {
		switch (layout) {
			case PORTRAIT:
				return portrait(content, railWidth, bottomInfoSize(score, log, chat));
			case SQUARE:
				return square(content, railWidth, rightInfoSize(score, log, chat));
			default:
				return landscape(content, railWidth, bottomInfoSize(score, log, chat));
		}
	}

	static Dimension naturalSize(ClientLayout layout, Dimension rail, Dimension pitch, Dimension score, Dimension log,
		Dimension chat) {
		switch (layout) {
			case PORTRAIT:
				return portraitNaturalSize(rail, pitch, bottomInfoSize(score, log, chat));
			case SQUARE:
				return squareNaturalSize(rail, pitch, rightInfoSize(score, log, chat));
			default:
				return landscapeNaturalSize(rail, pitch, bottomInfoSize(score, log, chat));
		}
	}

	private static LayoutAreas landscape(Rectangle content, int railWidth, Dimension infoSize) {
		Rectangle centerColumn = new Rectangle(content.x + railWidth, content.y,
			Math.max(1, content.width - (2 * railWidth)), content.height);
		int pitchAreaHeight = Math.max(1, centerColumn.height - infoSize.height);

		return new LayoutAreas(
			new Rectangle(content.x, content.y, railWidth, content.height),
			new Rectangle(content.x + content.width - railWidth, content.y, railWidth, content.height),
			new Rectangle(centerColumn.x, centerColumn.y, centerColumn.width, pitchAreaHeight),
			new Rectangle(centerColumn.x, centerColumn.y + pitchAreaHeight, centerColumn.width, infoSize.height),
			InfoPosition.BOTTOM
		);
	}

	private static LayoutAreas portrait(Rectangle content, int railWidth, Dimension infoSize) {
		int gameHeight = Math.max(1, content.height - infoSize.height);
		Rectangle gameArea = new Rectangle(content.x, content.y, content.width, gameHeight);

		return new LayoutAreas(
			new Rectangle(gameArea.x, gameArea.y, railWidth, gameArea.height),
			new Rectangle(gameArea.x + gameArea.width - railWidth, gameArea.y, railWidth, gameArea.height),
			new Rectangle(gameArea.x + railWidth, gameArea.y, Math.max(1, gameArea.width - (2 * railWidth)), gameArea.height),
			new Rectangle(content.x, content.y + gameHeight, content.width, infoSize.height),
			InfoPosition.BOTTOM
		);
	}

	private static LayoutAreas square(Rectangle content, int railWidth, Dimension infoSize) {
		int gameWidth = Math.max(1, content.width - infoSize.width);
		Rectangle gameArea = new Rectangle(content.x, content.y, gameWidth, content.height);

		return new LayoutAreas(
			new Rectangle(gameArea.x, gameArea.y, railWidth, gameArea.height),
			new Rectangle(gameArea.x + gameArea.width - railWidth, gameArea.y, railWidth, gameArea.height),
			new Rectangle(gameArea.x + railWidth, gameArea.y, Math.max(1, gameArea.width - (2 * railWidth)), gameArea.height),
			new Rectangle(content.x + gameWidth, content.y, infoSize.width, content.height),
			InfoPosition.RIGHT
		);
	}

	private static Dimension landscapeNaturalSize(Dimension rail, Dimension pitch, Dimension infoSize) {
		int centerWidth = Math.max(pitch.width, infoSize.width);
		int centerHeight = pitch.height + infoSize.height;
		return new Dimension(rail.width + centerWidth + rail.width, Math.max(rail.height, centerHeight));
	}

	private static Dimension portraitNaturalSize(Dimension rail, Dimension pitch, Dimension infoSize) {
		int gameWidth = rail.width + pitch.width + rail.width;
		int gameHeight = Math.max(rail.height, pitch.height);
		return new Dimension(Math.max(gameWidth, infoSize.width), gameHeight + infoSize.height);
	}

	private static Dimension squareNaturalSize(Dimension rail, Dimension pitch, Dimension infoSize) {
		int gameWidth = rail.width + pitch.width + rail.width;
		int gameHeight = Math.max(rail.height, pitch.height);
		return new Dimension(gameWidth + infoSize.width, Math.max(gameHeight, infoSize.height));
	}

	Rectangle finalInfoArea(Rectangle pitchBounds) {
		if (infoPosition == InfoPosition.BOTTOM) {
			return new Rectangle(infoArea.x, pitchBounds.y + pitchBounds.height, infoArea.width, infoArea.height);
		}
		return new Rectangle(infoArea);
	}

	private static Dimension bottomInfoSize(Dimension score, Dimension log, Dimension chat) {
		Dimension logChat = logChatSize(log, chat);
		return new Dimension(Math.max(score.width, logChat.width), score.height + logChat.height);
	}

	private static Dimension rightInfoSize(Dimension score, Dimension log, Dimension chat) {
		return new Dimension(Math.max(log.width, Math.max(score.width, chat.width)) + (2 * PANEL_BORDER),
			log.height + score.height + chat.height + (2 * PANEL_BORDER));
	}

	private static Dimension logChatSize(Dimension log, Dimension chat) {
		return new Dimension(log.width + LOG_CHAT_GAP + chat.width + (2 * PANEL_BORDER),
			Math.max(log.height, chat.height) + (2 * PANEL_BORDER));
	}
}
