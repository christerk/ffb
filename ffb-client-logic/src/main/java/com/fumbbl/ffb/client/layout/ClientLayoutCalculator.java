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
	private static class PitchFit {

		private final Rectangle bounds;
		private final double scale;

		private PitchFit(Rectangle bounds, double scale) {
			this.bounds = bounds;
			this.scale = scale;
		}
	}

	public ClientLayoutResult calculate(LayoutSettings layoutSettings, Dimension availableSize) {
		Dimension naturalSize = referenceContentSize(layoutSettings);
		double resizeScale = Math.min((double) availableSize.width / naturalSize.width,
			(double) availableSize.height / naturalSize.height);
		double effectiveRatio = Math.max(LayoutSettings.MIN_GUI_RATIO,
			Math.min(layoutSettings.getGuiRatio(), LayoutSettings.MAX_GUI_RATIO));
		double guiScale = layoutSettings.getScale() * effectiveRatio * resizeScale;

		Dimension sidebar = dimension(layoutSettings, Component.SIDEBAR, guiScale);
		Dimension reserveBox = dimension(layoutSettings, Component.BOX, guiScale);
		Dimension score = dimension(layoutSettings, Component.SCORE_BOARD, guiScale);
		Dimension log = dimension(layoutSettings, Component.LOG, guiScale);
		Dimension chat = dimension(layoutSettings, Component.CHAT, guiScale);

		Dimension layoutSize = scale(naturalSize, resizeScale);
		Rectangle content = new Rectangle((availableSize.width - layoutSize.width) / 2,
			(availableSize.height - layoutSize.height) / 2, layoutSize.width, layoutSize.height);

		LayoutAreas areas = LayoutAreas.arrange(layoutSettings.getLayout(), content, sidebar.width, score, log, chat);
		PitchFit pitchFit = fitPitch(areas.pitchArea, layoutSettings);
		LayoutAreas.HudBounds hudBounds = areas.placeHud(pitchFit.bounds, reserveBox, score, log, chat);

		return new ClientLayoutResult(
			new Dimension(availableSize),
			pitchFit.bounds,
			hudBounds.homeRail,
			hudBounds.homeReserveBox,
			hudBounds.awayRail,
			hudBounds.score,
			hudBounds.log,
			hudBounds.chat,
			pitchFit.scale,
			guiScale,
			guiScale);
	}

	private PitchFit fitPitch(Rectangle pitchArea, LayoutSettings settings) {
		boolean portrait = settings.getLayout().isPortrait();
		int columns = portrait ? 15 : 26;
		int rows = portrait ? 26 : 15;
		int square = Math.max(1, Math.min(pitchArea.width / columns, pitchArea.height / rows));
		while (square > 1 && (columns * square + 2 * border(square) > pitchArea.width
			|| rows * square + 2 * border(square) > pitchArea.height)) {
			square--;
		}
		int pitchWidth = columns * square + 2 * border(square);
		int pitchHeight = rows * square + 2 * border(square);
		int pitchX = pitchArea.x + ((pitchArea.width - pitchWidth) / 2);
		int pitchY = pitchArea.y;
		double scale = (double) square / LayoutSettings.BASE_SQUARE_SIZE / settings.getLayout().getPitchScale();
		return new PitchFit(new Rectangle(pitchX, pitchY, pitchWidth, pitchHeight), scale);
	}

	private int border(int square) {
		return (int) Math.ceil((double) square / LayoutSettings.BASE_SQUARE_SIZE);
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

	public Dimension minimumContentSize(LayoutSettings layoutSettings) {
		LayoutSettings minimum = new LayoutSettings(layoutSettings.getLayout(), LayoutSettings.MIN_SCALE_FACTOR);
		minimum.setGuiRatio(Math.max(LayoutSettings.MIN_GUI_RATIO,
			Math.min(layoutSettings.getGuiRatio(), LayoutSettings.MAX_GUI_RATIO)));
		return naturalContentSize(minimum);
	}

	public Dimension referenceContentSize(LayoutSettings settings) {
		LayoutSettings reference = new LayoutSettings(settings.getLayout(), settings.getScale());
		reference.setGuiRatio(Math.max(LayoutSettings.MIN_GUI_RATIO,
			Math.min(settings.getGuiRatio(), LayoutSettings.MAX_GUI_RATIO)));
		return naturalContentSize(reference);
	}

	private Dimension dimension(LayoutSettings layoutSettings, Component component) {
		return scale(unscaledDimension(layoutSettings, component), layoutSettings.getGuiScale());
	}

	private Dimension dimension(LayoutSettings layoutSettings, Component component, double scale) {
		return scale(unscaledDimension(layoutSettings, component), scale);
	}

	private Dimension unscaledDimension(LayoutSettings layoutSettings, Component component) {
		return component.dimension(layoutSettings.getLayout());
	}

	private Dimension scale(Dimension dimension, double scale) {
		return new Dimension(scaled(dimension.width, scale), scaled(dimension.height, scale));
	}

}
