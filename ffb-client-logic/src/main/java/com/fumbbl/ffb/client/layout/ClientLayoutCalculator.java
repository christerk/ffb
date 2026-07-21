package com.fumbbl.ffb.client.layout;

import com.fumbbl.ffb.client.ClientLayout;
import com.fumbbl.ffb.client.Component;
import com.fumbbl.ffb.client.LayoutSettings;

import java.awt.Dimension;
import java.awt.Rectangle;

public class ClientLayoutCalculator {

	private static final int LOG_CHAT_GAP = 2;
	private static final int PANEL_BORDER = 1;

	private static class PitchPlacement {

		private final Rectangle bounds;
		private final double scale;

		private PitchPlacement(Rectangle bounds, double scale) {
			this.bounds = bounds;
			this.scale = scale;
		}
	}

	public ClientLayoutResult calculate(LayoutSettings layoutSettings, Dimension availableSize) {
		Dimension layoutSize = new Dimension(availableSize);

		Dimension fieldBase = unscaledDimension(layoutSettings, Component.FIELD);
		Dimension sidebar = dimension(layoutSettings, Component.SIDEBAR);
		Dimension score = dimension(layoutSettings, Component.SCORE_BOARD);
		Dimension log = dimension(layoutSettings, Component.LOG);
		Dimension chat = dimension(layoutSettings, Component.CHAT);
		Dimension box = dimension(layoutSettings, Component.BOX);

		ClientLayout layout = layoutSettings.getLayout();
		switch (layout) {
			case PORTRAIT:
				return portrait(layoutSize, fieldBase, sidebar, box, score, log, chat);
			case SQUARE:
				return square(layoutSize, fieldBase, sidebar, box, score, log, chat);
			default:
				return landscape(layoutSize, fieldBase, sidebar, box, score, log, chat);
		}
	}

	private ClientLayoutResult landscape(Dimension availableSize, Dimension fieldBase, Dimension sidebar,
			Dimension box, Dimension score, Dimension log, Dimension chat) {
		Dimension logChatPanel = logChatPanelSize(log, chat);
		int bottomHeight = score.height + logChatPanel.height;

		Dimension layoutSize = new Dimension(availableSize);

		int centerX = sidebar.width;
		int centerWidth = Math.max(1, layoutSize.width - sidebar.width - sidebar.width);
		int pitchAreaHeight = Math.max(1, layoutSize.height - bottomHeight);
		PitchPlacement field = fitPitch(new Rectangle(centerX, 0, centerWidth, pitchAreaHeight), fieldBase);

		int scoreX = centerX + ((centerWidth - score.width) / 2);
		int scoreY = field.bounds.y + field.bounds.height;
		int logY = scoreY + score.height + PANEL_BORDER;
		int logX = centerX + ((centerWidth - logChatPanel.width) / 2) + PANEL_BORDER;
		int chatX = logX + log.width + LOG_CHAT_GAP;

		return new ClientLayoutResult(
			layoutSize,
			field.bounds,
			new Rectangle(0, 0, sidebar.width, layoutSize.height),
			new Rectangle(0, 0, box.width, box.height),
			new Rectangle(layoutSize.width - sidebar.width, 0, sidebar.width, layoutSize.height),
			new Rectangle(scoreX, scoreY, score.width, score.height),
			new Rectangle(logX, logY, log.width, log.height),
			new Rectangle(chatX, logY, chat.width, chat.height),
			field.scale
		);
	}

	private ClientLayoutResult portrait(Dimension availableSize, Dimension fieldBase, Dimension sidebar,
																						Dimension box, Dimension score, Dimension log, Dimension chat) {
		Dimension logChatPanel = logChatPanelSize(log, chat);
		int bottomHeight = score.height + logChatPanel.height;
		Dimension layoutSize = new Dimension(availableSize);

		int mainHeight = Math.max(1, layoutSize.height - bottomHeight);
		int pitchAreaWidth = Math.max(1, layoutSize.width - sidebar.width - sidebar.width);

		PitchPlacement field = fitPitch(new Rectangle(sidebar.width, 0, pitchAreaWidth, mainHeight), fieldBase);

		int scoreX = (layoutSize.width - score.width) / 2;
		int scoreY = field.bounds.y + field.bounds.height;
		int logY = scoreY + score.height + PANEL_BORDER;
		int logX = ((layoutSize.width - logChatPanel.width) / 2) + PANEL_BORDER;
		int chatX = logX + log.width + LOG_CHAT_GAP;

		return new ClientLayoutResult(
			layoutSize,
			field.bounds,
			new Rectangle(0, 0, sidebar.width, mainHeight),
			new Rectangle(0, 0, box.width, box.height),
			new Rectangle(layoutSize.width - sidebar.width, 0, sidebar.width, mainHeight),
			new Rectangle(scoreX, scoreY, score.width, score.height),
			new Rectangle(logX, logY, log.width, log.height),
			new Rectangle(chatX, logY, chat.width, chat.height),
			field.scale
		);
	}

