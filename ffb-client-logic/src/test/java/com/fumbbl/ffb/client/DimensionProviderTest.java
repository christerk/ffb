package com.fumbbl.ffb.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DimensionProviderTest {

	@Test
	void uiProviderUsesGuiScale() {
		LayoutSettings layoutSettings = new LayoutSettings(ClientLayout.WIDE, 2.0);

		assertEquals(2.0, new UiDimensionProvider(layoutSettings).effectiveScale(), 0.0001);
	}

	@Test
	void pitchProviderUsesPitchScaleAndLayoutPitchMultiplier() {
		LayoutSettings layoutSettings = new LayoutSettings(ClientLayout.WIDE, 2.0);

		assertEquals(3.8, new PitchDimensionProvider(layoutSettings).effectiveScale(), 0.0001);
	}

	@Test
	void dugoutProviderUsesDugoutScaleAndLayoutDugoutMultiplier() {
		LayoutSettings layoutSettings = new LayoutSettings(ClientLayout.WIDE, 2.0);

		assertEquals(2.5, new DugoutDimensionProvider(layoutSettings).effectiveScale(), 0.0001);
	}
}