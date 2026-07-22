package com.fumbbl.ffb.client.layout;

import com.fumbbl.ffb.client.Component;
import com.fumbbl.ffb.client.LayoutSettings;

import java.awt.Dimension;
import java.awt.Rectangle;

/**
 * Calculates the client layout for one content size.
 *
 * This class resolves configured component dimensions, arranges the major
 * layout areas, fits the pitch into its available area, and returns the bounds
 * consumed by Swing components and viewports.
 */

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

	private static class DockPanels {

		private final Rectangle score;
		private final Rectangle log;
		private final Rectangle chat;

		private DockPanels(Rectangle score, Rectangle log, Rectangle chat) {
			this.score = score;
			this.log = log;
			this.chat = chat;
		}
	}

	public ClientLayoutResult calculate(LayoutSettings layoutSettings, Dimension availableSize) {
		Dimension sidebar = dimension(layoutSettings, Component.SIDEBAR);
		Dimension reserveBox = dimension(layoutSettings, Component.BOX);
		Dimension score = dimension(layoutSettings, Component.SCORE_BOARD);
		Dimension log = dimension(layoutSettings, Component.LOG);
		Dimension chat = dimension(layoutSettings, Component.CHAT);
		Dimension pitch = unscaledDimension(layoutSettings, Component.FIELD);

		Rectangle content = new Rectangle(0, 0, availableSize.width, availableSize.height);
		LayoutAreas areas = LayoutAreas.arrange(layoutSettings.getLayout(), content, sidebar.width, score, log, chat);
		PitchFit pitchFit = fitPitch(areas.pitchArea, pitch);
		Rectangle dock = areas.finalDockBounds(pitchFit.bounds);
		DockPanels dockPanels = placeDockPanels(areas.dockPosition, dock, score, log, chat);

		return new ClientLayoutResult(new Dimension(availableSize), pitchFit.bounds, areas.homeRail,
			new Rectangle(areas.homeRail.x, areas.homeRail.y, reserveBox.width, reserveBox.height), areas.awayRail,
			dockPanels.score, dockPanels.log, dockPanels.chat, pitchFit.scale, layoutSettings.getGuiScale());
	}

	private DockPanels placeDockPanels(LayoutAreas.DockPosition dockPosition, Rectangle dock, Dimension score, Dimension log,
		Dimension chat) {
		if (dockPosition == LayoutAreas.DockPosition.RIGHT) {
			Rectangle logBounds = new Rectangle(dock.x + PANEL_BORDER, dock.y + PANEL_BORDER, log.width, log.height);
			Rectangle scoreBounds = new Rectangle(dock.x + PANEL_BORDER, dock.y + log.height + PANEL_BORDER,
				score.width, score.height);
			Rectangle chatBounds = new Rectangle(dock.x + PANEL_BORDER, dock.y + log.height + score.height + PANEL_BORDER,
				chat.width, chat.height);
			return new DockPanels(scoreBounds, logBounds, chatBounds);
		}

		Rectangle scoreBounds = new Rectangle(dock.x + ((dock.width - score.width) / 2), dock.y, score.width,
			score.height);
		int logChatWidth = log.width + LOG_CHAT_GAP + chat.width + (2 * PANEL_BORDER);
		Rectangle logBounds = new Rectangle(dock.x + ((dock.width - logChatWidth) / 2) + PANEL_BORDER,
			dock.y + score.height + PANEL_BORDER, log.width, log.height);
		Rectangle chatBounds = new Rectangle(logBounds.x + logBounds.width + LOG_CHAT_GAP, logBounds.y, chat.width,
			chat.height);
		return new DockPanels(scoreBounds, logBounds, chatBounds);
	}

	private PitchFit fitPitch(Rectangle pitchArea, Dimension pitch) {
		double scale = Math.min((double) pitchArea.width / pitch.width, (double) pitchArea.height / pitch.height);
		int pitchWidth = scaled(pitch.width, scale);
		int pitchHeight = scaled(pitch.height, scale);
		int pitchX = pitchArea.x + ((pitchArea.width - pitchWidth) / 2);
		int pitchY = pitchArea.y;
		return new PitchFit(new Rectangle(pitchX, pitchY, pitchWidth, pitchHeight), scale);
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
		return LayoutAreas.naturalSize(layoutSettings.getLayout(), sidebar, pitch, score, log, chat);
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
