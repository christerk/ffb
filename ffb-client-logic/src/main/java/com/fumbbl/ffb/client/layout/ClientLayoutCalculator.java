package com.fumbbl.ffb.client.layout;

import com.fumbbl.ffb.client.ClientLayout;
import com.fumbbl.ffb.client.Component;
import com.fumbbl.ffb.client.LayoutSettings;

import java.awt.Dimension;
import java.awt.Rectangle;

public class ClientLayoutCalculator {

	private static final int LOG_CHAT_GAP = 2;
	private static final int PANEL_BORDER = 1;

	public ClientLayoutResult calculate(LayoutSettings layoutSettings) {
		Dimension field = dimension(layoutSettings, Component.FIELD);
		Dimension sidebar = dimension(layoutSettings, Component.SIDEBAR);
		Dimension score = dimension(layoutSettings, Component.SCORE_BOARD);
		Dimension log = dimension(layoutSettings, Component.LOG);
		Dimension chat = dimension(layoutSettings, Component.CHAT);
		Dimension box = dimension(layoutSettings, Component.BOX);
		double pitchScale = layoutSettings.getPitchScale();

		ClientLayout layout = layoutSettings.getLayout();
		switch (layout) {
			case PORTRAIT:
				return portrait(field, sidebar, box, score, log, chat, pitchScale);
			case SQUARE:
				return square(field, sidebar, box, score, log, chat, pitchScale);
			default:
				return landscape(field, sidebar, box, score, log, chat, pitchScale);
		}
	}

	public ClientLayoutResult calculate(LayoutSettings layoutSettings, Dimension availableSize) {
		ClientLayoutResult fixedLayout = calculate(layoutSettings);
		Dimension fixedPreferredSize = fixedLayout.preferredSize();

		if (availableSize == null || availableSize.width <= 0 || availableSize.height <= 0) {
			return fixedLayout;
		}

		if (availableSize.width <= fixedPreferredSize.width || availableSize.height <= fixedPreferredSize.height) {
			return fixedLayout;
		}

		Dimension fieldBase = unscaledDimension(layoutSettings, Component.FIELD);
		Dimension sidebar = dimension(layoutSettings, Component.SIDEBAR);
		Dimension score = dimension(layoutSettings, Component.SCORE_BOARD);
		Dimension log = dimension(layoutSettings, Component.LOG);
		Dimension chat = dimension(layoutSettings, Component.CHAT);
		Dimension box = dimension(layoutSettings, Component.BOX);

		ClientLayout layout = layoutSettings.getLayout();
		switch (layout) {
			case PORTRAIT:
				return portraitDynamic(availableSize, fieldBase, sidebar, box, score, log, chat);
			case SQUARE:
				return squareDynamic(availableSize, fieldBase, sidebar, box, score, log, chat);
			default:
				return landscapeDynamic(availableSize, fieldBase, sidebar, box, score, log, chat);
		}
	}

	private ClientLayoutResult landscape(Dimension field, Dimension sidebar, Dimension box, Dimension score,
			Dimension log, Dimension chat, double pitchScale) {
		int logChatPanelWidth = log.width + LOG_CHAT_GAP + chat.width + (2 * PANEL_BORDER);
		int logChatPanelHeight = Math.max(log.height, chat.height) + (2 * PANEL_BORDER);
		int bottomHeight = score.height + logChatPanelHeight;

		int requiredCenterWidth = Math.max(field.width, Math.max(score.width, logChatPanelWidth));
		int requiredHeight = field.height + bottomHeight;

		Dimension layoutSize = new Dimension(sidebar.width + requiredCenterWidth + sidebar.width,
			Math.max(sidebar.height, requiredHeight));

		int centerX = sidebar.width;
		int centerWidth = Math.max(1, layoutSize.width - sidebar.width - sidebar.width);
		int pitchAreaHeight = field.height;

		int fieldWidth = field.width;
		int fieldHeight = field.height;
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

	private ClientLayoutResult landscapeDynamic(Dimension availableSize, Dimension fieldBase, Dimension sidebar,
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

	private ClientLayoutResult portrait(Dimension field, Dimension sidebar, Dimension box, Dimension score,
			Dimension log, Dimension chat, double pitchScale) {
		int mainWidth = sidebar.width + field.width + sidebar.width;
		int mainHeight = Math.max(sidebar.height, field.height);
		int logChatPanelWidth = log.width + LOG_CHAT_GAP + chat.width + (2 * PANEL_BORDER);
		int logChatPanelHeight = Math.max(log.height, chat.height) + (2 * PANEL_BORDER);

		Dimension layoutSize = new Dimension(Math.max(mainWidth, Math.max(score.width, logChatPanelWidth)),
			mainHeight + score.height + logChatPanelHeight);

		int pitchAreaWidth = Math.max(1, layoutSize.width - sidebar.width - sidebar.width);

		int fieldWidth = field.width;
		int fieldHeight = field.height;
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

	private ClientLayoutResult portraitDynamic(Dimension availableSize, Dimension fieldBase, Dimension sidebar,
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

	private ClientLayoutResult square(Dimension field, Dimension sidebar, Dimension box, Dimension score,
			Dimension log, Dimension chat, double pitchScale) {
		int rightColumnWidth = Math.max(log.width, Math.max(score.width, chat.width)) + (2 * PANEL_BORDER);
		int rightColumnHeight = log.height + score.height + chat.height + (2 * PANEL_BORDER);

		int requiredMainWidth = sidebar.width + field.width + sidebar.width;
		int requiredHeight = Math.max(sidebar.height, field.height);

		Dimension layoutSize = new Dimension(requiredMainWidth + rightColumnWidth,
			Math.max(requiredHeight, rightColumnHeight));

		int mainWidth = Math.max(1, layoutSize.width - rightColumnWidth);
		int pitchAreaWidth = Math.max(1, mainWidth - sidebar.width - sidebar.width);

		int fieldWidth = field.width;
		int fieldHeight = field.height;
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

	private ClientLayoutResult squareDynamic(Dimension availableSize, Dimension fieldBase, Dimension sidebar,
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
