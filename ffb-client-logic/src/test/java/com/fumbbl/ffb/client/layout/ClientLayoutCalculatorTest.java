package com.fumbbl.ffb.client.layout;

import com.fumbbl.ffb.client.ClientLayout;
import com.fumbbl.ffb.client.Component;
import com.fumbbl.ffb.client.LayoutSettings;
import org.junit.jupiter.api.Test;

import java.awt.Dimension;
import java.awt.Rectangle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClientLayoutCalculatorTest {

	@Test
	void measuresLandscapeFromTheGameAreaAndBottomInfoArea() {
		assertEquals(new Dimension(1072, 712), naturalSize(ClientLayout.LANDSCAPE));
	}

	@Test
	void measuresPortraitFromTheGameAreaAndBottomInfoArea() {
		assertEquals(new Dimension(782, 969), naturalSize(ClientLayout.PORTRAIT));
	}

	@Test
	void measuresSquareFromTheGameAreaAndSideInfoArea() {
		assertEquals(new Dimension(1044, 784), naturalSize(ClientLayout.SQUARE));
	}

	@Test
	void measuresWideFromTheGameAreaAndBottomInfoArea() {
		assertEquals(new Dimension(1776, 1030), naturalSize(ClientLayout.WIDE));
	}

	@Test
	void arrangesLandscapeAsGameAreaAboveBottomInfoArea() {
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
	void arrangesPortraitAsGameAreaAboveBottomInfoArea() {
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
	void arrangesSquareAsGameAreaBesideSideInfoArea() {
		ClientLayoutResult result = layout(ClientLayout.SQUARE);

		assertEquals(new Rectangle(0, 0, 165, 784), result.homeSidebarBounds());
		assertEquals(new Rectangle(165, 0, 452, 782), result.fieldBounds());
		assertEquals(new Rectangle(617, 0, 165, 784), result.awaySidebarBounds());
		assertEquals(new Rectangle(783, 344, 260, 96), result.scoreBarBounds());
		assertEquals(new Rectangle(783, 1, 260, 343), result.logBounds());
		assertEquals(new Rectangle(783, 440, 260, 343), result.chatBounds());
		assertEquals(new Rectangle(0, 0, 165, 472), result.homeReserveBoxBounds());
		assertEquals(1.0, result.pitchScale(), 0.0001);
	}

	@Test
	void arrangesWideAsGameAreaAboveBottomInfoArea() {
		ClientLayoutResult result = layout(ClientLayout.WIDE);

		assertEquals(new Rectangle(0, 0, 145, 1030), result.homeSidebarBounds());
		assertEquals(new Rectangle(158, 0, 1460, 844), result.fieldBounds());
		assertEquals(new Rectangle(1631, 0, 145, 1030), result.awaySidebarBounds());
		assertEquals(new Rectangle(145, 844, 1486, 32), result.scoreBarBounds());
		assertEquals(new Rectangle(146, 877, 741, 139), result.logBounds());
		assertEquals(new Rectangle(889, 877, 741, 139), result.chatBounds());
		assertEquals(new Rectangle(0, 0, 145, 430), result.homeReserveBoxBounds());
		assertEquals(56.0 / 57.0, result.pitchScale(), 0.0001);
		assertEquals(1.0, result.runtimeGuiScale(), 0.0001);
		assertEquals(1.0, result.runtimeDugoutScale(), 0.0001);
	}

	@Test
	void returnsDefensiveCopies() {
		ClientLayoutResult result = layout(ClientLayout.LANDSCAPE);

		Dimension contentSize = result.contentSize();
		contentSize.width = 1;
		Rectangle fieldBounds = result.fieldBounds();
		fieldBounds.x = 1;
		Rectangle homeReserveBoxBounds = result.homeReserveBoxBounds();
		homeReserveBoxBounds.x = 1;

		assertEquals(new Dimension(1072, 712), result.contentSize());
		assertEquals(new Rectangle(145, 0, 782, 452), result.fieldBounds());
		assertEquals(new Rectangle(0, 0, 145, 430), result.homeReserveBoxBounds());
		assertEquals(1.0, result.pitchScale(), 0.0001);
	}

	@Test
	void naturalSquareSizeUsesConfiguredPitchScaleIndependentlyFromGuiScale() {
		LayoutSettings layoutSettings = new LayoutSettings(ClientLayout.SQUARE, 0.5);
		layoutSettings.setGuiRatio(2.0);

		assertEquals(new Dimension(818, 784), new ClientLayoutCalculator().naturalContentSize(layoutSettings));
	}

	@Test
	void startupReferenceSizeKeepsSavedGuiScale() {
		LayoutSettings settings = new LayoutSettings(ClientLayout.LANDSCAPE, 0.95);
		settings.setGuiRatio(0.7);
		ClientLayoutCalculator calculator = new ClientLayoutCalculator();

		ClientLayoutResult result = calculator.calculate(settings, calculator.referenceContentSize(settings));

		assertEquals(0.665, result.runtimeGuiScale(), 0.0001);
		assertEquals(96, result.homeSidebarBounds().width);
	}

	@Test
	void referenceSizeKeepsSidebarsBesidePitchAtEachGuiScale() {
		for (ClientLayout layout : ClientLayout.values()) {
			for (double ratio : new double[] {0.25, 0.7, 1.0}) {
				LayoutSettings settings = new LayoutSettings(layout, 1.0);
				settings.setGuiRatio(ratio);
				ClientLayoutCalculator calculator = new ClientLayoutCalculator();
				ClientLayoutResult result = calculator.calculate(settings, calculator.referenceContentSize(settings));
				Rectangle pitch = result.fieldBounds();
				Rectangle home = result.homeSidebarBounds();
				Rectangle away = result.awaySidebarBounds();
				assertTrue(pitch.x >= home.x + home.width, layout + " GUI " + ratio);
				assertTrue(pitch.x + pitch.width <= away.x, layout + " GUI " + ratio);
			}
		}
	}

	@Test
	void naturalContentSizeProducesConfiguredScaleLayout() {
		for (ClientLayout layout : new ClientLayout[] {
			ClientLayout.LANDSCAPE, ClientLayout.PORTRAIT, ClientLayout.SQUARE, ClientLayout.WIDE
		}) {
			assertNaturalContentSizeProducesConfiguredScaleLayout(layout, 0.25);
			assertNaturalContentSizeProducesConfiguredScaleLayout(layout, 1.0);
		}
	}

	@Test
	void resizingPreservesGuiAndPitchProportionsAcrossLayoutsAndWindowShapes() {
		for (ClientLayout layout : ClientLayout.values()) {
			LayoutSettings settings = new LayoutSettings(layout, 1.0);
			settings.setGuiRatio(0.75);
			ClientLayoutCalculator calculator = new ClientLayoutCalculator();
			Dimension natural = calculator.referenceContentSize(settings);
			ClientLayoutResult base = calculator.calculate(settings, natural);
			ClientLayoutResult enlarged = calculator.calculate(settings,
				new Dimension(natural.width * 2, natural.height * 2));
			ClientLayoutResult wider = calculator.calculate(settings,
				new Dimension(natural.width * 3, natural.height * 2));
			ClientLayoutResult taller = calculator.calculate(settings,
				new Dimension(natural.width * 2, natural.height * 3));

			assertEquals(base.runtimeGuiScale() * 2, enlarged.runtimeGuiScale(), 0.0001);
			assertEquals(base.runtimeDugoutScale() * 2, enlarged.runtimeDugoutScale(), 0.0001);
			assertEquals(base.pitchScale() * 2, enlarged.pitchScale(), 0.04);
			assertEquals(enlarged.runtimeGuiScale(), wider.runtimeGuiScale(), 0.0001);
			assertEquals(enlarged.pitchScale(), wider.pitchScale(), 0.0001);
			assertTrue(wider.homeSidebarBounds().x > enlarged.homeSidebarBounds().x);
			assertEquals(enlarged.runtimeGuiScale(), taller.runtimeGuiScale(), 0.0001);
			assertEquals(enlarged.pitchScale(), taller.pitchScale(), 0.0001);
			assertTrue(taller.homeSidebarBounds().y > enlarged.homeSidebarBounds().y);
		}
	}

	@Test
	void minimumContentSizePreservesGuiRatioWhenOverallScaleChanges() {
		LayoutSettings settings = new LayoutSettings(ClientLayout.LANDSCAPE, 1.0);
		settings.setGuiRatio(0.75);
		ClientLayoutCalculator calculator = new ClientLayoutCalculator();
		Dimension minimum = calculator.minimumContentSize(settings);
		settings.setScale(2.0);

		assertEquals(minimum, calculator.minimumContentSize(settings));
	}

	@Test
	void increasingGuiRatioIncreasesGuiRelativeToPitch() {
		LayoutSettings settings = new LayoutSettings(ClientLayout.LANDSCAPE, 1.0);
		ClientLayoutCalculator calculator = new ClientLayoutCalculator();
		Dimension content = new Dimension(1500, 1000);
		settings.setGuiRatio(0.5);
		ClientLayoutResult normal = calculator.calculate(settings, content);
		settings.setGuiRatio(1.0);
		ClientLayoutResult largerGui = calculator.calculate(settings, content);

		assertTrue(largerGui.runtimeGuiScale() / largerGui.pitchScale()
			> normal.runtimeGuiScale() / normal.pitchScale());
	}

	@Test
	void guiLimitKeepsPanelsInsideTheLayoutAndLeavesAtLeastHalfSizePitch() {
		for (ClientLayout layout : ClientLayout.values()) {
			LayoutSettings settings = new LayoutSettings(layout, 1.0);
			ClientLayoutCalculator calculator = new ClientLayoutCalculator();
			Dimension content = calculator.naturalContentSize(settings);
			double limit = LayoutSettings.MAX_GUI_RATIO;
			settings.setGuiRatio(limit);
			ClientLayoutResult result = calculator.calculate(settings, content);

			assertTrue(result.pitchScale() >= 0.5 - 0.01, layout.toString());
			Rectangle bounds = new Rectangle(content);
			assertTrue(bounds.contains(result.fieldBounds()), layout.toString());
			assertTrue(bounds.contains(result.homeSidebarBounds()), layout.toString());
			assertTrue(bounds.contains(result.awaySidebarBounds()), layout.toString());
			assertTrue(bounds.contains(result.scoreBarBounds()), layout.toString());
			assertTrue(bounds.contains(result.logBounds()), layout.toString());
			assertTrue(bounds.contains(result.chatBounds()), layout.toString());

			settings.setGuiRatio(limit + 0.5);
			ClientLayoutResult aboveLimit = calculator.calculate(settings, content);
			assertEquals(result.runtimeGuiScale(), aboveLimit.runtimeGuiScale(), 0.0001, layout.toString());
			assertEquals(result.fieldBounds(), aboveLimit.fieldBounds(), layout.toString());
		}
	}

	@Test
	void guiRatioChangesComposedSizeWithoutChangingPitchScale() {
		for (ClientLayout layout : ClientLayout.values()) {
			LayoutSettings settings = new LayoutSettings(layout, 1.0);
			ClientLayoutCalculator calculator = new ClientLayoutCalculator();
			settings.setGuiRatio(0.25);
			Dimension smallContent = calculator.referenceContentSize(settings);
			ClientLayoutResult small = calculator.calculate(settings, smallContent);
			settings.setGuiRatio(LayoutSettings.MAX_GUI_RATIO);
			Dimension largeContent = calculator.referenceContentSize(settings);
			ClientLayoutResult large = calculator.calculate(settings, largeContent);

			assertEquals(smallContent, small.contentSize());
			assertEquals(largeContent, large.contentSize());
			assertTrue(large.runtimeGuiScale() > small.runtimeGuiScale(), layout.toString());
			assertEquals(small.pitchScale(), large.pitchScale(), 0.01, layout.toString());
		}
	}

	@Test
	void outOfRangeGuiRatioIsClampedForBothGuiAndDugoutWithoutChangingSettings() {
		LayoutSettings settings = new LayoutSettings(ClientLayout.LANDSCAPE, 1.0);
		settings.setGuiRatio(0.0);
		ClientLayoutCalculator calculator = new ClientLayoutCalculator();
		Dimension content = calculator.referenceContentSize(settings);

		ClientLayoutResult result = calculator.calculate(settings, content);

		assertEquals(LayoutSettings.MIN_GUI_RATIO, result.runtimeGuiScale(), 0.0001);
		assertEquals(result.runtimeGuiScale(), result.runtimeDugoutScale(), 0.0001);
		assertEquals(0.0, settings.getGuiRatio());
	}

	@Test
	void smallestGuiRatioProducesPositiveComponentSizesInEveryLayout() {
		for (ClientLayout layout : ClientLayout.values()) {
			LayoutSettings settings = new LayoutSettings(layout, LayoutSettings.MIN_SCALE_FACTOR);
			ClientLayoutCalculator calculator = new ClientLayoutCalculator();
			settings.setGuiRatio(LayoutSettings.MIN_GUI_RATIO);
			Dimension content = calculator.referenceContentSize(settings);
			ClientLayoutResult result = calculator.calculate(settings, content);

			assertEquals(0.125, result.runtimeGuiScale(), 0.0001, layout.toString());
			assertTrue(result.homeSidebarBounds().width > 0, layout.toString());
			assertTrue(result.scoreBarBounds().height > 0, layout.toString());
			assertTrue(result.logBounds().width > 0 && result.logBounds().height > 0, layout.toString());
			assertTrue(result.chatBounds().width > 0 && result.chatBounds().height > 0, layout.toString());
		}
	}

	private void assertNaturalContentSizeProducesConfiguredScaleLayout(ClientLayout layout, double guiScale) {
		LayoutSettings layoutSettings = new LayoutSettings(layout, 1.0);
		ClientLayoutCalculator calculator = new ClientLayoutCalculator();
		layoutSettings.setGuiRatio(guiScale);
		Dimension naturalContentSize = calculator.referenceContentSize(layoutSettings);
		ClientLayoutResult result = calculator.calculate(layoutSettings, naturalContentSize);

		assertEquals(layoutSettings.getGuiScale(), result.runtimeGuiScale(), 0.0001);
		assertEquals(dimension(layoutSettings, Component.SCORE_BOARD), result.scoreBarBounds().getSize());
		assertEquals(dimension(layoutSettings, Component.LOG), result.logBounds().getSize());
		assertEquals(dimension(layoutSettings, Component.CHAT), result.chatBounds().getSize());
		assertEquals(dimension(layoutSettings, Component.SIDEBAR).width, result.homeSidebarBounds().width);
		assertEquals(dimension(layoutSettings, Component.SIDEBAR).width, result.awaySidebarBounds().width);
	}

	private ClientLayoutResult layout(ClientLayout layout) {
		LayoutSettings layoutSettings = new LayoutSettings(layout, 1.0);
		return new ClientLayoutCalculator().calculate(layoutSettings, defaultLayoutSize(layout));
	}

	private Dimension naturalSize(ClientLayout layout) {
		LayoutSettings layoutSettings = new LayoutSettings(layout, 1.0);
		return new ClientLayoutCalculator().naturalContentSize(layoutSettings);
	}

	private Dimension defaultLayoutSize(ClientLayout layout) {
		switch (layout) {
			case PORTRAIT:
				return new Dimension(782, 969);
			case SQUARE:
				return new Dimension(1044, 784);
			case WIDE:
				return new Dimension(1776, 1030);
			default:
				return new Dimension(1072, 712);
		}
	}

	private ClientLayoutResult layout(ClientLayout layout, Dimension availableSize) {
		LayoutSettings layoutSettings = new LayoutSettings(layout, 1.0);
		return new ClientLayoutCalculator().calculate(layoutSettings, availableSize);
	}

	private Dimension dimension(LayoutSettings layoutSettings, Component component) {
		return scale(component.dimension(layoutSettings.getLayout()), layoutSettings.getGuiScale());
	}

	private Dimension scale(Dimension dimension, double scale) {
		return new Dimension((int) (dimension.width * scale), (int) (dimension.height * scale));
	}
}
