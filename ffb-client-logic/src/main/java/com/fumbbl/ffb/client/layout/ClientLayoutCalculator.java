package com.fumbbl.ffb.client.layout;

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
		ResolvedLayout resolved = resolve(layoutSettings);
		Dimension layoutSize = new Dimension(availableSize);

		switch (resolved.layout) {
			case PORTRAIT:
				return portrait(layoutSize, resolved);
			case SQUARE:
				return square(layoutSize, resolved);
			default:
				return landscape(layoutSize, resolved);
		}
	}

	private ResolvedLayout resolve(LayoutSettings layoutSettings) {
		Dimension field = dimension(layoutSettings, Component.FIELD);
		Dimension fieldBase = unscaledDimension(layoutSettings, Component.FIELD);
		Dimension sidebar = dimension(layoutSettings, Component.SIDEBAR);
		Dimension box = dimension(layoutSettings, Component.BOX);
		Dimension score = dimension(layoutSettings, Component.SCORE_BOARD);
		Dimension log = dimension(layoutSettings, Component.LOG);
		Dimension chat = dimension(layoutSettings, Component.CHAT);

		return new ResolvedLayout(
			layoutSettings.getLayout(),
			field,
			fieldBase,
			sidebar,
			box,
			score,
			log,
			chat,
			bottomPanelSize(score, log, chat),
			rightColumnSize(score, log, chat)
		);
	}

	private ClientLayoutResult landscape(Dimension availableSize, ResolvedLayout resolved) {
		Dimension logChatPanel = logChatPanelSize(resolved.log, resolved.chat);

		Dimension layoutSize = new Dimension(availableSize);

		int centerX = resolved.sidebar.width;
		int centerWidth = Math.max(1, layoutSize.width - resolved.sidebar.width - resolved.sidebar.width);
		int pitchAreaHeight = Math.max(1, layoutSize.height - resolved.bottomPanel.height);
		PitchPlacement field = fitPitch(new Rectangle(centerX, 0, centerWidth, pitchAreaHeight), resolved.fieldBase);

		int scoreX = centerX + ((centerWidth - resolved.score.width) / 2);
		int scoreY = field.bounds.y + field.bounds.height;
		int logY = scoreY + resolved.score.height + PANEL_BORDER;
		int logX = centerX + ((centerWidth - logChatPanel.width) / 2) + PANEL_BORDER;
		int chatX = logX + resolved.log.width + LOG_CHAT_GAP;

		return new ClientLayoutResult(
			layoutSize,
			field.bounds,
			new Rectangle(0, 0, resolved.sidebar.width, layoutSize.height),
			new Rectangle(0, 0, resolved.box.width, resolved.box.height),
			new Rectangle(layoutSize.width - resolved.sidebar.width, 0, resolved.sidebar.width, layoutSize.height),
			new Rectangle(scoreX, scoreY, resolved.score.width, resolved.score.height),
			new Rectangle(logX, logY, resolved.log.width, resolved.log.height),
			new Rectangle(chatX, logY, resolved.chat.width, resolved.chat.height),
			field.scale
		);
	}

	private ClientLayoutResult portrait(Dimension availableSize, ResolvedLayout resolved) {
		Dimension logChatPanel = logChatPanelSize(resolved.log, resolved.chat);
		Dimension layoutSize = new Dimension(availableSize);

		int mainHeight = Math.max(1, layoutSize.height - resolved.bottomPanel.height);
		int pitchAreaWidth = Math.max(1, layoutSize.width - resolved.sidebar.width - resolved.sidebar.width);

		PitchPlacement field = fitPitch(new Rectangle(resolved.sidebar.width, 0, pitchAreaWidth, mainHeight), resolved.fieldBase);

		int scoreX = (layoutSize.width - resolved.score.width) / 2;
		int scoreY = field.bounds.y + field.bounds.height;
		int logY = scoreY + resolved.score.height + PANEL_BORDER;
		int logX = ((layoutSize.width - logChatPanel.width) / 2) + PANEL_BORDER;
		int chatX = logX + resolved.log.width + LOG_CHAT_GAP;

		return new ClientLayoutResult(
			layoutSize,
			field.bounds,
			new Rectangle(0, 0, resolved.sidebar.width, mainHeight),
			new Rectangle(0, 0, resolved.box.width, resolved.box.height),
			new Rectangle(layoutSize.width - resolved.sidebar.width, 0, resolved.sidebar.width, mainHeight),
			new Rectangle(scoreX, scoreY, resolved.score.width, resolved.score.height),
			new Rectangle(logX, logY, resolved.log.width, resolved.log.height),
			new Rectangle(chatX, logY, resolved.chat.width, resolved.chat.height),
			field.scale
		);
	}

	private ClientLayoutResult square(Dimension availableSize, ResolvedLayout resolved) {
		Dimension layoutSize = new Dimension(availableSize);

		int mainWidth = Math.max(1, layoutSize.width - resolved.rightColumn.width);
		int pitchAreaWidth = Math.max(1, mainWidth - resolved.sidebar.width - resolved.sidebar.width);

		PitchPlacement field = fitPitch(new Rectangle(resolved.sidebar.width, 0, pitchAreaWidth, layoutSize.height), resolved.fieldBase);

		int rightX = mainWidth + PANEL_BORDER;

		return new ClientLayoutResult(
			layoutSize,
			field.bounds,
			new Rectangle(0, 0, resolved.sidebar.width, layoutSize.height),
			new Rectangle(0, 0, resolved.box.width, resolved.box.height),
			new Rectangle(mainWidth - resolved.sidebar.width, 0, resolved.sidebar.width, layoutSize.height),
			new Rectangle(rightX, resolved.log.height + PANEL_BORDER, resolved.score.width, resolved.score.height),
			new Rectangle(rightX, PANEL_BORDER, resolved.log.width, resolved.log.height),
			new Rectangle(rightX, resolved.log.height + resolved.score.height + PANEL_BORDER, resolved.chat.width, resolved.chat.height),
			field.scale
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

	public Dimension initialContentSize(LayoutSettings layoutSettings) {
		return resolve(layoutSettings).configuredContentSize();
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
