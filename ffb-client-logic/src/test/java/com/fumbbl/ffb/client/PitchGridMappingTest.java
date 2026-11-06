package com.fumbbl.ffb.client;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.client.layout.ClientLayoutCalculator;
import com.fumbbl.ffb.client.layout.ClientLayoutResult;
import org.junit.jupiter.api.Test;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PitchGridMappingTest {
	@Test
	void everyTileMapsBackFromItsCornersInEveryLayout() {
		for (ClientLayout layout : ClientLayout.values()) {
			for (double scale : new double[] {0.5, 1.0, 1.37, 2.0}) {
				LayoutSettings settings = new LayoutSettings(layout, scale);
				ClientLayoutCalculator calculator = new ClientLayoutCalculator();
				ClientLayoutResult result = calculator.calculate(settings, calculator.referenceContentSize(settings));
				PitchViewport viewport = new PitchViewport(new UiDimensionProvider(settings), settings);
				viewport.setRuntimePitchScale(result.pitchScale());
				viewport.setViewportBounds(result.fieldBounds());
				ReserveBoxViewport reserve = new ReserveBoxViewport(new UiDimensionProvider(settings));
				reserve.setViewportBounds(new Rectangle(-1000, -1000, 1, 1));
				SetupDragHitTester drag = new SetupDragHitTester(viewport, reserve);
				CoordinateConverter clicks = new CoordinateConverter(viewport);
				Canvas source = new Canvas();
				int square = viewport.squareSize();
				for (int x = 0; x < 26; x++) {
					for (int y = 0; y < 15; y++) {
						FieldCoordinate coordinate = new FieldCoordinate(x, y);
						Dimension origin = viewport.toLocal(coordinate);
						assertEquals(coordinate, viewport.toFieldCoordinate(new Point(origin.width, origin.height)));
						assertEquals(coordinate, viewport.toFieldCoordinate(
							new Point(origin.width + square - 1, origin.height + square - 1)));
						assertEquals(coordinate, clicks.getFieldCoordinate(new MouseEvent(source, MouseEvent.MOUSE_RELEASED,
							0, 0, origin.width, origin.height, 1, false)));
						Point screen = viewport.worldToScreen(coordinate);
						assertEquals(coordinate, drag.toFieldCoordinate(screen, 0));
					}
				}
				Rectangle bounds = result.fieldBounds();
				assertTrue(bounds.width <= result.contentSize().width && bounds.height <= result.contentSize().height);
				assertNull(viewport.toFieldCoordinate(new Point(0, 0)));
			}
		}
	}
}
