package com.fumbbl.ffb.client.util;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;

/**
 * 
 * @author Kalimar
 */
public class UtilClientThrowTeamMate {

	public static void updateThrownPlayer(FantasyFootballClient pClient) {
		if (pClient != null) {
			Game game = pClient.getGame();
			ActingPlayer actingPlayer = game.getActingPlayer();
			UserInterface userInterface = pClient.getUserInterface();
			if ((game.getDefender() != null)
					&& (game.getFieldModel().getPlayerState(game.getDefender()).getBase() == PlayerState.PICKED_UP)) {
				FieldCoordinate thrownPlayerCoordinate = game.getFieldModel().getPlayerCoordinate(game.getDefender());
				boolean thrownPlayerWithBall = ((thrownPlayerCoordinate != null)
						&& thrownPlayerCoordinate.equals(game.getFieldModel().getBallCoordinate()));
				FieldCoordinate throwerCoordinate = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
				userInterface.getFieldComponent().getLayerOverPlayers().drawThrownPlayer(game, game.getDefender(),
						throwerCoordinate, thrownPlayerWithBall);
			} else {
				userInterface.getFieldComponent().getLayerOverPlayers().removeThrownPlayer();
			}
		}
	}

}
