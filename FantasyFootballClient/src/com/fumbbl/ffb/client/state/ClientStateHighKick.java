package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.BoxType;
import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.ui.SideBarComponent;
import com.fumbbl.ffb.client.util.UtilClientCursor;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

/**
 *
 * @author Kalimar
 */
public class ClientStateHighKick extends ClientState {

	private FieldCoordinate fOldCoordinate;

	protected ClientStateHighKick(FantasyFootballClient pClient) {
		super(pClient);
	}

	public ClientStateId getId() {
		return ClientStateId.HIGH_KICK;
	}

	public void enterState() {
		super.enterState();
		setClickable(true);
		fOldCoordinate = null;
	}

	protected void clickOnPlayer(Player<?> pPlayer) {
		if (isPlayerSelectable(pPlayer)) {
			Game game = getClient().getGame();
			Player<?> oldPlayer = game.getFieldModel().getPlayer(game.getFieldModel().getBallCoordinate());
			if (pPlayer != oldPlayer) {
				if ((oldPlayer != null) && (fOldCoordinate != null)) {
					getClient().getCommunication().sendSetupPlayer(oldPlayer, fOldCoordinate);
				}
				fOldCoordinate = game.getFieldModel().getPlayerCoordinate(pPlayer);
				getClient().getCommunication().sendSetupPlayer(pPlayer, game.getFieldModel().getBallCoordinate());
			}
		}
	}

	protected boolean mouseOverPlayer(Player<?> pPlayer) {
		super.mouseOverPlayer(pPlayer);
		if (isPlayerSelectable(pPlayer)) {
			UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_PASS);
		} else {
			UtilClientCursor.setDefaultCursor(getClient().getUserInterface());
		}
		return true;
	}

	protected boolean mouseOverField(FieldCoordinate pCoordinate) {
		super.mouseOverField(pCoordinate);
		UtilClientCursor.setDefaultCursor(getClient().getUserInterface());
		return true;
	}

	private boolean isPlayerSelectable(Player<?> pPlayer) {
		boolean selectable = false;
		if (pPlayer != null) {
			Game game = getClient().getGame();
			PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
			selectable = ((playerState != null) && playerState.isActive() && game.getTeamHome().hasPlayer(pPlayer));
		}
		return selectable;
	}

	@Override
	public void endTurn() {
		SideBarComponent sideBarHome = getClient().getUserInterface().getSideBarHome();
		if (sideBarHome.getOpenBox() == BoxType.RESERVES) {
			sideBarHome.closeBox();
		}
		getClient().getCommunication().sendEndTurn(getClient().getGame().getTurnMode());
		getClient().getClientData().setEndTurnButtonHidden(true);
		sideBarHome.refresh();
	}

}
