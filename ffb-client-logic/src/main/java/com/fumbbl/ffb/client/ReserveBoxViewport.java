package com.fumbbl.ffb.client;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.client.ui.BoxComponent;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

public class ReserveBoxViewport {

	private final UiDimensionProvider uiDimensionProvider;
	private Rectangle viewportBounds;

	public ReserveBoxViewport(UiDimensionProvider uiDimensionProvider) {
		this.uiDimensionProvider = uiDimensionProvider;
		Dimension boxSize = boxSize();
		this.viewportBounds = new Rectangle(0, 0, boxSize.width, boxSize.height);
	}

	public Dimension boxSize() {
		return uiDimensionProvider.dimension(Component.BOX);
	}

	public Dimension squareSize() {
		return uiDimensionProvider.dimension(Component.BOX_SQUARE);
	}

	public Rectangle viewportBounds() {
		return new Rectangle(viewportBounds);
	}

	public void setViewportBounds(Rectangle viewportBounds) {
		this.viewportBounds = new Rectangle(viewportBounds);
	}

	public FieldCoordinate toReserveCoordinate(Point contentPoint, int boxTitleOffset) {
		Point localPoint = new Point(contentPoint.x - viewportBounds.x, contentPoint.y - viewportBounds.y);
		Dimension squareSize = squareSize();

		if ((localPoint.x >= 0) && (localPoint.x < viewportBounds.width)
			&& (localPoint.y >= 0) && (localPoint.y < viewportBounds.height)) {
			int y = (((localPoint.y - boxTitleOffset) / squareSize.height) * 3)
				+ (localPoint.x / squareSize.width);

			if ((y >= 0) && (y < BoxComponent.MAX_BOX_ELEMENTS)) {
				return new FieldCoordinate(FieldCoordinate.RSV_HOME_X, y);
			}
		}

		return null;
	}
}