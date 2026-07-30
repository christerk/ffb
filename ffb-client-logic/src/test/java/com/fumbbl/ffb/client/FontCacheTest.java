package com.fumbbl.ffb.client;

import org.junit.jupiter.api.Test;

import java.awt.Font;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

class FontCacheTest {

	@Test
	void cachesFontsByDimensionProviderCacheKey() {
		LayoutSettings layoutSettings = new LayoutSettings(ClientLayout.LANDSCAPE, 1.0);
		UiDimensionProvider uiDimensionProvider = new UiDimensionProvider(layoutSettings);
		PitchViewport pitchViewport = new PitchViewport(uiDimensionProvider, layoutSettings);
		PitchDimensionProvider pitchDimensionProvider = new PitchDimensionProvider(layoutSettings, pitchViewport);
		FontCache fontCache = new FontCache();

		Font initial = fontCache.font(Font.BOLD, 12, pitchDimensionProvider);
		Font cached = fontCache.font(Font.BOLD, 12, pitchDimensionProvider);

		pitchViewport.setRuntimePitchScale(1.5);
		Font resized = fontCache.font(Font.BOLD, 12, pitchDimensionProvider);

		assertSame(initial, cached);
		assertNotSame(initial, resized);
		assertEquals(12, initial.getSize());
		assertEquals(18, resized.getSize());
	}
}
