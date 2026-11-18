package com.fumbbl.ffb.client.layer;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.client.ClientLayout;
import com.fumbbl.ffb.client.LayoutSettings;
import com.fumbbl.ffb.client.PitchViewport;
import com.fumbbl.ffb.client.UiDimensionProvider;
import com.fumbbl.ffb.client.layout.ClientLayoutCalculator;
import com.fumbbl.ffb.client.layout.ClientLayoutResult;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FieldLayerPitchTest {
	@Test
	void artworkAndInputUseTheSameGridInEveryLayoutAndAtDifferentSourceResolutions() {
		for (ClientLayout layout : ClientLayout.values()) {
			for (double scale : new double[]{0.73, 1.0, 1.25}) {
				LayoutSettings settings = new LayoutSettings(layout, scale);
				ClientLayoutCalculator calculator = new ClientLayoutCalculator();
				ClientLayoutResult result = calculator.calculate(settings, calculator.referenceContentSize(settings));
				PitchViewport viewport = new PitchViewport(new UiDimensionProvider(settings), settings);
				viewport.setRuntimePitchScale(result.pitchScale());
				viewport.setViewportBounds(result.fieldBounds());
				for (int resolution : new int[]{1, 2}) {
					BufferedImage artwork = new BufferedImage(782 * resolution, 452 * resolution, BufferedImage.TYPE_INT_ARGB);
					Graphics2D graphics = artwork.createGraphics();
					graphics.scale(resolution, resolution);
					for (int y = 0; y < 15; y++) {
						for (int x = 0; x < 26; x++) {
							graphics.setColor(color(x, y));
							graphics.fillRect(1 + x * 30, 1 + y * 30, 30, 30);
						}
					}
					graphics.dispose();
					FieldLayerPitch layer = new FieldLayerPitch(null, null, null, viewport, null);
					layer.initLayout();
					layer.drawPitch(artwork);
					for (int y = 0; y < 15; y++) {
						for (int x = 0; x < 26; x++) {
							FieldCoordinate coordinate = new FieldCoordinate(x, y);
							Dimension center = viewport.toLocal(coordinate, true);
							assertEquals(color(x, y).getRGB(), layer.getImage().getRGB(center.width, center.height),
								layout + " " + scale + " " + coordinate);
							assertEquals(coordinate, viewport.toFieldCoordinate(new Point(center.width, center.height)));
						}
					}
				}
			}
		}
	}

	private Color color(int x, int y) {
		return new Color(20 + x * 8, 20 + y * 15, 100);
	}
}
