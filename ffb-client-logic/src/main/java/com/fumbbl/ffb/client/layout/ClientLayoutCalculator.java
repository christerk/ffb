package com.fumbbl.ffb.client.layout;

import com.fumbbl.ffb.client.Component;
import com.fumbbl.ffb.client.ClientLayout;
import com.fumbbl.ffb.client.LayoutSettings;

import java.awt.Dimension;
import java.awt.Rectangle;

public class ClientLayoutCalculator {

	private static final int LOG_CHAT_GAP = 2;
	private static final int PANEL_BORDER = 1;

	private static class PitchFit {

		private final Rectangle bounds;
		private final double scale;

		private PitchFit(Rectangle bounds, double scale) {
			this.bounds = bounds;
			this.scale = scale;
		}
	}

	public ClientLayoutResult calculate(LayoutSettings layoutSettings, Dimension availableSize) {
		Dimension sidebar = dimension(layoutSettings, Component.SIDEBAR);
		Dimension reserveBox = dimension(layoutSettings, Component.BOX);
		Dimension score = dimension(layoutSettings, Component.SCORE_BOARD);
		Dimension log = dimension(layoutSettings, Component.LOG);
		Dimension chat = dimension(layoutSettings, Component.CHAT);
		Dimension pitch = unscaledDimension(layoutSettings, Component.FIELD);
		boolean sideDock = layoutSettings.getLayout() == ClientLayout.SQUARE;
		Dimension dockSize = sideDock ? rightColumnSize(score, log, chat) : bottomPanelSize(score, log, chat);

		Rectangle content = new Rectangle(0, 0, availableSize.width, availableSize.height);
		LayoutAreas areas = arrange(layoutSettings.getLayout(), content, sidebar.width, dockSize);
		PitchFit pitchFit = fitPitch(areas.pitchArea, pitch);

		return new ClientLayoutResult(new Dimension(availableSize), pitchFit.bounds, areas.homeRail,
			new Rectangle(areas.homeRail.x, areas.homeRail.y, reserveBox.width, reserveBox.height), areas.awayRail,
			scoreBounds(areas.dock, sideDock, score, log), logBounds(areas.dock, sideDock, score, log, chat),
			chatBounds(areas.dock, sideDock, score, log, chat), pitchFit.scale, layoutSettings.getGuiScale());
	}

	private LayoutAreas arrange(ClientLayout layout, Rectangle content, int railWidth, Dimension dockSize) {
		switch (layout) {
			case PORTRAIT:
				return PortraitLayout.arrange(content, railWidth, dockSize);
			case SQUARE:
				return SquareLayout.arrange(content, railWidth, dockSize);
			default:
				return LandscapeLayout.arrange(content, railWidth, dockSize);
		}
	}

	private PitchFit fitPitch(Rectangle pitchArea, Dimension pitch) {
		double scale = Math.min((double) pitchArea.width / pitch.width, (double) pitchArea.height / pitch.height);
		int pitchWidth = scaled(pitch.width, scale);
		int pitchHeight = scaled(pitch.height, scale);
		int pitchX = pitchArea.x + ((pitchArea.width - pitchWidth) / 2);
		int pitchY = pitchArea.y + ((pitchArea.height - pitchHeight) / 2);
		return new PitchFit(new Rectangle(pitchX, pitchY, pitchWidth, pitchHeight), scale);
	}

	private Rectangle scoreBounds(Rectangle dock, boolean sideDock, Dimension score, Dimension log) {
		if (sideDock) {
			return new Rectangle(dock.x + PANEL_BORDER, dock.y + log.height + PANEL_BORDER, score.width, score.height);
		}
		return new Rectangle(dock.x + ((dock.width - score.width) / 2), dock.y, score.width, score.height);
	}

	private Rectangle logBounds(Rectangle dock, boolean sideDock, Dimension score, Dimension log, Dimension chat) {
		if (sideDock) {
			return new Rectangle(dock.x + PANEL_BORDER, dock.y + PANEL_BORDER, log.width, log.height);
		}
		int logChatWidth = logChatPanelSize(log, chat).width;
		return new Rectangle(dock.x + ((dock.width - logChatWidth) / 2) + PANEL_BORDER,
			dock.y + score.height + PANEL_BORDER, log.width, log.height);
	}

	private Rectangle chatBounds(Rectangle dock, boolean sideDock, Dimension score, Dimension log, Dimension chat) {
		if (sideDock) {
			return new Rectangle(dock.x + PANEL_BORDER, dock.y + log.height + score.height + PANEL_BORDER, chat.width, chat.height);
		}
		Rectangle logRectangle = logBounds(dock, false, score, log, chat);
		return new Rectangle(logRectangle.x + logRectangle.width + LOG_CHAT_GAP, logRectangle.y, chat.width, chat.height);
	}

	private Dimension logChatPanelSize(Dimension log, Dimension chat) {
		return new Dimension(
			log.width + LOG_CHAT_GAP + chat.width + (2 * PANEL_BORDER),
			Math.max(log.height, chat.height) + (2 * PANEL_BORDER)
		);
	}

	private Dimension rightColumnSize(Dimension score, Dimension log, Dimension chat) {
		return new Dimension(
			Math.max(log.width, Math.max(score.width, chat.width)) + (2 * PANEL_BORDER),
			log.height + score.height + chat.height + (2 * PANEL_BORDER)
		);
	}

	private Dimension bottomPanelSize(Dimension score, Dimension log, Dimension chat) {
		Dimension logChatPanel = logChatPanelSize(log, chat);
		return new Dimension(
			Math.max(score.width, logChatPanel.width),
			score.height + logChatPanel.height
		);
	}

	private int scaled(int size, double scale) {
		return (int) (size * scale);
	}

	public Dimension naturalContentSize(LayoutSettings layoutSettings) {
		Dimension sidebar = dimension(layoutSettings, Component.SIDEBAR);
		Dimension score = dimension(layoutSettings, Component.SCORE_BOARD);
		Dimension log = dimension(layoutSettings, Component.LOG);
		Dimension chat = dimension(layoutSettings, Component.CHAT);
		Dimension pitch = scale(unscaledDimension(layoutSettings, Component.FIELD), layoutSettings.getPitchScale());
		if (layoutSettings.getLayout() == ClientLayout.SQUARE) {
			Dimension dock = rightColumnSize(score, log, chat);
			return SquareLayout.naturalSize(sidebar, pitch, dock);
		}

		Dimension dock = bottomPanelSize(score, log, chat);
		if (layoutSettings.getLayout().isPortrait()) {
			return PortraitLayout.naturalSize(sidebar, pitch, dock);
		}
		return LandscapeLayout.naturalSize(sidebar, pitch, dock);
	}

	private Dimension dimension(LayoutSettings layoutSettings, Component component) {
		return scale(unscaledDimension(layoutSettings, component), layoutSettings.getGuiScale());
	}

	private Dimension unscaledDimension(LayoutSettings layoutSettings, Component component) {
		return component.dimension(layoutSettings.getLayout());
	}

	private Dimension scale(Dimension dimension, double scale) {
		return new Dimension(scaled(dimension.width, scale), scaled(dimension.height, scale));
	}
}
