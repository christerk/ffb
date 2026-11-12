package com.fumbbl.ffb.client.layer;

import com.fumbbl.ffb.client.ClientLayout;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.LayoutSettings;
import com.fumbbl.ffb.client.PitchDimensionProvider;
import com.fumbbl.ffb.client.PitchViewport;
import com.fumbbl.ffb.client.UiDimensionProvider;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class FieldLayerPitchTest {
	@Test
	void artworkDotsAlignWithGridInBothOrientationsAtMultipleScales() {
		for (ClientLayout layout : new ClientLayout[] {ClientLayout.LANDSCAPE, ClientLayout.PORTRAIT}) {
			for (int square : new int[] {30, 45}) {
				LayoutSettings settings = new LayoutSettings(layout, 1);
				UiDimensionProvider ui = new UiDimensionProvider(settings);
				PitchViewport viewport = new PitchViewport(ui, settings);
				viewport.setRuntimePitchScale(square / 30.0);
				int border = viewport.gridOffset();
				int columns = layout.isPortrait() ? 15 : 26;
				int rows = layout.isPortrait() ? 26 : 15;
				viewport.setViewportBounds(new Rectangle(0, 0, columns * square + 2 * border,
					rows * square + 2 * border));
				FieldLayerPitch layer = new FieldLayerPitch(mock(FantasyFootballClient.class), ui,
					new PitchDimensionProvider(settings, viewport), viewport, null);
				layer.initLayout();
				BufferedImage artwork = new BufferedImage(782, 452, BufferedImage.TYPE_INT_ARGB);
				Graphics2D graphics = artwork.createGraphics();
				graphics.setColor(Color.WHITE);
				graphics.fillRect(29, 29, 4, 4);
				graphics.dispose();
				layer.drawPitch(artwork);
				int x = border + square - 1;
				int y = border + (layout.isPortrait() ? 25 : 1) * square - 1;
				assertEquals(Color.WHITE.getRGB(), layer.getImage().getRGB(x, y), layout + " square " + square);
			}
		}
	}
}
