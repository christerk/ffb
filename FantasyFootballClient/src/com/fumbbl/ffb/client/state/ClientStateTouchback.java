package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.util.UtilClientCursor;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;

/**
 *
 * @author Kalimar
 */
public class ClientStateTouchback extends ClientState {

	private boolean fTouchbackToAnyField;

	protected ClientStateTouchback(FantasyFootballClient pClient) {
		super(pClient);
	}

	public ClientStateId getId() {
		return ClientStateId.TOUCHBACK;
	}

	public void enterState() {
		super.enterState();
		setSelectable(true);
		// check if there are players on the field to give the ball to
		Game game = getClient().getGame();
		fTouchbackToAnyField = true;
		for (Player<?> player : game.getTeamHome().getPlayers()) {
			if (isPlayerSelectable(player)) {
				fTouchbackToAnyField = false;
				break;
			}
		}
	}

	@Override
	protected boolean mouseOverPlayer(Player<?> pPlayer) {
		super.mouseOverPlayer(pPlayer);
		if (isClickable() && (fTouchbackToAnyField || isPlayerSelectable(pPlayer))) {
			UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_PASS);
		} else {
			UtilClientCursor.setDefaultCursor(getClient().getUserInterface());
		}
		return true;
	}

	@Override
	protected boolean mouseOverField(FieldCoordinate pCoordinate) {
		super.mouseOverField(pCoordinate);
		if (isClickable() && fTouchbackToAnyField) {
			UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_PASS);
		} else {
			UtilClientCursor.setDefaultCursor(getClient().getUserInterface());
		}
		return true;
	}

	@Override
	protected void clickOnPlayer(Player<?> pPlayer) {
		Game game = getClient().getGame();
		if (isClickable() && (fTouchbackToAnyField || isPlayerSelectable(pPlayer))) {
			FieldCoordinate touchBackCoordinate = game.getFieldModel().getPlayerCoordinate(pPlayer);
			getClient().getCommunication().sendTouchback(touchBackCoordinate);
		}
	}

	@Override
	protected void clickOnField(FieldCoordinate pCoordinate) {
		if (isClickable() && fTouchbackToAnyField) {
			getClient().getCommunication().sendTouchback(pCoordinate);
		}
	}

	private boolean isPlayerSelectable(Player<?> pPlayer) {
		boolean selectable = false;
		if (pPlayer != null) {
			Game game = getClient().getGame();
			PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
			selectable = ((playerState != null) && playerState.hasTacklezones() && game.getTeamHome().hasPlayer(pPlayer)
					&& !pPlayer.hasSkillProperty(NamedProperties.preventHoldBall));
		}
		return selectable;
	}

}
