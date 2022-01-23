package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.BoxType;
import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.ui.SideBarComponent;
import com.fumbbl.ffb.client.util.UtilClientPlayerDrag;
import com.fumbbl.ffb.dialog.DialogTeamSetupParameter;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.net.NetCommand;
import com.fumbbl.ffb.net.commands.ServerCommandTeamSetupList;

import javax.swing.SwingUtilities;
import java.awt.event.MouseEvent;

/**
 * 
 * @author Kalimar
 */
public class ClientStateSetup extends ClientState {

	protected boolean fLoadDialog;
	private boolean fReservesBoxOpened;

	protected ClientStateSetup(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void enterState() {
		super.enterState();
		getClient().getClientData().clear();
		SideBarComponent sideBarHome = getClient().getUserInterface().getSideBarHome();
		if (!sideBarHome.isBoxOpen()) {
			fReservesBoxOpened = true;
			sideBarHome.openBox(BoxType.RESERVES);
		}
	}

	public void leaveState() {
		SideBarComponent sideBarHome = getClient().getUserInterface().getSideBarHome();
		if (fReservesBoxOpened && (sideBarHome.getOpenBox() == BoxType.RESERVES)) {
			sideBarHome.closeBox();
		}
	}

	public ClientStateId getId() {
		return ClientStateId.SETUP;
	}

	public void mouseEntered(MouseEvent pMouseEvent) {
	}

	public void mousePressed(MouseEvent pMouseEvent) {
		UtilClientPlayerDrag.mousePressed(getClient(), pMouseEvent, false);
	}

	public void mouseDragged(MouseEvent pMouseEvent) {
		UtilClientPlayerDrag.mouseDragged(getClient(), pMouseEvent, false);
	}

	public void mouseReleased(MouseEvent pMouseEvent) {
		if (SwingUtilities.isRightMouseButton(pMouseEvent)) {
			super.mouseReleased(pMouseEvent);
		} else {
			UtilClientPlayerDrag.mouseReleased(getClient(), pMouseEvent, false);
		}
	}

	public boolean actionKeyPressed(ActionKey pActionKey) {
		boolean actionHandled = true;
		switch (pActionKey) {
		case MENU_SETUP_LOAD:
			fLoadDialog = true;
			getClient().getCommunication().sendTeamSetupLoad(null);
			break;
		case MENU_SETUP_SAVE:
			fLoadDialog = false;
			getClient().getCommunication().sendTeamSetupLoad(null);
			break;
		default:
			actionHandled = false;
			break;
		}
		return actionHandled;
	}

	@Override
	public void endTurn() {
		SideBarComponent sideBarHome = getClient().getUserInterface().getSideBarHome();
		if (sideBarHome.getOpenBox() == BoxType.RESERVES) {
			sideBarHome.closeBox();
		}
		UtilClientPlayerDrag.resetDragging(getClient());
		getClient().getCommunication().sendEndTurn(useTurnMode() ? getClient().getGame().getTurnMode() : null);
	}

	protected boolean useTurnMode() {
		return false;
	}

	public void handleCommand(NetCommand pNetCommand) {
		Game game = getClient().getGame();
		UserInterface userInterface = getClient().getUserInterface();
		switch (pNetCommand.getId()) {
			case SERVER_TEAM_SETUP_LIST:
				ServerCommandTeamSetupList setupListCommand = (ServerCommandTeamSetupList) pNetCommand;
				game.setDialogParameter(new DialogTeamSetupParameter(fLoadDialog, setupListCommand.getSetupNames()));
				userInterface.getDialogManager().updateDialog();
				break;
		default:
			break;
		}
	}

	@Override
	public boolean isInitDragAllowed(FieldCoordinate pCoordinate) {
		return true;
	}

	@Override
	public boolean isDragAllowed(FieldCoordinate pCoordinate) {
		Game game = getClient().getGame();
		return ((pCoordinate != null)
				&& ((FieldCoordinateBounds.HALF_HOME.isInBounds(pCoordinate) || pCoordinate.isBoxCoordinate())
						&& (game.getFieldModel().getPlayer(pCoordinate) == null)));
	}

	@Override
	public boolean isDropAllowed(FieldCoordinate pCoordinate) {
		return ((pCoordinate != null) && (FieldCoordinateBounds.HALF_HOME.isInBounds(pCoordinate)
				|| (pCoordinate.getX() == FieldCoordinate.RSV_HOME_X)));
	}

}
