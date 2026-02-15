package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.BoxType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.SetupLogicModule;
import com.fumbbl.ffb.client.ui.SideBarComponent;
import com.fumbbl.ffb.client.util.UtilClientPlayerDrag;

import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Map;

/**
 * @author Kalimar
 */
public abstract class AbstractClientStateSetup<T extends SetupLogicModule> extends ClientStateAwt<T> {

	private boolean fReservesBoxOpened;

	protected AbstractClientStateSetup(FantasyFootballClientAwt pClient, T logicModule) {
		super(pClient, logicModule);
	}

	public void setUp() {
		super.setUp();
		SideBarComponent sideBarHome = getClient().getUserInterface().getSideBarHome();
		if (!sideBarHome.isBoxOpen()) {
			fReservesBoxOpened = true;
			sideBarHome.openBox(BoxType.RESERVES);
		}
	}

	public void tearDown() {
		SideBarComponent sideBarHome = getClient().getUserInterface().getSideBarHome();
		if (fReservesBoxOpened && (sideBarHome.getOpenBox() == BoxType.RESERVES)) {
			sideBarHome.closeBox();
		}
		super.tearDown();
	}

	public void mousePressed(MouseEvent pMouseEvent) {
		synchronized (getClient()) {
			if (getClient().getCurrentMouseButton() != MouseEvent.NOBUTTON || pMouseEvent.getID() == MouseEvent.MOUSE_WHEEL) {
				return;
			}
			getClient().setCurrentMouseButton(pMouseEvent.getButton());
			UtilClientPlayerDrag.mousePressed(getClient(), pMouseEvent, false);
		}
	}

	public void mouseDragged(MouseEvent pMouseEvent) {
		synchronized (getClient()) {
			if (getClient().getCurrentMouseButton() != MouseEvent.BUTTON1) {
				return;
			}
			UtilClientPlayerDrag.mouseDragged(getClient(), pMouseEvent, false);
		}
	}

	public void mouseReleased(MouseEvent pMouseEvent) {
		synchronized (getClient()) {

			// SwingUtilities#isRightMouseButton would return true even if both buttons are pressed
			if (pMouseEvent.getButton() == MouseEvent.BUTTON3 || pMouseEvent.isShiftDown()) {
				super.mouseReleased(pMouseEvent);
			} else {
				if (getClient().getCurrentMouseButton() != pMouseEvent.getButton()) {
					return;
				}
				getClient().setCurrentMouseButton(MouseEvent.NOBUTTON);
				UtilClientPlayerDrag.mouseReleased(getClient());
			}
		}
	}

	@Override
	protected Map<Integer, ClientAction> actionMapping(int menuIndex) {
		return Collections.emptyMap();
	}

	@Override
	public void postEndTurn() {
		SideBarComponent sideBarHome = getClient().getUserInterface().getSideBarHome();
		if (sideBarHome.getOpenBox() == BoxType.RESERVES) {
			sideBarHome.closeBox();
		}
		UtilClientPlayerDrag.resetDragging(getClient());
	}

	@Override
	public boolean isInitDragAllowed(FieldCoordinate pCoordinate) {
		return true;
	}

	@Override
	public boolean isDragAllowed(FieldCoordinate pCoordinate) {
		return (pCoordinate != null)
			&& logicModule.squareIsValidForSetup(pCoordinate)
			&& logicModule.squareIsEmpty(pCoordinate);
	}

	@Override
	public boolean isDropAllowed(FieldCoordinate pCoordinate) {
		return (pCoordinate != null) && logicModule.squareIsValidForSetup(pCoordinate);
	}

}
