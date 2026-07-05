package com.fumbbl.ffb.client.layout;

import com.fumbbl.ffb.client.ClientLayout;
import com.fumbbl.ffb.client.LayoutSettings;
import com.fumbbl.ffb.client.UiDimensionProvider;
import org.junit.jupiter.api.Test;

import java.awt.Dimension;
import java.awt.Rectangle;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClientLayoutCalculatorTest {

	@Test
	void calculatesCurrentLandscapePreferredSize() {
		assertEquals(new Dimension(1072, 712), layout(ClientLayout.LANDSCAPE).preferredSize());
	}

	@Test
	void calculatesCurrentPortraitPreferredSize() {
		assertEquals(new Dimension(782, 969), layout(ClientLayout.PORTRAIT).preferredSize());
	}

	@Test
	void calculatesCurrentSquarePreferredSize() {
		assertEquals(new Dimension(1044, 784), layout(ClientLayout.SQUARE).preferredSize());
	}

	@Test
	void calculatesCurrentWidePreferredSize() {
		assertEquals(new Dimension(1776, 1030), layout(ClientLayout.WIDE).preferredSize());
	}

	@Test
	void calculatesCurrentLandscapeBounds() {
		ClientLayoutResult result = layout(ClientLayout.LANDSCAPE);

		assertEquals(new Rectangle(0, 0, 145, 712), result.homeSidebarBounds());
		assertEquals(new Rectangle(145, 0, 782, 452), result.fieldBounds());
		assertEquals(new Rectangle(145, 452, 782, 32), result.scoreBarBounds());
		assertEquals(new Rectangle(146, 485, 389, 226), result.logBounds());
		assertEquals(new Rectangle(537, 485, 389, 226), result.chatBounds());
		assertEquals(new Rectangle(927, 0, 145, 712), result.awaySidebarBounds());
	}

	@Test
	void calculatesCurrentPortraitBounds() {
		ClientLayoutResult result = layout(ClientLayout.PORTRAIT);

		assertEquals(new Rectangle(0, 0, 165, 782), result.homeSidebarBounds());
		assertEquals(new Rectangle(165, 0, 452, 782), result.fieldBounds());
		assertEquals(new Rectangle(617, 0, 165, 782), result.awaySidebarBounds());
		assertEquals(new Rectangle(0, 782, 782, 32), result.scoreBarBounds());
		assertEquals(new Rectangle(1, 815, 389, 153), result.logBounds());
		assertEquals(new Rectangle(392, 815, 389, 153), result.chatBounds());
	}

	@Test
	void returnsDefensiveCopies() {
		ClientLayoutResult result = layout(ClientLayout.LANDSCAPE);

		Dimension preferredSize = result.preferredSize();
		preferredSize.width = 1;

		assertEquals(new Dimension(1072, 712), result.preferredSize());
	}

	private ClientLayoutResult layout(ClientLayout layout) {
		LayoutSettings layoutSettings = new LayoutSettings(layout, 1.0);
		UiDimensionProvider uiDimensionProvider = new UiDimensionProvider(layoutSettings);
		return new ClientLayoutCalculator().calculate(uiDimensionProvider);
	}
}