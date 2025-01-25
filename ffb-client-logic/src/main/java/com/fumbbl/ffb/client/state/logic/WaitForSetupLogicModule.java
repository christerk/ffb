package com.fumbbl.ffb.client.state.logic;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.state.logic.interaction.ActionContext;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Player;

import java.util.Collections;
import java.util.Set;

/**
 * 
 * @author Kalimar
 */
public class WaitForSetupLogicModule extends LogicModule {

	public WaitForSetupLogicModule(FantasyFootballClient pClient) {
		super(pClient);
	}

	public ClientStateId getId() {
		return ClientStateId.WAIT_FOR_SETUP;
	}

	@Override
	public Set<ClientAction> availableActions() {
		return Collections.emptySet();
	}

	@Override
	protected void performAvailableAction(Player<?> player, ClientAction action) {

	}

	@Override
	protected ActionContext actionContext(ActingPlayer actingPlayer) {
		throw new UnsupportedOperationException("actionContext for acting player is not supported in waiting context");
	}

}
