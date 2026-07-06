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
		assertEquals(new Rectangle(0, 0, 145, 430), result.homeReserveBoxBounds());
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
		assertEquals(new Rectangle(0, 0, 165, 472), result.homeReserveBoxBounds());
	}

	@Test
	void calculatesCurrentSquareBounds() {
		ClientLayoutResult result = layout(ClientLayout.SQUARE);

		assertEquals(new Rectangle(0, 0, 165, 782), result.homeSidebarBounds());
		assertEquals(new Rectangle(165, 0, 452, 782), result.fieldBounds());
		assertEquals(new Rectangle(617, 0, 165, 782), result.awaySidebarBounds());
		assertEquals(new Rectangle(783, 344, 260, 96), result.scoreBarBounds());
		assertEquals(new Rectangle(783, 1, 260, 343), result.logBounds());
		assertEquals(new Rectangle(783, 440, 260, 343), result.chatBounds());
		assertEquals(new Rectangle(0, 0, 165, 472), result.homeReserveBoxBounds());
	}

	@Test
	void calculatesCurrentWideBounds() {
		ClientLayoutResult result = layout(ClientLayout.WIDE);

		assertEquals(new Rectangle(0, 0, 145, 1030), result.homeSidebarBounds());
		assertEquals(new Rectangle(145, 0, 1484, 857), result.fieldBounds());
		assertEquals(new Rectangle(1631, 0, 145, 1030), result.awaySidebarBounds());
		assertEquals(new Rectangle(145, 857, 1486, 32), result.scoreBarBounds());
		assertEquals(new Rectangle(146, 890, 741, 139), result.logBounds());
		assertEquals(new Rectangle(889, 890, 741, 139), result.chatBounds());
		assertEquals(new Rectangle(0, 0, 145, 430), result.homeReserveBoxBounds());
	}

	@Test
	void returnsDefensiveCopies() {
		ClientLayoutResult result = layout(ClientLayout.LANDSCAPE);

		Dimension preferredSize = result.preferredSize();
		preferredSize.width = 1;
		Rectangle fieldBounds = result.fieldBounds();
		fieldBounds.x = 1;
		Rectangle homeReserveBoxBounds = result.homeReserveBoxBounds();
		homeReserveBoxBounds.x = 1;

		assertEquals(new Dimension(1072, 712), result.preferredSize());
		assertEquals(new Rectangle(145, 0, 782, 452), result.fieldBounds());
		assertEquals(new Rectangle(0, 0, 145, 430), result.homeReserveBoxBounds());
	}

	private ClientLayoutResult layout(ClientLayout layout) {
		LayoutSettings layoutSettings = new LayoutSettings(layout, 1.0);
		UiDimensionProvider uiDimensionProvider = new UiDimensionProvider(layoutSettings);
		return new ClientLayoutCalculator().calculate(uiDimensionProvider);
	}
}