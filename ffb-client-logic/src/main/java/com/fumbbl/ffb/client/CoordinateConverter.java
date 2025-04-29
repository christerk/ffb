package com.fumbbl.ffb.client;

import com.fumbbl.ffb.FieldCoordinate;

import java.awt.*;
import java.awt.event.MouseEvent;

public class CoordinateConverter {
	private final UiDimensionProvider uiDimensionProvider;
	private final PitchDimensionProvider pitchDimensionProvider;

	public CoordinateConverter(UiDimensionProvider uiDimensionProvider, PitchDimensionProvider pitchDimensionProvider) {
		this.uiDimensionProvider = uiDimensionProvider;
		this.pitchDimensionProvider = pitchDimensionProvider;
	}

	public FieldCoordinate getFieldCoordinate(MouseEvent pMouseEvent) {
		FieldCoordinate coordinate = null;
		int x = pMouseEvent.getX();
		int y = pMouseEvent.getY();
		Dimension field = uiDimensionProvider.dimension(Component.FIELD);
		if ((x > 0) && (x < field.width) && (y > 0) && (y < field.height)) {
			coordinate = new FieldCoordinate((int) ((x / (pitchDimensionProvider.getLayoutSettings().getScale() * pitchDimensionProvider.getLayoutSettings().getLayout().getPitchScale())) / pitchDimensionProvider.unscaledFieldSquare()),
				(int) ((y / (pitchDimensionProvider.getLayoutSettings().getScale() * pitchDimensionProvider.getLayoutSettings().getLayout().getPitchScale())) / pitchDimensionProvider.unscaledFieldSquare()));
			coordinate = pitchDimensionProvider.mapToGlobal(coordinate);
		}
		return coordinate;
	}

}
