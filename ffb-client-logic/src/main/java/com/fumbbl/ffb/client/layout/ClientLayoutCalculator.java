package com.fumbbl.ffb.client.layout;

import com.fumbbl.ffb.client.ClientLayout;
import com.fumbbl.ffb.client.Component;
import com.fumbbl.ffb.client.UiDimensionProvider;

import java.awt.Dimension;
import java.awt.Rectangle;

public class ClientLayoutCalculator {

	private static final int LOG_CHAT_GAP = 2;
	private static final int PANEL_BORDER = 1;

	public ClientLayoutResult calculate(UiDimensionProvider uiDimensionProvider) {
		Dimension field = uiDimensionProvider.dimension(Component.FIELD);
		Dimension sidebar = uiDimensionProvider.dimension(Component.SIDEBAR);
		Dimension score = uiDimensionProvider.dimension(Component.SCORE_BOARD);
		Dimension log = uiDimensionProvider.dimension(Component.LOG);
		Dimension chat = uiDimensionProvider.dimension(Component.CHAT);

		ClientLayout layout = uiDimensionProvider.getLayoutSettings().getLayout();
		switch (layout) {
			case PORTRAIT:
				return portrait(field, sidebar, score, log, chat);
			case SQUARE:
				return square(field, sidebar, score, log, chat);
			default:
				return landscape(field, sidebar, score, log, chat);
		}
	}

	private ClientLayoutResult landscape(Dimension field, Dimension sidebar, Dimension score, Dimension log, Dimension chat) {
		int logChatPanelWidth = log.width + LOG_CHAT_GAP + chat.width + (2 * PANEL_BORDER);
		int logChatPanelHeight = Math.max(log.height, chat.height) + (2 * PANEL_BORDER);
		int centerWidth = Math.max(field.width, Math.max(score.width, logChatPanelWidth));
		int centerHeight = field.height + score.height + logChatPanelHeight;

		Dimension preferredSize = new Dimension(sidebar.width + centerWidth + sidebar.width,
			Math.max(sidebar.height, centerHeight));

		int centerX = sidebar.width;
		int logChatY = field.height + score.height;
		int logY = logChatY + PANEL_BORDER;
		int logX = centerX + PANEL_BORDER;
		int chatX = logX + log.width + LOG_CHAT_GAP;

		return new ClientLayoutResult(
			preferredSize,
			new Rectangle(centerX, 0, field.width, field.height),
			new Rectangle(0, 0, sidebar.width, preferredSize.height),
			new Rectangle(centerX + centerWidth, 0, sidebar.width, preferredSize.height),
			new Rectangle(centerX, field.height, score.width, score.height),
			new Rectangle(logX, logY, log.width, log.height),
			new Rectangle(chatX, logY, chat.width, chat.height)
		);
	}

	private ClientLayoutResult portrait(Dimension field, Dimension sidebar, Dimension score, Dimension log, Dimension chat) {
		int mainWidth = sidebar.width + field.width + sidebar.width;
		int mainHeight = Math.max(sidebar.height, field.height);
		int logChatPanelWidth = log.width + LOG_CHAT_GAP + chat.width + (2 * PANEL_BORDER);
		int logChatPanelHeight = Math.max(log.height, chat.height) + (2 * PANEL_BORDER);

		Dimension preferredSize = new Dimension(Math.max(mainWidth, Math.max(score.width, logChatPanelWidth)),
			mainHeight + score.height + logChatPanelHeight);

		int scoreY = mainHeight;
		int logChatY = scoreY + score.height;
		int logY = logChatY + PANEL_BORDER;
		int logX = PANEL_BORDER;
		int chatX = logX + log.width + LOG_CHAT_GAP;

		return new ClientLayoutResult(
			preferredSize,
			new Rectangle(sidebar.width, 0, field.width, field.height),
			new Rectangle(0, 0, sidebar.width, mainHeight),
			new Rectangle(sidebar.width + field.width, 0, sidebar.width, mainHeight),
			new Rectangle(0, scoreY, score.width, score.height),
			new Rectangle(logX, logY, log.width, log.height),
			new Rectangle(chatX, logY, chat.width, chat.height)
		);
	}

	private ClientLayoutResult square(Dimension field, Dimension sidebar, Dimension score, Dimension log, Dimension chat) {
		int mainWidth = sidebar.width + field.width + sidebar.width;
		int mainHeight = Math.max(sidebar.height, field.height);
		int logChatScoreWidth = Math.max(log.width, Math.max(score.width, chat.width)) + (2 * PANEL_BORDER);
		int logChatScoreHeight = log.height + score.height + chat.height + (2 * PANEL_BORDER);

		Dimension preferredSize = new Dimension(mainWidth + logChatScoreWidth,
			Math.max(mainHeight, logChatScoreHeight));

		int rightX = mainWidth + PANEL_BORDER;

		return new ClientLayoutResult(
			preferredSize,
			new Rectangle(sidebar.width, 0, field.width, field.height),
			new Rectangle(0, 0, sidebar.width, mainHeight),
			new Rectangle(sidebar.width + field.width, 0, sidebar.width, mainHeight),
			new Rectangle(rightX, log.height + PANEL_BORDER, score.width, score.height),
			new Rectangle(rightX, PANEL_BORDER, log.width, log.height),
			new Rectangle(rightX, log.height + score.height + PANEL_BORDER, chat.width, chat.height)
		);
	}
}