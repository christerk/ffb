package com.balancedbytes.games.ffb.client.state;

import com.balancedbytes.games.ffb.ClientStateId;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.client.ActionKey;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.IIconProperty;
import com.balancedbytes.games.ffb.client.util.UtilClientActionKeys;
import com.balancedbytes.games.ffb.client.util.UtilClientCursor;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.util.UtilPlayer;

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
		boolean actionHandled = false;
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		FieldCoordinate playerPosition = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
		FieldCoordinate victimPosition = UtilClientActionKeys.findMoveCoordinate(getClient(), playerPosition, pActionKey);
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
