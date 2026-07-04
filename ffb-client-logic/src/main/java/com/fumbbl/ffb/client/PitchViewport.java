package com.fumbbl.ffb.client;

import com.fumbbl.ffb.Direction;
import com.fumbbl.ffb.FieldCoordinate;

import java.awt.Dimension;
import java.awt.Point;

public class PitchViewport {

	private final UiDimensionProvider uiDimensionProvider;
	private final PitchDimensionProvider pitchDimensionProvider;

	public PitchViewport(UiDimensionProvider uiDimensionProvider, PitchDimensionProvider pitchDimensionProvider) {
		this.uiDimensionProvider = uiDimensionProvider;
		this.pitchDimensionProvider = pitchDimensionProvider;
	}

	public Dimension fieldSize() {
		return uiDimensionProvider.dimension(Component.FIELD);
	}

	public int squareSize() {
		return pitchDimensionProvider.fieldSquareSize();
	}

	public int squareSize(double factor) {
		return pitchDimensionProvider.fieldSquareSize(factor);
	}

	public int imageOffset() {
		return pitchDimensionProvider.imageOffset();
	}

	public Dimension toLocal(FieldCoordinate coordinate) {
		return pitchDimensionProvider.mapToLocal(coordinate);
	}

	public Dimension toLocal(FieldCoordinate coordinate, boolean center) {
		return pitchDimensionProvider.mapToLocal(coordinate, center);
	}

	public Dimension toLocal(int x, int y, boolean center) {
		return pitchDimensionProvider.mapToLocal(x, y, center);
	}

	public FieldCoordinate toFieldCoordinate(Point localPoint) {
		FieldCoordinate coordinate = null;
		int x = localPoint.x;
		int y = localPoint.y;
		Dimension field = fieldSize();

		if ((x > 0) && (x < field.width) && (y > 0) && (y < field.height)) {
			double scale = pitchDimensionProvider.getLayoutSettings().getScale()
				* pitchDimensionProvider.getLayoutSettings().getLayout().getPitchScale();

			coordinate = new FieldCoordinate(
				(int) ((x / scale) / pitchDimensionProvider.unscaledFieldSquare()),
				(int) ((y / scale) / pitchDimensionProvider.unscaledFieldSquare())
			);
			coordinate = pitchDimensionProvider.mapToGlobal(coordinate);
		}

		return coordinate;
	}

	public Direction toLocal(Direction direction) {
		return pitchDimensionProvider.mapToLocal(direction);
	}

	public Direction getLocalDirection(FieldCoordinate from, FieldCoordinate to) {
		return pitchDimensionProvider.getDirection(from, to);
	}
}