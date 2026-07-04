package com.fumbbl.ffb.client.util;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.client.ClientLayout;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.LayoutSettings;
import com.fumbbl.ffb.client.PitchDimensionProvider;
import com.fumbbl.ffb.client.UiDimensionProvider;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.ui.BoxComponent;
import com.fumbbl.ffb.client.ui.SideBarComponent;
import org.junit.jupiter.api.Test;

import java.awt.Canvas;
import java.awt.event.MouseEvent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class UtilClientPlayerDragTest {

	@Test
	void landscapeFieldModeMapsFieldPixelsToCurrentCoordinates() {
		assertEquals(new FieldCoordinate(0, 0), getFieldCoordinate(ClientLayout.LANDSCAPE, mouseEventAt(0, 0), false));
		assertEquals(new FieldCoordinate(0, 0), getFieldCoordinate(ClientLayout.LANDSCAPE, mouseEventAt(1, 1), false));
		assertEquals(new FieldCoordinate(1, 0), getFieldCoordinate(ClientLayout.LANDSCAPE, mouseEventAt(30, 1), false));
		assertEquals(new FieldCoordinate(25, 14), getFieldCoordinate(ClientLayout.LANDSCAPE, mouseEventAt(750, 420), false));
	}

	@Test
	void landscapeFieldModePreservesCurrentEdgeAndPaddingBehavior() {
		assertEquals(new FieldCoordinate(26, 15), getFieldCoordinate(ClientLayout.LANDSCAPE, mouseEventAt(781, 451), false));
		assertNull(getFieldCoordinate(ClientLayout.LANDSCAPE, mouseEventAt(782, 1), false));
		assertNull(getFieldCoordinate(ClientLayout.LANDSCAPE, mouseEventAt(1, 452), false));
	}

	@Test
	void portraitFieldModePreservesCurrentDragMapping() {
		assertNull(getFieldCoordinate(ClientLayout.PORTRAIT, mouseEventAt(1, 1), false));
		assertNull(getFieldCoordinate(ClientLayout.PORTRAIT, mouseEventAt(1, 330), false));
		assertEquals(new FieldCoordinate(15, 0), getFieldCoordinate(ClientLayout.PORTRAIT, mouseEventAt(1, 331), false));
		assertEquals(new FieldCoordinate(12, 14), getFieldCoordinate(ClientLayout.PORTRAIT, mouseEventAt(420, 420), false));
		assertEquals(new FieldCoordinate(0, 0), getFieldCoordinate(ClientLayout.PORTRAIT, mouseEventAt(0, 782), false));
	}

	@Test
	void boxModeCrossesFromBoxToFieldAfterBoxWidth() {
		assertEquals(new FieldCoordinate(0, 0), getFieldCoordinate(ClientLayout.LANDSCAPE, mouseEventAt(145, 0), true));
		assertEquals(new FieldCoordinate(1, 0), getFieldCoordinate(ClientLayout.LANDSCAPE, mouseEventAt(175, 1), true));
		assertEquals(new FieldCoordinate(25, 14), getFieldCoordinate(ClientLayout.LANDSCAPE, mouseEventAt(895, 420), true));
	}

	@Test
	void fieldModeCrossesFromFieldToBoxForNegativeX() {
		assertEquals(new FieldCoordinate(FieldCoordinate.RSV_HOME_X, 3), getFieldCoordinate(ClientLayout.LANDSCAPE, mouseEventAt(-1, 0), false));
		assertEquals(new FieldCoordinate(FieldCoordinate.RSV_HOME_X, 0), getFieldCoordinate(ClientLayout.LANDSCAPE, mouseEventAt(-145, 0), false));
		assertNull(getFieldCoordinate(ClientLayout.LANDSCAPE, mouseEventAt(-146, 0), false));
	}

	@Test
	void boxModeMapsBoxPixelsToReserveCoordinates() {
		assertEquals(new FieldCoordinate(FieldCoordinate.RSV_HOME_X, 0), getFieldCoordinate(ClientLayout.LANDSCAPE, mouseEventAt(0, 0), true));
		assertEquals(new FieldCoordinate(FieldCoordinate.RSV_HOME_X, 1), getFieldCoordinate(ClientLayout.LANDSCAPE, mouseEventAt(48, 0), true));
		assertEquals(new FieldCoordinate(FieldCoordinate.RSV_HOME_X, 2), getFieldCoordinate(ClientLayout.LANDSCAPE, mouseEventAt(96, 0), true));
		assertEquals(new FieldCoordinate(FieldCoordinate.RSV_HOME_X, 3), getFieldCoordinate(ClientLayout.LANDSCAPE, mouseEventAt(144, 0), true));
		assertEquals(new FieldCoordinate(FieldCoordinate.RSV_HOME_X, 3), getFieldCoordinate(ClientLayout.LANDSCAPE, mouseEventAt(0, 48), true));
	}

	private FieldCoordinate getFieldCoordinate(ClientLayout layout, MouseEvent event, boolean boxMode) {
		return UtilClientPlayerDrag.getFieldCoordinate(client(layout, 0), event, boxMode);
	}

	private FantasyFootballClient client(ClientLayout layout, int boxTitleOffset) {
		LayoutSettings layoutSettings = new LayoutSettings(layout, 1.0);
		UiDimensionProvider uiDimensionProvider = new UiDimensionProvider(layoutSettings);
		PitchDimensionProvider pitchDimensionProvider = new PitchDimensionProvider(layoutSettings);

		FantasyFootballClient client = mock(FantasyFootballClient.class);
		UserInterface userInterface = mock(UserInterface.class);
		SideBarComponent sideBarHome = mock(SideBarComponent.class);
		BoxComponent boxComponent = mock(BoxComponent.class);

		given(client.getUserInterface()).willReturn(userInterface);
		given(userInterface.getUiDimensionProvider()).willReturn(uiDimensionProvider);
		given(userInterface.getPitchDimensionProvider()).willReturn(pitchDimensionProvider);
		given(userInterface.getSideBarHome()).willReturn(sideBarHome);
		given(sideBarHome.getBoxComponent()).willReturn(boxComponent);
		given(boxComponent.getMaxTitleOffset()).willReturn(boxTitleOffset);

		return client;
	}

	private MouseEvent mouseEventAt(int x, int y) {
		return new MouseEvent(new Canvas(), MouseEvent.MOUSE_MOVED, 0, 0, x, y, 0, false);
	}
}