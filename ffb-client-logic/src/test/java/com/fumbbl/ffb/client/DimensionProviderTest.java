package com.fumbbl.ffb.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class DimensionProviderTest {

	@Test
	void uiProviderUsesGuiScale() {
		LayoutSettings layoutSettings = new LayoutSettings(ClientLayout.WIDE, 2.0);

		assertEquals(2.0, new UiDimensionProvider(layoutSettings).effectiveScale(), 0.0001);
	}

	@Test
	void pitchProviderUsesPitchScaleAndLayoutPitchMultiplier() {
		LayoutSettings layoutSettings = new LayoutSettings(ClientLayout.WIDE, 2.0);

		assertEquals(3.8, pitchDimensionProvider(layoutSettings).effectiveScale(), 0.0001);
	}

	@Test
	void dugoutProviderUsesDugoutScaleAndLayoutDugoutMultiplier() {
		LayoutSettings layoutSettings = new LayoutSettings(ClientLayout.WIDE, 2.0);

		assertEquals(2.5, new DugoutDimensionProvider(layoutSettings).effectiveScale(), 0.0001);
	}

	@Test
	void cacheKeyChangesWhenEffectiveScaleChanges() {
		LayoutSettings layoutSettings = new LayoutSettings(ClientLayout.LANDSCAPE, 1.0);
		UiDimensionProvider uiDimensionProvider = new UiDimensionProvider(layoutSettings);
		PitchViewport pitchViewport = new PitchViewport(uiDimensionProvider, layoutSettings);
		PitchDimensionProvider provider = new PitchDimensionProvider(layoutSettings, pitchViewport);
		String initialKey = provider.cacheKey();

		pitchViewport.setRuntimePitchScale(1.5);

		assertNotEquals(initialKey, provider.cacheKey());
	}

	@Test
	void pitchProviderUsesViewportRuntimePitchScale() {
		LayoutSettings layoutSettings = new LayoutSettings(ClientLayout.LANDSCAPE, 1.0);
		UiDimensionProvider uiDimensionProvider = new UiDimensionProvider(layoutSettings);
		PitchViewport pitchViewport = new PitchViewport(uiDimensionProvider, layoutSettings);
		PitchDimensionProvider provider = new PitchDimensionProvider(layoutSettings, pitchViewport);

		pitchViewport.setRuntimePitchScale(1.5);

		assertEquals(1.5, provider.effectiveScale(), 0.0001);
		assertEquals(1.0, layoutSettings.getPitchScale(), 0.0001);
	}

	private PitchDimensionProvider pitchDimensionProvider(LayoutSettings layoutSettings) {
		UiDimensionProvider uiDimensionProvider = new UiDimensionProvider(layoutSettings);
		PitchViewport pitchViewport = new PitchViewport(uiDimensionProvider, layoutSettings);
		return new PitchDimensionProvider(layoutSettings, pitchViewport);
	}
}
