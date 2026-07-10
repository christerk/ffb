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
		assertEquals(1.0, result.pitchScale(), 0.0001);
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
		assertEquals(1.0, result.pitchScale(), 0.0001);
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
		assertEquals(1.0, result.pitchScale(), 0.0001);
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
		assertEquals(1.0, result.pitchScale(), 0.0001);
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
		assertEquals(1.0, result.pitchScale(), 0.0001);
	}

	@Test
	void calculatesDynamicLandscapeBoundsAndPitchScale() {
		ClientLayoutResult result = layout(ClientLayout.LANDSCAPE, new Dimension(1272, 812));

		assertEquals(new Dimension(1272, 812), result.preferredSize());
		assertEquals(new Rectangle(158, 0, 955, 552), result.fieldBounds());
		assertEquals(new Rectangle(0, 0, 145, 812), result.homeSidebarBounds());
		assertEquals(new Rectangle(0, 0, 145, 430), result.homeReserveBoxBounds());
		assertEquals(new Rectangle(1127, 0, 145, 812), result.awaySidebarBounds());
		assertEquals(new Rectangle(145, 552, 782, 32), result.scoreBarBounds());
		assertEquals(new Rectangle(146, 585, 389, 226), result.logBounds());
		assertEquals(new Rectangle(537, 585, 389, 226), result.chatBounds());
		assertEquals(1.2212, result.pitchScale(), 0.0001);
	}

	@Test
	void calculatesDynamicPortraitBoundsAndPitchScale() {
		ClientLayoutResult result = layout(ClientLayout.PORTRAIT, new Dimension(982, 1100));

		assertEquals(new Dimension(982, 1100), result.preferredSize());
		assertEquals(new Rectangle(227, 0, 527, 913), result.fieldBounds());
		assertEquals(new Rectangle(0, 0, 165, 913), result.homeSidebarBounds());
		assertEquals(new Rectangle(0, 0, 165, 472), result.homeReserveBoxBounds());
		assertEquals(new Rectangle(817, 0, 165, 913), result.awaySidebarBounds());
		assertEquals(new Rectangle(0, 913, 782, 32), result.scoreBarBounds());
		assertEquals(new Rectangle(1, 946, 389, 153), result.logBounds());
		assertEquals(new Rectangle(392, 946, 389, 153), result.chatBounds());
		assertEquals(1.1675, result.pitchScale(), 0.0001);
	}

	@Test
	void calculatesDynamicSquareBoundsAndPitchScale() {
		ClientLayoutResult result = layout(ClientLayout.SQUARE, new Dimension(1244, 900));

		assertEquals(new Dimension(1244, 900), result.preferredSize());
		assertEquals(new Rectangle(231, 0, 520, 900), result.fieldBounds());
		assertEquals(new Rectangle(0, 0, 165, 900), result.homeSidebarBounds());
		assertEquals(new Rectangle(0, 0, 165, 472), result.homeReserveBoxBounds());
		assertEquals(new Rectangle(817, 0, 165, 900), result.awaySidebarBounds());
		assertEquals(new Rectangle(983, 344, 260, 96), result.scoreBarBounds());
		assertEquals(new Rectangle(983, 1, 260, 343), result.logBounds());
		assertEquals(new Rectangle(983, 440, 260, 343), result.chatBounds());
		assertEquals(1.1509, result.pitchScale(), 0.0001);
	}

	@Test
	void calculatesDynamicWideBoundsAndPitchScale() {
		ClientLayoutResult result = layout(ClientLayout.WIDE, new Dimension(1920, 1080));

		assertEquals(new Dimension(1920, 1080), result.preferredSize());
		assertEquals(new Rectangle(175, 0, 1570, 907), result.fieldBounds());
		assertEquals(new Rectangle(0, 0, 145, 1080), result.homeSidebarBounds());
		assertEquals(new Rectangle(0, 0, 145, 430), result.homeReserveBoxBounds());
		assertEquals(new Rectangle(1775, 0, 145, 1080), result.awaySidebarBounds());
		assertEquals(new Rectangle(145, 907, 1486, 32), result.scoreBarBounds());
		assertEquals(new Rectangle(146, 940, 741, 139), result.logBounds());
		assertEquals(new Rectangle(889, 940, 741, 139), result.chatBounds());
	}

	@Test
	void dynamicCalculationFallsBackToFixedLayoutForInvalidAvailableSize() {
		assertEquals(layout(ClientLayout.LANDSCAPE).preferredSize(),
			layout(ClientLayout.LANDSCAPE, new Dimension(0, 0)).preferredSize());
	}

	@Test
	void dynamicLayoutFallsBackToFixedLayoutAtOrBelowFixedPreferredSize() {
		LayoutSettings layoutSettings = new LayoutSettings(ClientLayout.LANDSCAPE, 1.0);
		UiDimensionProvider uiDimensionProvider = new UiDimensionProvider(layoutSettings);
		ClientLayoutCalculator calculator = new ClientLayoutCalculator();

		ClientLayoutResult fixedLayout = calculator.calculate(uiDimensionProvider);

		ClientLayoutResult sameSize = calculator.calculate(uiDimensionProvider, fixedLayout.preferredSize());
		ClientLayoutResult narrower = calculator.calculate(uiDimensionProvider,
			new Dimension(fixedLayout.preferredSize().width - 1, fixedLayout.preferredSize().height + 100));
		ClientLayoutResult shorter = calculator.calculate(uiDimensionProvider,
			new Dimension(fixedLayout.preferredSize().width + 100, fixedLayout.preferredSize().height - 1));

		assertFixedLayout(fixedLayout, sameSize);
		assertFixedLayout(fixedLayout, narrower);
		assertFixedLayout(fixedLayout, shorter);
	}

	private ClientLayoutResult layout(ClientLayout layout) {
		LayoutSettings layoutSettings = new LayoutSettings(layout, 1.0);
		UiDimensionProvider uiDimensionProvider = new UiDimensionProvider(layoutSettings);
		return new ClientLayoutCalculator().calculate(uiDimensionProvider);
	}

	private ClientLayoutResult layout(ClientLayout layout, Dimension availableSize) {
		LayoutSettings layoutSettings = new LayoutSettings(layout, 1.0);
		UiDimensionProvider uiDimensionProvider = new UiDimensionProvider(layoutSettings);
		return new ClientLayoutCalculator().calculate(uiDimensionProvider, availableSize);
	}

	private void assertFixedLayout(ClientLayoutResult expected, ClientLayoutResult actual) {
		assertEquals(expected.preferredSize(), actual.preferredSize());
		assertEquals(expected.fieldBounds(), actual.fieldBounds());
		assertEquals(expected.homeSidebarBounds(), actual.homeSidebarBounds());
		assertEquals(expected.homeReserveBoxBounds(), actual.homeReserveBoxBounds());
		assertEquals(expected.awaySidebarBounds(), actual.awaySidebarBounds());
		assertEquals(expected.scoreBarBounds(), actual.scoreBarBounds());
		assertEquals(expected.logBounds(), actual.logBounds());
		assertEquals(expected.chatBounds(), actual.chatBounds());
		assertEquals(expected.pitchScale(), actual.pitchScale(), 0.0001);
	}
}
