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
		double runtimeResizeScale = runtimeResizeScale(layoutSettings, availableSize);
		double guiScale = layoutSettings.getGuiScale() * runtimeResizeScale;
		double dugoutScale = layoutSettings.getDugoutScale() * runtimeResizeScale;

		Dimension sidebar = dimension(layoutSettings, Component.SIDEBAR, guiScale);
		Dimension reserveBox = dimension(layoutSettings, Component.BOX, guiScale);
		Dimension score = dimension(layoutSettings, Component.SCORE_BOARD, guiScale);
		Dimension log = dimension(layoutSettings, Component.LOG, guiScale);
		Dimension chat = dimension(layoutSettings, Component.CHAT, guiScale);
		Dimension pitch = unscaledDimension(layoutSettings, Component.FIELD);

		Dimension layoutSize = layoutSettings.isDynamicPitchScaling()
			? new Dimension(availableSize)
			: LayoutAreas.naturalSize(layoutSettings.getLayout(), sidebar,
				scale(pitch, layoutSettings.getPitchScale() * runtimeResizeScale), score, log, chat);

		Rectangle content = centered(layoutSize, availableSize);
		
		LayoutAreas areas = LayoutAreas.arrange(layoutSettings.getLayout(), content, sidebar.width, score, log, chat);
		PitchFit pitchFit = fitPitch(areas.pitchArea, pitch);
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
			dugoutScale);
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

	private Rectangle centered(Dimension size, Dimension availableSize) {
		return new Rectangle(
			(availableSize.width - size.width) / 2,
			(availableSize.height - size.height) / 2,
			size.width,
			size.height);
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

	private Dimension dimension(LayoutSettings layoutSettings, Component component, double scale) {
		return scale(unscaledDimension(layoutSettings, component), scale);
	}

	private Dimension unscaledDimension(LayoutSettings layoutSettings, Component component) {
		return component.dimension(layoutSettings.getLayout());
	}

	private Dimension scale(Dimension dimension, double scale) {
		return new Dimension(scaled(dimension.width, scale), scaled(dimension.height, scale));
	}

	private double runtimeResizeScale(LayoutSettings layoutSettings, Dimension availableSize) {
		if (layoutSettings.isDynamicPitchScaling()) {
			return 1.0;
		}

		Dimension naturalSize = naturalContentSize(layoutSettings);
		return Math.min((double) availableSize.width / naturalSize.width,
			(double) availableSize.height / naturalSize.height);
	}

}
