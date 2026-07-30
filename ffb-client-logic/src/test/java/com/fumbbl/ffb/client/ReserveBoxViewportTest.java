package com.fumbbl.ffb.client;

import com.fumbbl.ffb.FieldCoordinate;
import org.junit.jupiter.api.Test;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ReserveBoxViewportTest {

	@Test
	void initializesToCurrentBoxSizeAtOrigin() {
		ReserveBoxViewport viewport = viewport(ClientLayout.LANDSCAPE);

		assertEquals(new Dimension(145, 430), viewport.boxSize());
		assertEquals(new Dimension(48, 48), viewport.squareSize());
		assertEquals(new Rectangle(0, 0, 145, 430), viewport.viewportBounds());
	}

	@Test
	void mapsLandscapeContentPointToHomeReserveCoordinate() {
		ReserveBoxViewport viewport = viewport(ClientLayout.LANDSCAPE);

		assertEquals(new FieldCoordinate(FieldCoordinate.RSV_HOME_X, 0),
			viewport.toReserveCoordinate(new Point(0, 0), 0));
		assertEquals(new FieldCoordinate(FieldCoordinate.RSV_HOME_X, 1),
			viewport.toReserveCoordinate(new Point(48, 0), 0));
		assertEquals(new FieldCoordinate(FieldCoordinate.RSV_HOME_X, 2),
			viewport.toReserveCoordinate(new Point(96, 0), 0));
		assertEquals(new FieldCoordinate(FieldCoordinate.RSV_HOME_X, 3),
			viewport.toReserveCoordinate(new Point(0, 48), 0));
	}

	@Test
	void appliesViewportOffsetBeforeMapping() {
		ReserveBoxViewport viewport = viewport(ClientLayout.LANDSCAPE);
		viewport.setViewportBounds(new Rectangle(10, 20, 145, 430));

		assertEquals(new FieldCoordinate(FieldCoordinate.RSV_HOME_X, 0),
			viewport.toReserveCoordinate(new Point(10, 20), 0));
		assertEquals(new FieldCoordinate(FieldCoordinate.RSV_HOME_X, 3),
			viewport.toReserveCoordinate(new Point(10, 68), 0));
	}

	@Test
	void appliesBoxTitleOffset() {
		ReserveBoxViewport viewport = viewport(ClientLayout.LANDSCAPE);

		assertEquals(new FieldCoordinate(FieldCoordinate.RSV_HOME_X, 3),
			viewport.toReserveCoordinate(new Point(0, 58), 10));
	}

	@Test
	void rejectsPointsOutsideViewportBounds() {
		ReserveBoxViewport viewport = viewport(ClientLayout.LANDSCAPE);

		assertNull(viewport.toReserveCoordinate(new Point(-1, 0), 0));
		assertNull(viewport.toReserveCoordinate(new Point(145, 0), 0));
		assertNull(viewport.toReserveCoordinate(new Point(0, 430), 0));
	}

	@Test
	void mapsPortraitContentPointUsingPortraitBoxSquareSize() {
		ReserveBoxViewport viewport = viewport(ClientLayout.PORTRAIT);

		assertEquals(new Dimension(165, 472), viewport.boxSize());
		assertEquals(new Dimension(55, 55), viewport.squareSize());
		assertEquals(new FieldCoordinate(FieldCoordinate.RSV_HOME_X, 0),
			viewport.toReserveCoordinate(new Point(0, 0), 0));
		assertEquals(new FieldCoordinate(FieldCoordinate.RSV_HOME_X, 1),
			viewport.toReserveCoordinate(new Point(55, 0), 0));
		assertEquals(new FieldCoordinate(FieldCoordinate.RSV_HOME_X, 3),
			viewport.toReserveCoordinate(new Point(0, 55), 0));
	}

	@Test
	void returnsDefensiveViewportBoundsCopies() {
		ReserveBoxViewport viewport = viewport(ClientLayout.LANDSCAPE);

		Rectangle bounds = viewport.viewportBounds();
		bounds.x = 99;

		assertEquals(new Rectangle(0, 0, 145, 430), viewport.viewportBounds());
	}

	@Test
	void setViewportBoundsUsesDefensiveCopy() {
		ReserveBoxViewport viewport = viewport(ClientLayout.LANDSCAPE);
		Rectangle bounds = new Rectangle(10, 20, 145, 430);

		viewport.setViewportBounds(bounds);
		bounds.x = 99;

		assertEquals(new Rectangle(10, 20, 145, 430), viewport.viewportBounds());
	}

	private ReserveBoxViewport viewport(ClientLayout layout) {
		return new ReserveBoxViewport(new UiDimensionProvider(new LayoutSettings(layout, 1.0)));
	}
}