package com.fumbbl.ffb.client.state.logic;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.model.Player;

import java.util.Set;

public abstract class LogicModule {
	protected final FantasyFootballClient client;

	public LogicModule(FantasyFootballClient client) {
		this.client = client;
	}

	public void perform(Player<?> player, ClientAction action) {
		if (availableActions().contains(action)) {
			performAvailableAction(player, action);
		} else {
			client.logError("Unsupported action " + action.name() + " in logic module " + this.getClass().getCanonicalName());
		}
	}

	public abstract Set<ClientAction> availableActions();

	protected abstract void performAvailableAction(Player<?> player, ClientAction action);
}
