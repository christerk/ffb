package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.model.Game;

public class DialogPileDriver extends DialogThreeWayChoice {

	private final String playerId;

	public DialogPileDriver(FantasyFootballClient pClient, String playerId) {
		super(pClient, "Use Pile Driver", "Do you want to foul " + getName(pClient.getGame(), playerId) + " using Pile Driver?");
		this.playerId = playerId;
	}

	private static String getName(Game game, String playerId) {
		return game.getPlayerById(playerId).getName();
	}

	public DialogId getId() {
		return DialogId.PILE_DRIVER;
	}

	public String getPlayerId() {
		return playerId;
	}

}
