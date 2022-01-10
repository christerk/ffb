package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.util.UtilClientActionKeys;
import com.fumbbl.ffb.client.util.UtilClientCursor;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.util.UtilPlayer;

/**
 * @author Kristian Widén
 * @author Kalimar
 */
public class ClientStateGaze extends ClientStateMove {

	protected ClientStateGaze(FantasyFootballClient pClient) {
		super(pClient);
	}

	public ClientStateId getId() {
		return ClientStateId.GAZE;
	}

	protected void clickOnPlayer(Player<?> pPlayer) {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (pPlayer == actingPlayer.getPlayer()) {
			super.clickOnPlayer(pPlayer);
		} else {
			if (canBeGazed(pPlayer)) {
				getClient().getCommunication().sendGaze(actingPlayer.getPlayerId(), pPlayer);
			}
		}
	}

	public boolean actionKeyPressed(ActionKey pActionKey) {
		boolean actionHandled;
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		FieldCoordinate playerPosition = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
		FieldCoordinate victimPosition = UtilClientActionKeys.findMoveCoordinate(playerPosition, pActionKey);
		Player<?> victim = game.getFieldModel().getPlayer(victimPosition);
		if (victim != null) {
			actionHandled = canBeGazed(victim);
			if (actionHandled) {
				getClient().getCommunication().sendGaze(actingPlayer.getPlayerId(), victim);
			}
		} else {
			actionHandled = super.actionKeyPressed(pActionKey);
		}
		return actionHandled;
	}

	protected boolean mouseOverPlayer(Player<?> pPlayer) {
		super.mouseOverPlayer(pPlayer);
		if (canBeGazed(pPlayer)) {
			UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_GAZE);
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

	// Added a check to see if the player had tacklezones so no prone players could
	// be gazed or already gazed players.
	private boolean canBeGazed(Player<?> pVictim) {
		boolean result = false;
		if (pVictim != null) {
			Game game = getClient().getGame();
			ActingPlayer actingPlayer = game.getActingPlayer();
			FieldCoordinate actorCoordinate = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
			FieldCoordinate victimCoordinate = game.getFieldModel().getPlayerCoordinate(pVictim);
			Team actorTeam = game.getTeamHome().hasPlayer(actingPlayer.getPlayer()) ? game.getTeamHome() : game.getTeamAway();
			Team victimTeam = game.getTeamHome().hasPlayer(pVictim) ? game.getTeamHome() : game.getTeamAway();
			result = (UtilPlayer.canGaze(game, actingPlayer.getPlayer()) && (victimCoordinate != null)
					&& victimCoordinate.isAdjacent(actorCoordinate) && (actorTeam != victimTeam)
					&& (game.getFieldModel().getPlayerState(pVictim).hasTacklezones()));
		}
		return result;
	}

}
