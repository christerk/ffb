package com.fumbbl.ffb.client.layout;

import com.fumbbl.ffb.client.ClientLayout;
import com.fumbbl.ffb.client.Component;
import com.fumbbl.ffb.client.LayoutSettings;

import java.awt.Dimension;
import java.awt.Rectangle;

public class ClientLayoutCalculator {

	private static final int LOG_CHAT_GAP = 2;
	private static final int PANEL_BORDER = 1;

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
		int logChatPanelWidth = log.width + LOG_CHAT_GAP + chat.width + (2 * PANEL_BORDER);
		int logChatPanelHeight = Math.max(log.height, chat.height) + (2 * PANEL_BORDER);
		int bottomHeight = score.height + logChatPanelHeight;

		Dimension layoutSize = new Dimension(availableSize);

		int centerX = sidebar.width;
		int centerWidth = Math.max(1, layoutSize.width - sidebar.width - sidebar.width);
		int pitchAreaHeight = Math.max(1, layoutSize.height - bottomHeight);

		double pitchScale = pitchScale(new Dimension(centerWidth, pitchAreaHeight), fieldBase);

		int fieldWidth = scaled(fieldBase.width, pitchScale);
		int fieldHeight = scaled(fieldBase.height, pitchScale);
		int fieldX = centerX + ((centerWidth - fieldWidth) / 2);
		int fieldY = 0;

		int scoreX = centerX + ((centerWidth - score.width) / 2);
		int scoreY = fieldY + fieldHeight;
		int logY = scoreY + score.height + PANEL_BORDER;
		int logX = centerX + ((centerWidth - logChatPanelWidth) / 2) + PANEL_BORDER;
		int chatX = logX + log.width + LOG_CHAT_GAP;

		return new ClientLayoutResult(
			layoutSize,
			new Rectangle(fieldX, fieldY, fieldWidth, fieldHeight),
			new Rectangle(0, 0, sidebar.width, layoutSize.height),
			new Rectangle(0, 0, box.width, box.height),
			new Rectangle(layoutSize.width - sidebar.width, 0, sidebar.width, layoutSize.height),
			new Rectangle(scoreX, scoreY, score.width, score.height),
			new Rectangle(logX, logY, log.width, log.height),
			new Rectangle(chatX, logY, chat.width, chat.height),
			pitchScale
		);
	}

	private ClientLayoutResult portrait(Dimension availableSize, Dimension fieldBase, Dimension sidebar,
																						Dimension box, Dimension score, Dimension log, Dimension chat) {
		int logChatPanelWidth = log.width + LOG_CHAT_GAP + chat.width + (2 * PANEL_BORDER);
		int logChatPanelHeight = Math.max(log.height, chat.height) + (2 * PANEL_BORDER);
		int bottomHeight = score.height + logChatPanelHeight;
		Dimension layoutSize = new Dimension(availableSize);

		int mainHeight = Math.max(1, layoutSize.height - bottomHeight);
		int pitchAreaWidth = Math.max(1, layoutSize.width - sidebar.width - sidebar.width);

		double pitchScale = pitchScale(new Dimension(pitchAreaWidth, mainHeight), fieldBase);

		int fieldWidth = scaled(fieldBase.width, pitchScale);
		int fieldHeight = scaled(fieldBase.height, pitchScale);
		int fieldX = sidebar.width + ((pitchAreaWidth - fieldWidth) / 2);
		int fieldY = 0;

		int scoreX = (layoutSize.width - score.width) / 2;
		int scoreY = fieldY + fieldHeight;
		int logY = scoreY + score.height + PANEL_BORDER;
		int logX = ((layoutSize.width - logChatPanelWidth) / 2) + PANEL_BORDER;
		int chatX = logX + log.width + LOG_CHAT_GAP;

		return new ClientLayoutResult(
			layoutSize,
			new Rectangle(fieldX, fieldY, fieldWidth, fieldHeight),
			new Rectangle(0, 0, sidebar.width, mainHeight),
			new Rectangle(0, 0, box.width, box.height),
			new Rectangle(layoutSize.width - sidebar.width, 0, sidebar.width, mainHeight),
			new Rectangle(scoreX, scoreY, score.width, score.height),
			new Rectangle(logX, logY, log.width, log.height),
			new Rectangle(chatX, logY, chat.width, chat.height),
			pitchScale
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

		int logChatWidth = log.width + LOG_CHAT_GAP + chat.width + (2 * PANEL_BORDER);
		int logChatHeight = Math.max(log.height, chat.height) + (2 * PANEL_BORDER);
		int bottomHeight = score.height + logChatHeight;

		if (layout == ClientLayout.PORTRAIT) {
			int mainWidth = sidebar.width + field.width + sidebar.width;
			return new Dimension(Math.max(mainWidth, Math.max(score.width, logChatWidth)),
				Math.max(sidebar.height, field.height) + bottomHeight);
		}

		int centerWidth = Math.max(field.width, Math.max(score.width, logChatWidth));
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
