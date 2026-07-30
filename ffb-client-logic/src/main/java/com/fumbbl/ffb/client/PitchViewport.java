package com.fumbbl.ffb.client;

import com.fumbbl.ffb.Direction;
import com.fumbbl.ffb.FieldCoordinate;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

/**
 * Owns the runtime pitch viewport and pitch/world coordinate conversion.
 *
 * Layout code updates the viewport bounds and runtime pitch scale. Rendering,
 * input, animation, and overlays use this class so they agree on where the
 * pitch is and how it is scaled.
 */

public class PitchViewport {

	private final LayoutSettings layoutSettings;
	private Rectangle viewportBounds;
	private double runtimePitchScale;

	public PitchViewport(UiDimensionProvider uiDimensionProvider, LayoutSettings layoutSettings) {
		this.layoutSettings = layoutSettings;
		this.runtimePitchScale = layoutSettings.getPitchScale();
		Dimension fieldSize = uiDimensionProvider.dimension(Component.FIELD);
		this.viewportBounds = new Rectangle(0, 0, fieldSize.width, fieldSize.height);
	}

	public Dimension fieldSize() {
		return new Dimension(viewportBounds.width, viewportBounds.height);
	}

	public double runtimePitchScale() {
		return runtimePitchScale;
	}

	public void setRuntimePitchScale(double runtimePitchScale) {
		this.runtimePitchScale = runtimePitchScale;
	}

	public double effectiveScale() {
		return runtimePitchScale * layoutSettings.getLayout().getPitchScale();
	}

	public int squareSize() {
		return squareSize(1);
	}

	public int squareSize(double factor) {
		return (int) (unscaledFieldSquare() * factor * effectiveScale());
	}

	public int imageOffset() {
		return squareSize() / 2;
	}

	public Dimension toLocal(FieldCoordinate coordinate) {
		return toLocal(coordinate, false);
	}

	public Dimension toLocal(FieldCoordinate coordinate, boolean center) {
		return toLocal(coordinate.getX(), coordinate.getY(), center);
	}

	public Dimension toLocal(int x, int y, boolean center) {
		int offset = center ? unscaledFieldSquare() / 2 : 0;
		int localX;
		int localY;

		if (layoutSettings.getLayout().isPortrait()) {
			localX = y * unscaledFieldSquare() + offset;
			localY = (25 - x) * unscaledFieldSquare() + offset;
		} else {
			localX = x * unscaledFieldSquare() + offset;
			localY = y * unscaledFieldSquare() + offset;
		}

		return new Dimension((int) (localX * effectiveScale()), (int) (localY * effectiveScale()));
	}

	public Point worldToScreen(FieldCoordinate coordinate) {
		return worldToScreen(coordinate, false);
	}

	public Point worldToScreen(FieldCoordinate coordinate, boolean center) {
		return worldToScreen(coordinate.getX(), coordinate.getY(), center);
	}

	public Point worldToScreen(int x, int y, boolean center) {
		Dimension local = toLocal(x, y, center);
		return new Point(viewportBounds.x + local.width, viewportBounds.y + local.height);
	}

	public FieldCoordinate toFieldCoordinate(Point localPoint) {
		FieldCoordinate coordinate = null;
		int x = localPoint.x;
		int y = localPoint.y;
		Dimension field = fieldSize();

		if ((x > 0) && (x < field.width) && (y > 0) && (y < field.height)) {
			double scale = effectiveScale();

			coordinate = new FieldCoordinate(
				(int) ((x / scale) / unscaledFieldSquare()),
				(int) ((y / scale) / unscaledFieldSquare())
			);
			coordinate = toGlobal(coordinate);
		}

		return coordinate;
	}

	// Screen coordinates are currently client content coordinates, not OS/global monitor coordinates.
	public FieldCoordinate screenToWorld(Point screenPoint) {
		Point localPoint = new Point(screenPoint.x - viewportBounds.x, screenPoint.y - viewportBounds.y);
		return toFieldCoordinate(localPoint);
	}

	public Direction toLocal(Direction direction) {
		if (layoutSettings.getLayout().isPortrait()) {
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

	public Direction getLocalDirection(FieldCoordinate from, FieldCoordinate to) {
		Direction direction = from.getDirection(to);
		if (layoutSettings.getLayout().isPortrait()) {
			return toLocal(direction);
		}
		return direction;
	}

	public Rectangle viewportBounds() {
		return new Rectangle(viewportBounds);
	}

	public void setViewportBounds(Rectangle viewportBounds) {
		this.viewportBounds = new Rectangle(viewportBounds);
	}

	private int unscaledFieldSquare() {
		return Component.FIELD_SQUARE.dimension(layoutSettings.getLayout()).width;
	}

	private FieldCoordinate toGlobal(FieldCoordinate fieldCoordinate) {
		if (layoutSettings.getLayout().isPortrait()) {
			return new FieldCoordinate(25 - fieldCoordinate.getY(), fieldCoordinate.getX());
		}
		return fieldCoordinate;
	}
}
