package com.fumbbl.ffb.client;

import com.fumbbl.ffb.Direction;
import com.fumbbl.ffb.FieldCoordinate;
import org.junit.jupiter.api.Test;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PitchViewportTest {

	private PitchViewport viewport(ClientLayout layout) {
		LayoutSettings layoutSettings = new LayoutSettings(layout, 1.0);
		UiDimensionProvider uiDimensionProvider = new UiDimensionProvider(layoutSettings);
		PitchDimensionProvider pitchDimensionProvider = new PitchDimensionProvider(layoutSettings);
		return new PitchViewport(uiDimensionProvider, pitchDimensionProvider);
	}

	@Test
	void fieldSizeUsesCurrentLandscapeFieldDimension() {
		PitchViewport viewport = viewport(ClientLayout.LANDSCAPE);

		assertEquals(new Dimension(782, 452), viewport.fieldSize());
	}

	@Test
	void fieldSizeUsesCurrentPortraitFieldDimension() {
		PitchViewport viewport = viewport(ClientLayout.PORTRAIT);

		assertEquals(new Dimension(452, 782), viewport.fieldSize());
	}

	@Test
	void squareSizeUsesCurrentPitchSquareSize() {
		PitchViewport viewport = viewport(ClientLayout.LANDSCAPE);

		assertEquals(30, viewport.squareSize());
	}

	@Test
	void squareSizeWithFactorUsesCurrentPitchSquareSizeRounding() {
		PitchViewport viewport = viewport(ClientLayout.LANDSCAPE);

		assertEquals(15, viewport.squareSize(0.5));
		assertEquals(750, viewport.squareSize(25));
		assertEquals(765, viewport.squareSize(25.5));
	}

	@Test
	void imageOffsetUsesCurrentPitchImageOffset() {
		PitchViewport viewport = viewport(ClientLayout.LANDSCAPE);

		assertEquals(15, viewport.imageOffset());
	}

	@Test
	void landscapeCoordinateMapsToUpperLeftLocalSquare() {
		PitchViewport viewport = viewport(ClientLayout.LANDSCAPE);

		assertEquals(new Dimension(0, 0), viewport.toLocal(new FieldCoordinate(0, 0)));
		assertEquals(new Dimension(750, 420), viewport.toLocal(new FieldCoordinate(25, 14)));
	}

	@Test
	void landscapeCoordinateMapsToLocalSquareCenter() {
		PitchViewport viewport = viewport(ClientLayout.LANDSCAPE);

		assertEquals(new Dimension(15, 15), viewport.toLocal(new FieldCoordinate(0, 0), true));
		assertEquals(new Dimension(765, 435), viewport.toLocal(new FieldCoordinate(25, 14), true));
	}

	@Test
	void portraitCoordinateMapsToUpperLeftLocalSquare() {
		PitchViewport viewport = viewport(ClientLayout.PORTRAIT);

		assertEquals(new Dimension(0, 750), viewport.toLocal(new FieldCoordinate(0, 0)));
		assertEquals(new Dimension(420, 0), viewport.toLocal(new FieldCoordinate(25, 14)));
	}

	@Test
	void portraitCoordinateMapsToLocalSquareCenter() {
		PitchViewport viewport = viewport(ClientLayout.PORTRAIT);

		assertEquals(new Dimension(15, 765), viewport.toLocal(new FieldCoordinate(0, 0), true));
		assertEquals(new Dimension(435, 15), viewport.toLocal(new FieldCoordinate(25, 14), true));
	}

	@Test
	void landscapeLocalPointMapsToFieldCoordinate() {
		PitchViewport viewport = viewport(ClientLayout.LANDSCAPE);

		assertEquals(new FieldCoordinate(0, 0), viewport.toFieldCoordinate(new Point(1, 1)));
		assertEquals(new FieldCoordinate(1, 0), viewport.toFieldCoordinate(new Point(30, 1)));
		assertEquals(new FieldCoordinate(25, 14), viewport.toFieldCoordinate(new Point(750, 420)));
	}

	@Test
	void portraitLocalPointMapsToFieldCoordinateUsingCurrentCoordinateConverterBehavior() {
		PitchViewport viewport = viewport(ClientLayout.PORTRAIT);

		assertEquals(new FieldCoordinate(25, 0), viewport.toFieldCoordinate(new Point(1, 1)));
		assertEquals(new FieldCoordinate(24, 0), viewport.toFieldCoordinate(new Point(1, 30)));
		assertEquals(new FieldCoordinate(11, 14), viewport.toFieldCoordinate(new Point(420, 420)));
	}

	@Test
	void localPointOutsideStrictCurrentBoundsReturnsNull() {
		PitchViewport viewport = viewport(ClientLayout.LANDSCAPE);

		assertNull(viewport.toFieldCoordinate(new Point(0, 1)));
		assertNull(viewport.toFieldCoordinate(new Point(1, 0)));
		assertNull(viewport.toFieldCoordinate(new Point(782, 1)));
		assertNull(viewport.toFieldCoordinate(new Point(1, 452)));
	}

	@Test
	void landscapeDirectionsAreUnchanged() {
		PitchViewport viewport = viewport(ClientLayout.LANDSCAPE);

		assertEquals(Direction.NORTH, viewport.toLocal(Direction.NORTH));
		assertEquals(Direction.EAST, viewport.toLocal(Direction.EAST));
		assertEquals(Direction.SOUTH, viewport.toLocal(Direction.SOUTH));
		assertEquals(Direction.WEST, viewport.toLocal(Direction.WEST));
	}

	@Test
	void portraitDirectionsUseCurrentRemapping() {
		PitchViewport viewport = viewport(ClientLayout.PORTRAIT);

		assertEquals(Direction.WEST, viewport.toLocal(Direction.NORTH));
		assertEquals(Direction.NORTH, viewport.toLocal(Direction.EAST));
		assertEquals(Direction.EAST, viewport.toLocal(Direction.SOUTH));
		assertEquals(Direction.SOUTH, viewport.toLocal(Direction.WEST));
		assertEquals(Direction.NORTHWEST, viewport.toLocal(Direction.NORTHEAST));
		assertEquals(Direction.NORTHEAST, viewport.toLocal(Direction.SOUTHEAST));
		assertEquals(Direction.SOUTHEAST, viewport.toLocal(Direction.SOUTHWEST));
		assertEquals(Direction.SOUTHWEST, viewport.toLocal(Direction.NORTHWEST));
	}

	@Test
	void localDirectionDelegatesToCurrentProviderBehavior() {
		PitchViewport viewport = viewport(ClientLayout.PORTRAIT);

		assertEquals(Direction.NORTH, viewport.getLocalDirection(
			new FieldCoordinate(0, 0),
			new FieldCoordinate(1, 0)
		));
	}

	@Test
	void viewportBoundsDefaultsToCurrentLandscapeFieldSizeAtOrigin() {
		PitchViewport viewport = viewport(ClientLayout.LANDSCAPE);

		assertEquals(new Rectangle(0, 0, 782, 452), viewport.viewportBounds());
	}

	@Test
	void viewportBoundsDefaultsToCurrentPortraitFieldSizeAtOrigin() {
		PitchViewport viewport = viewport(ClientLayout.PORTRAIT);

		assertEquals(new Rectangle(0, 0, 452, 782), viewport.viewportBounds());
	}

	@Test
	void viewportBoundsCanBeUpdatedFromLayoutResult() {
		PitchViewport viewport = viewport(ClientLayout.LANDSCAPE);

		viewport.setViewportBounds(new Rectangle(145, 0, 782, 452));

		assertEquals(new Rectangle(145, 0, 782, 452), viewport.viewportBounds());
	}

	@Test
	void viewportBoundsUsesDefensiveCopies() {
		PitchViewport viewport = viewport(ClientLayout.LANDSCAPE);
		Rectangle bounds = new Rectangle(145, 0, 782, 452);

		viewport.setViewportBounds(bounds);
		bounds.x = 999;

		assertEquals(new Rectangle(145, 0, 782, 452), viewport.viewportBounds());

		Rectangle returned = viewport.viewportBounds();
		returned.x = 999;

		assertEquals(new Rectangle(145, 0, 782, 452), viewport.viewportBounds());
	}

	@Test
	void landscapeWorldCoordinateMapsToScreenPointUsingViewportOffset() {
		PitchViewport viewport = viewport(ClientLayout.LANDSCAPE);
		viewport.setViewportBounds(new Rectangle(145, 0, 782, 452));

		assertEquals(new Point(145, 0), viewport.worldToScreen(new FieldCoordinate(0, 0)));
		assertEquals(new Point(895, 420), viewport.worldToScreen(new FieldCoordinate(25, 14)));
	}

	@Test
	void landscapeWorldCoordinateMapsToCenteredScreenPointUsingViewportOffset() {
		PitchViewport viewport = viewport(ClientLayout.LANDSCAPE);
		viewport.setViewportBounds(new Rectangle(145, 0, 782, 452));

		assertEquals(new Point(160, 15), viewport.worldToScreen(new FieldCoordinate(0, 0), true));
		assertEquals(new Point(910, 435), viewport.worldToScreen(new FieldCoordinate(25, 14), true));
	}

	@Test
	void portraitWorldCoordinateMapsToScreenPointUsingViewportOffset() {
		PitchViewport viewport = viewport(ClientLayout.PORTRAIT);
		viewport.setViewportBounds(new Rectangle(165, 0, 452, 782));

		assertEquals(new Point(165, 750), viewport.worldToScreen(new FieldCoordinate(0, 0)));
		assertEquals(new Point(585, 0), viewport.worldToScreen(new FieldCoordinate(25, 14)));
	}

	@Test
	void landscapeScreenPointMapsToWorldCoordinateUsingViewportOffset() {
		PitchViewport viewport = viewport(ClientLayout.LANDSCAPE);
		viewport.setViewportBounds(new Rectangle(145, 0, 782, 452));

		assertEquals(new FieldCoordinate(0, 0), viewport.screenToWorld(new Point(146, 1)));
		assertEquals(new FieldCoordinate(1, 0), viewport.screenToWorld(new Point(175, 1)));
		assertEquals(new FieldCoordinate(25, 14), viewport.screenToWorld(new Point(895, 420)));
	}

	@Test
	void portraitScreenPointMapsToWorldCoordinateUsingViewportOffset() {
		PitchViewport viewport = viewport(ClientLayout.PORTRAIT);
		viewport.setViewportBounds(new Rectangle(165, 0, 452, 782));

		assertEquals(new FieldCoordinate(25, 0), viewport.screenToWorld(new Point(166, 1)));
		assertEquals(new FieldCoordinate(24, 0), viewport.screenToWorld(new Point(166, 30)));
		assertEquals(new FieldCoordinate(11, 14), viewport.screenToWorld(new Point(585, 420)));
	}

	@Test
	void screenPointOutsideViewportMapsToNullUsingCurrentLocalBounds() {
		PitchViewport viewport = viewport(ClientLayout.LANDSCAPE);
		viewport.setViewportBounds(new Rectangle(145, 0, 782, 452));

		assertNull(viewport.screenToWorld(new Point(145, 1)));
		assertNull(viewport.screenToWorld(new Point(146, 0)));
		assertNull(viewport.screenToWorld(new Point(927, 1)));
		assertNull(viewport.screenToWorld(new Point(146, 452)));
	}
}
