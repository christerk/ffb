package com.fumbbl.ffb.client;

import com.fumbbl.ffb.Direction;
import com.fumbbl.ffb.FieldCoordinate;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import static com.fumbbl.ffb.client.LayoutSettings.BASE_SQUARE_SIZE;

/**
 * Owns the runtime pitch viewport and pitch/world coordinate conversion.
 *
 * Layout code updates the viewport bounds and runtime pitch scale. Rendering,
 * input, animation, and overlays use this class so they agree on where the
 * pitch is and how it is scaled.
 */

public class PitchViewport {
	public static final int WORLD_WIDTH = FieldCoordinate.FIELD_WIDTH * BASE_SQUARE_SIZE;
	public static final int WORLD_HEIGHT = FieldCoordinate.FIELD_HEIGHT * BASE_SQUARE_SIZE;

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

	/** Rounded display length for decorations; use worldSquare/localSquare for tile coverage. */
	public int squareSize() {
		return squareSize(1);
	}

	public int squareSize(double factor) {
		return (int) (unscaledFieldSquare() * factor * effectiveScale());
	}

	public int imageOffset() {
		return squareSize() / 2;
	}

	/**
	 * Maps pitch geometry (30 world units per square) into the field's image buffers.
	 * The grid is centered within the existing field allowance. Bounds are only used
	 * for its origin; the exact scale still comes from layout, not rounded bounds.
	 * Returns a new transform so callers cannot mutate viewport state.
	 */
	public AffineTransform worldToLocalTransform() {
		double scale = effectiveScale();
		boolean portrait = layoutSettings.getLayout().isPortrait();
		double width = (portrait ? WORLD_HEIGHT : WORLD_WIDTH) * scale;
		double height = (portrait ? WORLD_WIDTH : WORLD_HEIGHT) * scale;
		AffineTransform transform = AffineTransform.getTranslateInstance(
			(viewportBounds.width - width) / 2, (viewportBounds.height - height) / 2);
		transform.scale(scale, scale);
		if (portrait) {
			transform.translate(0, WORLD_WIDTH);
			transform.quadrantRotate(-1);
		}
		return transform;
	}

	public Rectangle2D worldSquare(FieldCoordinate coordinate) {
		return new Rectangle2D.Double(coordinate.getX() * BASE_SQUARE_SIZE,
			coordinate.getY() * BASE_SQUARE_SIZE, BASE_SQUARE_SIZE, BASE_SQUARE_SIZE);
	}

	/** Exact projected edges, for screen-facing decorations and pixel-width insets. */
	public Rectangle2D localSquare(FieldCoordinate coordinate) {
		return worldToLocalTransform().createTransformedShape(worldSquare(coordinate)).getBounds2D();
	}

	public Dimension toLocal(FieldCoordinate coordinate) {
		return toLocal(coordinate, false);
	}

	public Dimension toLocal(FieldCoordinate coordinate, boolean center) {
		return toLocal(coordinate.getX(), coordinate.getY(), center);
	}

	public Dimension toLocal(int x, int y, boolean center) {
		Rectangle2D bounds = localSquare(new FieldCoordinate(x, y));
		return new Dimension((int) Math.floor(center ? bounds.getCenterX() : bounds.getMinX()),
			(int) Math.floor(center ? bounds.getCenterY() : bounds.getMinY()));
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
		if (localPoint.x < 0 || localPoint.y < 0
			|| localPoint.x >= viewportBounds.width || localPoint.y >= viewportBounds.height) {
			return null;
		}
		try {
			// Mouse coordinates identify pixels; test their centers against world geometry.
			Point2D world = worldToLocalTransform().inverseTransform(
				new Point2D.Double(localPoint.x + 0.5, localPoint.y + 0.5), null);
			// Raster coverage includes the top/left edge and excludes bottom/right.
			// Portrait reverses world X: its intervals are (left, right], not [left, right).
			int x = layoutSettings.getLayout().isPortrait()
				? (int) Math.ceil(world.getX() / BASE_SQUARE_SIZE) - 1
				: (int) Math.floor(world.getX() / BASE_SQUARE_SIZE);
			int y = (int) Math.floor(world.getY() / BASE_SQUARE_SIZE);
			if (x < 0 || y < 0 || x >= FieldCoordinate.FIELD_WIDTH
				|| y >= FieldCoordinate.FIELD_HEIGHT) {
				return null;
			}
			return new FieldCoordinate(x, y);
		} catch (NoninvertibleTransformException exception) {
			throw new IllegalStateException("Pitch scale must produce an invertible transform", exception);
		}
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

}
