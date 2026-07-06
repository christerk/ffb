package com.fumbbl.ffb.client;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.client.layout.ClientLayoutCalculator;
import com.fumbbl.ffb.client.layout.ClientLayoutResult;
import org.junit.jupiter.api.Test;

import java.awt.Point;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SetupDragHitTesterTest {

	@Test
	void mapsLandscapeReserveHitsFromContentCoordinates() {
		SetupDragHitTester hitTester = hitTester(ClientLayout.LANDSCAPE);

		assertEquals(new FieldCoordinate(FieldCoordinate.RSV_HOME_X, 0),
			hitTester.toFieldCoordinate(new Point(0, 0), 0));
		assertEquals(new FieldCoordinate(FieldCoordinate.RSV_HOME_X, 1),
			hitTester.toFieldCoordinate(new Point(48, 0), 0));
		assertEquals(new FieldCoordinate(FieldCoordinate.RSV_HOME_X, 2),
			hitTester.toFieldCoordinate(new Point(96, 0), 0));
		assertEquals(new FieldCoordinate(FieldCoordinate.RSV_HOME_X, 3),
			hitTester.toFieldCoordinate(new Point(144, 0), 0));
		assertEquals(new FieldCoordinate(FieldCoordinate.RSV_HOME_X, 3),
			hitTester.toFieldCoordinate(new Point(0, 48), 0));
	}

	@Test
	void mapsLandscapePitchHitsFromContentCoordinates() {
		SetupDragHitTester hitTester = hitTester(ClientLayout.LANDSCAPE);

		assertEquals(new FieldCoordinate(0, 0),
			hitTester.toFieldCoordinate(new Point(145, 0), 0));
		assertEquals(new FieldCoordinate(0, 0),
			hitTester.toFieldCoordinate(new Point(146, 1), 0));
		assertEquals(new FieldCoordinate(1, 0),
			hitTester.toFieldCoordinate(new Point(175, 1), 0));
		assertEquals(new FieldCoordinate(25, 14),
			hitTester.toFieldCoordinate(new Point(895, 420), 0));
	}

	@Test
	void preservesLandscapeSetupEdgeQuirks() {
		SetupDragHitTester hitTester = hitTester(ClientLayout.LANDSCAPE);

		assertEquals(new FieldCoordinate(26, 15),
			hitTester.toFieldCoordinate(new Point(926, 451), 0));
		assertNull(hitTester.toFieldCoordinate(new Point(927, 1), 0));
		assertNull(hitTester.toFieldCoordinate(new Point(146, 452), 0));
	}

	@Test
	void mapsPortraitReserveHitsFromContentCoordinates() {
		SetupDragHitTester hitTester = hitTester(ClientLayout.PORTRAIT);

		assertEquals(new FieldCoordinate(FieldCoordinate.RSV_HOME_X, 0),
			hitTester.toFieldCoordinate(new Point(0, 0), 0));
		assertEquals(new FieldCoordinate(FieldCoordinate.RSV_HOME_X, 1),
			hitTester.toFieldCoordinate(new Point(55, 0), 0));
		assertEquals(new FieldCoordinate(FieldCoordinate.RSV_HOME_X, 2),
			hitTester.toFieldCoordinate(new Point(110, 0), 0));
		assertEquals(new FieldCoordinate(FieldCoordinate.RSV_HOME_X, 3),
			hitTester.toFieldCoordinate(new Point(0, 55), 0));
	}

	@Test
	void mapsPortraitPitchHitsFromContentCoordinatesUsingSetupDragMath() {
		SetupDragHitTester hitTester = hitTester(ClientLayout.PORTRAIT);

		assertNull(hitTester.toFieldCoordinate(new Point(166, 1), 0));
		assertNull(hitTester.toFieldCoordinate(new Point(166, 330), 0));
		assertEquals(new FieldCoordinate(15, 0),
			hitTester.toFieldCoordinate(new Point(166, 331), 0));
		assertEquals(new FieldCoordinate(12, 14),
			hitTester.toFieldCoordinate(new Point(585, 420), 0));
		assertEquals(new FieldCoordinate(0, 0),
			hitTester.toFieldCoordinate(new Point(165, 782), 0));
	}

	@Test
	void appliesBoxTitleOffsetToReserveHits() {
		SetupDragHitTester hitTester = hitTester(ClientLayout.LANDSCAPE);

		assertEquals(new FieldCoordinate(FieldCoordinate.RSV_HOME_X, 3),
			hitTester.toFieldCoordinate(new Point(0, 58), 10));
	}

	@Test
	void returnsNullOutsideReserveAndPitchRegions() {
		SetupDragHitTester hitTester = hitTester(ClientLayout.LANDSCAPE);

		assertNull(hitTester.toFieldCoordinate(new Point(-1, 0), 0));
		assertNull(hitTester.toFieldCoordinate(new Point(1000, 0), 0));
		assertNull(hitTester.toFieldCoordinate(new Point(145, 712), 0));
	}

	private SetupDragHitTester hitTester(ClientLayout layout) {
		LayoutSettings layoutSettings = new LayoutSettings(layout, 1.0);
		UiDimensionProvider uiDimensionProvider = new UiDimensionProvider(layoutSettings);
		PitchDimensionProvider pitchDimensionProvider = new PitchDimensionProvider(layoutSettings);

		ClientLayoutResult layoutResult = new ClientLayoutCalculator().calculate(uiDimensionProvider);

		PitchViewport pitchViewport = new PitchViewport(uiDimensionProvider, pitchDimensionProvider);
		pitchViewport.setViewportBounds(layoutResult.fieldBounds());

		ReserveBoxViewport reserveBoxViewport = new ReserveBoxViewport(uiDimensionProvider);
		reserveBoxViewport.setViewportBounds(layoutResult.homeReserveBoxBounds());

		return new SetupDragHitTester(pitchViewport, reserveBoxViewport, pitchDimensionProvider);
	}
}