	private ClientLayoutResult square(Dimension availableSize, Dimension fieldBase, Dimension sidebar,
			Dimension box, Dimension score, Dimension log, Dimension chat) {
		int rightColumnWidth = Math.max(log.width, Math.max(score.width, chat.width)) + (2 * PANEL_BORDER);

		Dimension layoutSize = new Dimension(availableSize);

		int mainWidth = Math.max(1, layoutSize.width - rightColumnWidth);
		int pitchAreaWidth = Math.max(1, mainWidth - sidebar.width - sidebar.width);

		double pitchScale = pitchScale(new Dimension(pitchAreaWidth, layoutSize.height), fieldBase);

		int fieldWidth = scaled(fieldBase.width, pitchScale);
		int fieldHeight = scaled(fieldBase.height, pitchScale);
		int fieldX = sidebar.width + ((pitchAreaWidth - fieldWidth) / 2);
		int fieldY = 0;

		int rightX = mainWidth + PANEL_BORDER;

		return new ClientLayoutResult(
			layoutSize,
			new Rectangle(fieldX, fieldY, fieldWidth, fieldHeight),
			new Rectangle(0, 0, sidebar.width, layoutSize.height),
			new Rectangle(0, 0, box.width, box.height),
			new Rectangle(mainWidth - sidebar.width, 0, sidebar.width, layoutSize.height),
			new Rectangle(rightX, log.height + PANEL_BORDER, score.width, score.height),
			new Rectangle(rightX, PANEL_BORDER, log.width, log.height),
			new Rectangle(rightX, log.height + score.height + PANEL_BORDER, chat.width, chat.height),
			pitchScale
		);
	}

	private double pitchScale(Dimension availablePitchArea, Dimension fieldBase) {
		return Math.min(
			(double) availablePitchArea.width / fieldBase.width,
			(double) availablePitchArea.height / fieldBase.height
		);
	}

	private PitchPlacement fitPitch(Rectangle pitchArea, Dimension field) {
		double scale = pitchScale(new Dimension(pitchArea.width, pitchArea.height), field);

		int fieldWidth = scaled(field.width, scale);
		int fieldHeight = scaled(field.height, scale);
		int fieldX = pitchArea.x + ((pitchArea.width - fieldWidth) / 2);
		int fieldY = pitchArea.y;

		return new PitchPlacement(new Rectangle(fieldX, fieldY, fieldWidth, fieldHeight), scale);
	}

	private Dimension logChatPanelSize(Dimension log, Dimension chat) {
		return new Dimension(
			log.width + LOG_CHAT_GAP + chat.width + (2 * PANEL_BORDER),
			Math.max(log.height, chat.height) + (2 * PANEL_BORDER)
		);
	}

	private int scaled(int size, double scale) {
		return (int) (size * scale);
	}

	public Dimension initialContentSize(LayoutSettings layoutSettings) {
		Dimension field = dimension(layoutSettings, Component.FIELD);
		Dimension sidebar = dimension(layoutSettings, Component.SIDEBAR);
		Dimension score = dimension(layoutSettings, Component.SCORE_BOARD);
		Dimension log = dimension(layoutSettings, Component.LOG);
		Dimension chat = dimension(layoutSettings, Component.CHAT);

		ClientLayout layout = layoutSettings.getLayout();
		if (layout == ClientLayout.SQUARE) {
			int rightColumnWidth = Math.max(log.width, Math.max(score.width, chat.width)) + (2 * PANEL_BORDER);
			int rightColumnHeight = log.height + score.height + chat.height + (2 * PANEL_BORDER);
			int mainWidth = sidebar.width + field.width + sidebar.width;
			int mainHeight = Math.max(sidebar.height, field.height);

			return new Dimension(mainWidth + rightColumnWidth, Math.max(mainHeight, rightColumnHeight));
		}

		Dimension logChatPanel = logChatPanelSize(log, chat);
		int bottomHeight = score.height + logChatPanel.height;

		if (layout == ClientLayout.PORTRAIT) {
			int mainWidth = sidebar.width + field.width + sidebar.width;
			return new Dimension(Math.max(mainWidth, Math.max(score.width, logChatPanel.width)),
				Math.max(sidebar.height, field.height) + bottomHeight);
		}

		int centerWidth = Math.max(field.width, Math.max(score.width, logChatPanel.width));
		return new Dimension(sidebar.width + centerWidth + sidebar.width,
			Math.max(sidebar.height, field.height + bottomHeight));
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
