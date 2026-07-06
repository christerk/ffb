package com.fumbbl.ffb.client.util;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.client.ClientLayout;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.LayoutSettings;
import com.fumbbl.ffb.client.PitchDimensionProvider;
import com.fumbbl.ffb.client.PitchViewport;
import com.fumbbl.ffb.client.ReserveBoxViewport;
import com.fumbbl.ffb.client.SetupDragHitTester;
import com.fumbbl.ffb.client.UiDimensionProvider;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.layout.ClientLayoutCalculator;
import com.fumbbl.ffb.client.layout.ClientLayoutResult;
import com.fumbbl.ffb.client.ui.BoxComponent;
import com.fumbbl.ffb.client.ui.SideBarComponent;
import org.junit.jupiter.api.Test;

import java.awt.Canvas;
import java.awt.Point;
import java.awt.event.MouseEvent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class UtilClientPlayerDragTest {

	private final Canvas fieldSource = new Canvas();
	private final Canvas boxSource = new Canvas();

	@Test
	void landscapeFieldModeMapsFieldPixelsToCurrentCoordinates() {
		assertEquals(new FieldCoordinate(0, 0), getFieldCoordinate(ClientLayout.LANDSCAPE, 0, 0, false));
		assertEquals(new FieldCoordinate(0, 0), getFieldCoordinate(ClientLayout.LANDSCAPE, 1, 1, false));
		assertEquals(new FieldCoordinate(1, 0), getFieldCoordinate(ClientLayout.LANDSCAPE, 30, 1, false));
		assertEquals(new FieldCoordinate(25, 14), getFieldCoordinate(ClientLayout.LANDSCAPE, 750, 420, false));
	}

	@Test
	void landscapeFieldModePreservesCurrentEdgeAndPaddingBehavior() {
		assertEquals(new FieldCoordinate(26, 15), getFieldCoordinate(ClientLayout.LANDSCAPE, 781, 451, false));
		assertNull(getFieldCoordinate(ClientLayout.LANDSCAPE, 782, 1, false));
		assertNull(getFieldCoordinate(ClientLayout.LANDSCAPE, 1, 452, false));
	}

	@Test
	void portraitFieldModePreservesCurrentDragMapping() {
		assertNull(getFieldCoordinate(ClientLayout.PORTRAIT, 1, 1, false));
		assertNull(getFieldCoordinate(ClientLayout.PORTRAIT, 1, 330, false));
		assertEquals(new FieldCoordinate(15, 0), getFieldCoordinate(ClientLayout.PORTRAIT, 1, 331, false));
		assertEquals(new FieldCoordinate(12, 14), getFieldCoordinate(ClientLayout.PORTRAIT, 420, 420, false));
		assertEquals(new FieldCoordinate(0, 0), getFieldCoordinate(ClientLayout.PORTRAIT, 0, 782, false));
	}

	@Test
	void boxModeCrossesFromBoxToFieldAfterBoxWidth() {
		assertEquals(new FieldCoordinate(0, 0), getFieldCoordinate(ClientLayout.LANDSCAPE, 145, 0, true));
		assertEquals(new FieldCoordinate(1, 0), getFieldCoordinate(ClientLayout.LANDSCAPE, 175, 1, true));
		assertEquals(new FieldCoordinate(25, 14), getFieldCoordinate(ClientLayout.LANDSCAPE, 895, 420, true));
	}

	@Test
	void fieldModeCrossesFromFieldToBoxForNegativeX() {
		assertEquals(new FieldCoordinate(FieldCoordinate.RSV_HOME_X, 3), getFieldCoordinate(ClientLayout.LANDSCAPE, -1, 0, false));
		assertEquals(new FieldCoordinate(FieldCoordinate.RSV_HOME_X, 0), getFieldCoordinate(ClientLayout.LANDSCAPE, -145, 0, false));
		assertNull(getFieldCoordinate(ClientLayout.LANDSCAPE, -146, 0, false));
	}

	@Test
	void boxModeMapsBoxPixelsToReserveCoordinates() {
		assertEquals(new FieldCoordinate(FieldCoordinate.RSV_HOME_X, 0), getFieldCoordinate(ClientLayout.LANDSCAPE, 0, 0, true));
		assertEquals(new FieldCoordinate(FieldCoordinate.RSV_HOME_X, 1), getFieldCoordinate(ClientLayout.LANDSCAPE, 48, 0, true));
		assertEquals(new FieldCoordinate(FieldCoordinate.RSV_HOME_X, 2), getFieldCoordinate(ClientLayout.LANDSCAPE, 96, 0, true));
		assertEquals(new FieldCoordinate(FieldCoordinate.RSV_HOME_X, 3), getFieldCoordinate(ClientLayout.LANDSCAPE, 144, 0, true));
		assertEquals(new FieldCoordinate(FieldCoordinate.RSV_HOME_X, 3), getFieldCoordinate(ClientLayout.LANDSCAPE, 0, 48, true));
	}

	private FieldCoordinate getFieldCoordinate(ClientLayout layout, int x, int y, boolean boxMode) {
		return UtilClientPlayerDrag.getFieldCoordinate(client(layout, 0), mouseEventAt(x, y, boxMode));
	}

	private FantasyFootballClient client(ClientLayout layout, int boxTitleOffset) {
		LayoutSettings layoutSettings = new LayoutSettings(layout, 1.0);
		UiDimensionProvider uiDimensionProvider = new UiDimensionProvider(layoutSettings);
		PitchDimensionProvider pitchDimensionProvider = new PitchDimensionProvider(layoutSettings);

		ClientLayoutResult layoutResult = new ClientLayoutCalculator().calculate(uiDimensionProvider);

		PitchViewport pitchViewport = new PitchViewport(uiDimensionProvider, pitchDimensionProvider);
		pitchViewport.setViewportBounds(layoutResult.fieldBounds());

		ReserveBoxViewport reserveBoxViewport = new ReserveBoxViewport(uiDimensionProvider);
		reserveBoxViewport.setViewportBounds(layoutResult.homeReserveBoxBounds());

		SetupDragHitTester setupDragHitTester = new SetupDragHitTester(
			pitchViewport, reserveBoxViewport, pitchDimensionProvider
		);

		FantasyFootballClient client = mock(FantasyFootballClient.class);
		UserInterface userInterface = mock(UserInterface.class);
		SideBarComponent sideBarHome = mock(SideBarComponent.class);
		BoxComponent boxComponent = mock(BoxComponent.class);

		given(client.getUserInterface()).willReturn(userInterface);
		given(userInterface.getSetupDragHitTester()).willReturn(setupDragHitTester);
		given(userInterface.getSideBarHome()).willReturn(sideBarHome);
		given(sideBarHome.getBoxComponent()).willReturn(boxComponent);
		given(boxComponent.getMaxTitleOffset()).willReturn(boxTitleOffset);
		given(userInterface.toClientContentPoint(any(java.awt.Component.class), any(Point.class))).willAnswer(invocation -> {
			java.awt.Component source = invocation.getArgument(0);
			Point point = invocation.getArgument(1);

			if (source == fieldSource) {
				return new Point(layoutResult.fieldBounds().x + point.x, layoutResult.fieldBounds().y + point.y);
			}
			if (source == boxSource) {
				return new Point(layoutResult.homeReserveBoxBounds().x + point.x, layoutResult.homeReserveBoxBounds().y + point.y);
			}

			return new Point(point);
		});

		return client;
	}

	private MouseEvent mouseEventAt(int x, int y, boolean boxMode) {
		return new MouseEvent(boxMode ? boxSource : fieldSource, MouseEvent.MOUSE_MOVED, 0, 0, x, y, 0, false);
	}
}
