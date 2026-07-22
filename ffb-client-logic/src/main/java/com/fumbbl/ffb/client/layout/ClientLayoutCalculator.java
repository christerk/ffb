package com.fumbbl.ffb.client.layout;

import com.fumbbl.ffb.client.Component;
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
		Dimension bottomDockSize = bottomPanelSize(score, log, chat);
		Dimension rightDockSize = rightColumnSize(score, log, chat);

		Rectangle content = new Rectangle(0, 0, availableSize.width, availableSize.height);
		LayoutAreas areas = LayoutAreas.arrange(layoutSettings.getLayout(), content, sidebar.width, bottomDockSize, rightDockSize);
		PitchFit pitchFit = fitPitch(areas.pitchArea, pitch);
		Rectangle dock = areas.dock;
		if (areas.dockPosition == LayoutAreas.DockPosition.BOTTOM) {
			dock = new Rectangle(dock.x, pitchFit.bounds.y + pitchFit.bounds.height, dock.width, dock.height);
		}

		return new ClientLayoutResult(new Dimension(availableSize), pitchFit.bounds, areas.homeRail,
			new Rectangle(areas.homeRail.x, areas.homeRail.y, reserveBox.width, reserveBox.height), areas.awayRail,
			scoreBounds(dock, areas.dockPosition, score, log), logBounds(dock, areas.dockPosition, score, log, chat),
			chatBounds(dock, areas.dockPosition, score, log, chat), pitchFit.scale, layoutSettings.getGuiScale());
	}

	private PitchFit fitPitch(Rectangle pitchArea, Dimension pitch) {
		double scale = Math.min((double) pitchArea.width / pitch.width, (double) pitchArea.height / pitch.height);
		int pitchWidth = scaled(pitch.width, scale);
		int pitchHeight = scaled(pitch.height, scale);
		int pitchX = pitchArea.x + ((pitchArea.width - pitchWidth) / 2);
		int pitchY = pitchArea.y;
		return new PitchFit(new Rectangle(pitchX, pitchY, pitchWidth, pitchHeight), scale);
	}

	private Rectangle scoreBounds(Rectangle dock, LayoutAreas.DockPosition dockPosition, Dimension score, Dimension log) {
		if (dockPosition == LayoutAreas.DockPosition.RIGHT) {
			return new Rectangle(dock.x + PANEL_BORDER, dock.y + log.height + PANEL_BORDER, score.width, score.height);
		}
		return new Rectangle(dock.x + ((dock.width - score.width) / 2), dock.y, score.width, score.height);
	}

	private Rectangle logBounds(Rectangle dock, LayoutAreas.DockPosition dockPosition, Dimension score, Dimension log, Dimension chat) {
		if (dockPosition == LayoutAreas.DockPosition.RIGHT) {
			return new Rectangle(dock.x + PANEL_BORDER, dock.y + PANEL_BORDER, log.width, log.height);
		}
		int logChatWidth = logChatPanelSize(log, chat).width;
		return new Rectangle(dock.x + ((dock.width - logChatWidth) / 2) + PANEL_BORDER,
			dock.y + score.height + PANEL_BORDER, log.width, log.height);
	}

	private Rectangle chatBounds(Rectangle dock, LayoutAreas.DockPosition dockPosition, Dimension score, Dimension log, Dimension chat) {
		if (dockPosition == LayoutAreas.DockPosition.RIGHT) {
			return new Rectangle(dock.x + PANEL_BORDER, dock.y + log.height + score.height + PANEL_BORDER, chat.width, chat.height);
		}
		Rectangle logRectangle = logBounds(dock, LayoutAreas.DockPosition.BOTTOM, score, log, chat);
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
		return LayoutAreas.naturalSize(layoutSettings.getLayout(), sidebar, pitch, bottomPanelSize(score, log, chat),
			rightColumnSize(score, log, chat));
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
