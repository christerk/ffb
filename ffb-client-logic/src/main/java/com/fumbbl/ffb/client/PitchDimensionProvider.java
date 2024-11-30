package com.fumbbl.ffb.client;

import com.fumbbl.ffb.Direction;
import com.fumbbl.ffb.FieldCoordinate;

import java.awt.*;

public class PitchDimensionProvider extends DimensionProvider {

	public PitchDimensionProvider(LayoutSettings layoutSettings) {
		super(layoutSettings, RenderContext.ON_PITCH);
	}

	public FieldCoordinate mapToGlobal(FieldCoordinate fieldCoordinate) {
		if (isPitchPortrait()) {
			return new FieldCoordinate(25 - fieldCoordinate.getY(), fieldCoordinate.getX());
		}

		return fieldCoordinate;
	}

	public int fieldSquareSize() {
		return fieldSquareSize(1);
	}

	public int fieldSquareSize(double factor) {
		return (int) scale(unscaledFieldSquare() * factor);
	}

	public int unscaledFieldSquare() {
		return unscaledDimension(Component.FIELD_SQUARE).width;
	}

	public int imageOffset() {
		return fieldSquareSize() / 2;
	}

	public Dimension mapToLocal(int x, int y, boolean addImageOffset) {
		int offset = addImageOffset ? unscaledFieldSquare() / 2 : 0;

		if (isPitchPortrait()) {
			return scale(new Dimension(y * unscaledFieldSquare() + offset, (25 - x) * unscaledFieldSquare() + offset));
		}
		return scale(new Dimension(x * unscaledFieldSquare() + offset, y * unscaledFieldSquare() + offset));
	}

	public Dimension mapToLocal(FieldCoordinate fieldCoordinate) {
		return mapToLocal(fieldCoordinate, false);
	}

	public Dimension mapToLocal(FieldCoordinate fieldCoordinate, boolean addImageOffset) {
		return mapToLocal(fieldCoordinate.getX(), fieldCoordinate.getY(), addImageOffset);
	}

	public Direction mapToLocal(Direction direction) {
		if (isPitchPortrait()) {
			switch (direction) {
				case NORTHEAST:
					return Direction.NORTHWEST;
				case EAST:
					return Direction.NORTH;
				case SOUTHEAST:
					return Direction.NORTHEAST;
				case SOUTHWEST:
					return Direction.SOUTHEAST;
				case WEST:
					return Direction.SOUTH;
				case NORTHWEST:
					return Direction.SOUTHWEST;
				case NORTH:
					return Direction.WEST;
				case SOUTH:
					return Direction.EAST;
			}
		}

		return direction;
	}
}
