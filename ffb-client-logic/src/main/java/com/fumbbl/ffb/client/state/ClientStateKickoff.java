package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.ui.SideBarComponent;
import com.fumbbl.ffb.client.util.UtilClientCursor;
import com.fumbbl.ffb.model.Player;

/**
 *
 * @author Kalimar
 */
public class ClientStateKickoff extends ClientState {

	private boolean fKicked;

	protected ClientStateKickoff(FantasyFootballClient pClient) {
		super(pClient);
	}

	public ClientStateId getId() {
		return ClientStateId.KICKOFF;
	}

	public void initUI() {
		super.initUI();
		fKicked = false;
	}

	protected void clickOnField(FieldCoordinate pCoordinate) {
		if (!fKicked) {
			placeBall(pCoordinate);
		}
	}

	protected void clickOnPlayer(Player<?> pPlayer) {
		if (!fKicked) {
			FieldCoordinate playerCoordinate = getClient().getGame().getFieldModel().getPlayerCoordinate(pPlayer);
			placeBall(playerCoordinate);
		}
	}

	private void placeBall(FieldCoordinate pCoordinate) {
		if ((pCoordinate != null) && FieldCoordinateBounds.HALF_AWAY.isInBounds(pCoordinate)) {
			getClient().getGame().getFieldModel().setBallMoving(true);
			getClient().getGame().getFieldModel().setBallCoordinate(pCoordinate);
			getClient().getUserInterface().getFieldComponent().refresh();
		}
	}

	@Override
	public void endTurn() {
		FieldCoordinate ballCoordinate = getClient().getGame().getFieldModel().getBallCoordinate();
		if ((ballCoordinate != null) && FieldCoordinateBounds.HALF_AWAY.isInBounds(ballCoordinate)) {
			fKicked = true;
			getClient().getCommunication().sendKickoff(ballCoordinate);
			getClient().getClientData().setEndTurnButtonHidden(true);
			SideBarComponent sideBarHome = getClient().getUserInterface().getSideBarHome();
			sideBarHome.refresh();
			UtilClientCursor.setDefaultCursor(getClient().getUserInterface());
		}
	}

	protected boolean mouseOverField(FieldCoordinate pCoordinate) {
		super.mouseOverField(pCoordinate);
		if (!fKicked && (pCoordinate != null) && FieldCoordinateBounds.HALF_AWAY.isInBounds(pCoordinate)) {
			UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_PASS);
		} else {
			UtilClientCursor.setDefaultCursor(getClient().getUserInterface());
		}
		return true;
	}

}
