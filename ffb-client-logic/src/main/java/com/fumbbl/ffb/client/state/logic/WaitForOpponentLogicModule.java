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
public class WaitForOpponentLogicModule extends LogicModule {

	public WaitForOpponentLogicModule(FantasyFootballClient pClient) {
		super(pClient);
	}

	public ClientStateId getId() {
		return ClientStateId.WAIT_FOR_OPPONENT;
	}

	public void illegalProcedure() {
		client.getCommunication().sendIllegalProcedure();
	}

	@Override
	public Set<ClientAction> availableActions() {
		return Collections.emptySet();
	}

	@Override
	protected void performAvailableAction(Player<?> player, ClientAction action) {

	}

}
