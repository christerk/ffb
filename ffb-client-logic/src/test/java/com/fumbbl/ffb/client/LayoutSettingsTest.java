package com.fumbbl.ffb.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LayoutSettingsTest {

	@Test
	void constructorInitializesAllScaleConceptsFromCompatibilityScale() {
		LayoutSettings layoutSettings = new LayoutSettings(ClientLayout.LANDSCAPE, 1.5);

		assertEquals(1.5, layoutSettings.getScale());
		assertEquals(1.5, layoutSettings.getGuiScale());
		assertEquals(1.5, layoutSettings.getPitchScale());
		assertEquals(1.5, layoutSettings.getDugoutScale());
	}

	@Test
	void setScaleUpdatesAllScaleConceptsForCurrentCompatibilityBehavior() {
		LayoutSettings layoutSettings = new LayoutSettings(ClientLayout.LANDSCAPE, 1.0);

		layoutSettings.setScale(2.0);

		assertEquals(2.0, layoutSettings.getScale());
		assertEquals(2.0, layoutSettings.getGuiScale());
		assertEquals(2.0, layoutSettings.getPitchScale());
		assertEquals(2.0, layoutSettings.getDugoutScale());
	}

	@Test
	void effectivePitchScalePreservesCurrentLayoutMultiplierBehavior() {
		LayoutSettings layoutSettings = new LayoutSettings(ClientLayout.WIDE, 2.0);

		assertEquals(3.8, layoutSettings.effectivePitchScale(), 0.0001);
	}

	@Test
	void effectiveDugoutScalePreservesCurrentLayoutMultiplierBehavior() {
		LayoutSettings layoutSettings = new LayoutSettings(ClientLayout.WIDE, 2.0);

		assertEquals(2.5, layoutSettings.effectiveDugoutScale(), 0.0001);
	}

	@Test
	void largerAndSmallerScaleUseCompatibilityGuiScale() {
		LayoutSettings layoutSettings = new LayoutSettings(ClientLayout.LANDSCAPE, 1.0);

		assertEquals(1.05, layoutSettings.largerScale(), 0.0001);
		assertEquals(0.95, layoutSettings.smallerScale(), 0.0001);
	}

	@Test
	void largerAndSmallerScaleClampToCurrentBounds() {
		LayoutSettings max = new LayoutSettings(ClientLayout.LANDSCAPE, LayoutSettings.MAX_SCALE_FACTOR);
		LayoutSettings min = new LayoutSettings(ClientLayout.LANDSCAPE, LayoutSettings.MIN_SCALE_FACTOR);

		assertEquals(LayoutSettings.MAX_SCALE_FACTOR, max.largerScale());
		assertEquals(LayoutSettings.MIN_SCALE_FACTOR, min.smallerScale());
	}
}
