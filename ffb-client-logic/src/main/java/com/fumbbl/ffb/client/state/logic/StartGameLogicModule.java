package com.fumbbl.ffb.client.state.logic;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.model.Player;

import java.util.Collections;
import java.util.Set;

/**
 * 
 * @author Kalimar
 */
public class StartGameLogicModule extends LogicModule {

	public StartGameLogicModule(FantasyFootballClient pClient) {
		super(pClient);
	}

	public ClientStateId getId() {
		return ClientStateId.START_GAME;
	}

	@Override
	public Set<ClientAction> availableActions() {
		return Collections.emptySet();
	}

	@Override
	protected void performAvailableAction(Player<?> player, ClientAction action) {

	}

}
