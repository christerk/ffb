package com.balancedbytes.games.ffb.client.state;

import com.balancedbytes.games.ffb.ClientStateId;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.UserInterface;
import com.balancedbytes.games.ffb.client.util.UtilClientCursor;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;

/**
 * 
 * @author Kalimar
 */
public class ClientStateSwoop extends ClientStateMove {

	protected ClientStateSwoop(FantasyFootballClient pClient) {
		super(pClient);
	}

	public ClientStateId getId() {
		return ClientStateId.SWOOP;
	}

	private void sendSwoop(Game game, ActingPlayer actingPlayer, FieldCoordinate destination) {
		FieldCoordinate source = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
		if (source.isAdjacent(destination)) {
			// Check if the destination is in one of the 4 cardinal directions from the
			// player
			if (source.getY() == destination.getY() || source.getX() == destination.getX()) {
				getClient().getCommunication().sendSwoop(actingPlayer.getPlayerId(), destination);
			}
		}
	}

	protected void clickOnField(FieldCoordinate pCoordinate) {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();

		if (actingPlayer.getPlayerAction() == PlayerAction.SWOOP) {
			sendSwoop(game, actingPlayer, pCoordinate);
		}
	}

	protected void clickOnPlayer(Player pPlayer) {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();

		if (actingPlayer.getPlayerAction() == PlayerAction.SWOOP) {
			FieldCoordinate coordinate = game.getFieldModel().getPlayerCoordinate(pPlayer);
			sendSwoop(game, actingPlayer, coordinate);
		}
	}

	protected boolean mouseOverPlayer(Player pPlayer) {
		Game game = getClient().getGame();
		UserInterface userInterface = getClient().getUserInterface();
		if ((game.getDefender() == null) && (game.getPassCoordinate() == null)) {
			UtilClientCursor.setDefaultCursor(userInterface);
		}
		// if ((PlayerAction.THROW_TEAM_MATE == actingPlayer.getPlayerAction()) &&
		// (game.getPassCoordinate() == null)) {
		getClient().getClientData().setSelectedPlayer(pPlayer);
		userInterface.refreshSideBars();
		return true;
	}

	@Override
	public void leaveState() {
		// clear marked players
		UserInterface userInterface = getClient().getUserInterface();
		userInterface.getFieldComponent().getLayerRangeRuler().clearMarkedCoordinates();
		userInterface.getFieldComponent().refresh();
	}
}
