package com.fumbbl.ffb.client.layer;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.client.ClientLayout;
import com.fumbbl.ffb.client.LayoutSettings;
import com.fumbbl.ffb.client.PitchViewport;
import com.fumbbl.ffb.client.UiDimensionProvider;
import com.fumbbl.ffb.client.layout.ClientLayoutCalculator;
import com.fumbbl.ffb.client.layout.ClientLayoutResult;
import org.junit.jupiter.api.Test;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PitchWorldRenderingTest {

	@Test
	void adjacentTranslucentSquaresHaveNeitherGapsNorDoublePaintedEdges() {
		for (ClientLayout layout : ClientLayout.values()) {
			for (double scale : new double[]{0.55, 0.73, 1.0, 1.015, 1.25, 1.85}) {
				PitchViewport viewport = viewport(layout, scale);
				TestLayer tiled = new TestLayer(viewport);
				TestLayer solid = new TestLayer(viewport);
				tiled.paintSquares(null);
				solid.paintGrid();
				assertArrayEquals(pixels(solid), pixels(tiled), layout + " at " + scale);
			}
		}
	}

	@Test
	void clearingSquareUsesItsPaintedFootprintWithoutErasingNeighbors() {
		for (ClientLayout layout : ClientLayout.values()) {
			for (double scale : new double[]{0.73, 1.015, 1.25}) {
				PitchViewport viewport = viewport(layout, scale);
				FieldCoordinate removed = new FieldCoordinate(12, 7);
				TestLayer actual = new TestLayer(viewport);
				actual.paintSquares(null);
				actual.fetchUpdatedArea();
				actual.clear(removed, true);
				TestLayer expected = new TestLayer(viewport);
				expected.paintSquares(removed);
				assertArrayEquals(pixels(expected), pixels(actual), layout + " at " + scale);
				Rectangle dirty = actual.fetchUpdatedArea();
				assertTrue(dirty.contains(viewport.localSquare(removed).getBounds()));
				actual.clear(true);
				assertTrue(Arrays.stream(pixels(actual)).allMatch(pixel -> pixel == 0));
			}
		}
	}

	@Test
	void projectedSquareFillsMatchWorldDrawingAndPixelHitTesting() {
		for (ClientLayout layout : ClientLayout.values()) {
			for (double scale : new double[]{0.73, 1.015, 1.25}) {
				PitchViewport viewport = viewport(layout, scale);
				TestLayer world = new TestLayer(viewport);
				TestLayer projected = new TestLayer(viewport);
				world.paintCoordinateColors(false);
				projected.paintCoordinateColors(true);
				assertArrayEquals(pixels(world), pixels(projected), layout + " at " + scale);
				// Cross every vertical and horizontal boundary, including the field borders.
				for (int y = 0; y < 15; y++) {
					for (int x = 0; x < 26; x++) {
						Rectangle2D cell = viewport.localSquare(new FieldCoordinate(x, y));
						int centerX = (int) cell.getCenterX();
						int centerY = (int) cell.getCenterY();
						for (int px = (int) cell.getMinX() - 1; px <= Math.ceil(cell.getMaxX()); px++) {
							assertHit(world, viewport, px, centerY);
						}
						for (int py = (int) cell.getMinY() - 1; py <= Math.ceil(cell.getMaxY()); py++) {
							assertHit(world, viewport, centerX, py);
						}
					}
				}
			}
		}
	}

	private void assertHit(TestLayer layer, PitchViewport viewport, int x, int y) {
		BufferedImage image = layer.getImage();
		if (x < 0 || y < 0 || x >= image.getWidth() || y >= image.getHeight()) {
			return;
		}
		int color = image.getRGB(x, y) & 0xffffff;
		FieldCoordinate expected = color == 0 ? null : new FieldCoordinate((color - 1) % 26, (color - 1) / 26);
		assertEquals(expected, viewport.toFieldCoordinate(new Point(x, y)), "Pixel " + x + "," + y);
	}

	private PitchViewport viewport(ClientLayout layout, double scale) {
		LayoutSettings settings = new LayoutSettings(layout, scale);
		ClientLayoutCalculator calculator = new ClientLayoutCalculator();
		Dimension size = calculator.referenceContentSize(settings);
		ClientLayoutResult result = calculator.calculate(settings, size);
		PitchViewport viewport = new PitchViewport(new UiDimensionProvider(settings), settings);
		viewport.setRuntimePitchScale(result.pitchScale());
		viewport.setViewportBounds(result.fieldBounds());
		return viewport;
	}

	private int[] pixels(TestLayer layer) {
		BufferedImage image = layer.getImage();
		return image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
	}

	private static class TestLayer extends FieldLayer {
		TestLayer(PitchViewport viewport) {
			super(null, null, null, viewport, null);
			initLayout();
		}

		void paintGrid() {
			Graphics2D graphics = createWorldGraphics();
			graphics.setColor(Color.RED);
			graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));
			graphics.fill(new Path2D.Double(new Rectangle2D.Double(0, 0,
				PitchViewport.WORLD_WIDTH, PitchViewport.WORLD_HEIGHT)));
			graphics.dispose();
		}

		void paintSquares(FieldCoordinate excluded) {
			Graphics2D graphics = createWorldGraphics();
			graphics.setColor(Color.RED);
			graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));
			for (int y = 0; y < 15; y++) {
				for (int x = 0; x < 26; x++) {
					FieldCoordinate coordinate = new FieldCoordinate(x, y);
					if (!coordinate.equals(excluded)) {
						fillWorldSquare(graphics, coordinate);
					}
				}
			}
			graphics.dispose();
		}

		void paintCoordinateColors(boolean projected) {
			Graphics2D graphics = projected ? getImage().createGraphics() : createWorldGraphics();
			for (int y = 0; y < 15; y++) {
				for (int x = 0; x < 26; x++) {
					FieldCoordinate coordinate = new FieldCoordinate(x, y);
					graphics.setColor(new Color(1 + x + 26 * y));
					if (projected) {
						fillSquare(graphics, coordinate, 0);
					} else {
						fillWorldSquare(graphics, coordinate);
					}
				}
			}
			graphics.dispose();
		}
	}
}
