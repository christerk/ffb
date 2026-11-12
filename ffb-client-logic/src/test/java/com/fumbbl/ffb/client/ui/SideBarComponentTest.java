package com.fumbbl.ffb.client.ui;

import com.fumbbl.ffb.client.ClientLayout;
import com.fumbbl.ffb.client.DugoutDimensionProvider;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.FontCache;
import com.fumbbl.ffb.client.LayoutSettings;
import com.fumbbl.ffb.client.StyleProvider;
import com.fumbbl.ffb.client.UiDimensionProvider;
import com.fumbbl.ffb.client.layout.ClientLayoutCalculator;
import com.fumbbl.ffb.client.layout.ClientLayoutResult;
import com.fumbbl.ffb.client.util.MarkerService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class SideBarComponentTest {
	@Test
	void initialLayoutUsesCalculatedScaleWithoutRefreshingBeforeClientInterfaceExists() {
		LayoutSettings settings = new LayoutSettings(ClientLayout.LANDSCAPE, 0.95);
		settings.setGuiRatio(0.7);
		ClientLayoutCalculator calculator = new ClientLayoutCalculator();
		ClientLayoutResult result = calculator.calculate(settings, calculator.referenceContentSize(settings));
		UiDimensionProvider uiDimensions = new UiDimensionProvider(settings);
		uiDimensions.setRuntimeGuiScale(result.runtimeGuiScale());
		DugoutDimensionProvider dugoutDimensions = new DugoutDimensionProvider(settings);
		dugoutDimensions.setRuntimeDugoutScale(result.runtimeDugoutScale());
		FantasyFootballClient client = mock(FantasyFootballClient.class);
		SideBarComponent sidebar = new SideBarComponent(client, true, uiDimensions, dugoutDimensions,
			new StyleProvider(), new FontCache(), new MarkerService());

		sidebar.initLayout();

		assertFalse(sidebar.resizeIfNeeded());
		verify(client, never()).getUserInterface();
	}
}
