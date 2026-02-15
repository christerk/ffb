package com.fumbbl.ffb.client.state.logic;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;

public abstract class AbstractBlockLogicModule extends LogicModule {

	public AbstractBlockLogicModule(FantasyFootballClient client) {
		super(client);
	}

	@Override
	public ClientStateId getId() {
		return ClientStateId.BLOCK;
	}


	@Override
	public void endTurn() {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		perform(actingPlayer.getPlayer(), ClientAction.END_MOVE);
		client.getCommunication().sendEndTurn(game.getTurnMode());
	}

	public boolean isSufferingBloodLust(ActingPlayer actingPlayer) {
		return actingPlayer.isSufferingBloodLust();
	}

}
