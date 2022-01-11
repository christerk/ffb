package com.fumbbl.ffb.client.state.bb2020;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.state.ClientStateMove;
import com.fumbbl.ffb.client.util.UtilClientCursor;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.util.UtilPlayer;

public class ClientStateGazeMove extends ClientStateMove {
	public ClientStateGazeMove(FantasyFootballClient pClient) {
		super(pClient);
	}

	@Override
	public ClientStateId getId() {
		return ClientStateId.GAZE_MOVE;
	}

	@Override
	protected boolean mouseOverPlayer(Player<?> player) {
		boolean result = super.mouseOverPlayer(player);

		Game game = getClient().getGame();

		if (UtilPlayer.isAdjacentGazeTarget(game, player)) {
			UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_GAZE);
		}

		return result;
	}

	@Override
	protected void clickOnPlayer(Player<?> player) {

		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();

		if (UtilPlayer.isAdjacentGazeTarget(game, player)) {
			getClient().getCommunication().sendGaze(actingPlayer.getPlayerId(), player);
		}

		super.clickOnPlayer(player);
	}
}
