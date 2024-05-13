package com.fumbbl.ffb.client.state.logic;

import com.fumbbl.ffb.client.FantasyFootballClient;

import java.util.Set;

public abstract class LogicModule {
	protected final FantasyFootballClient client;

	public LogicModule(FantasyFootballClient client) {
		this.client = client;
	}

	public abstract Set<ClientAction> availableActions();
	public void perform(ClientAction action) {
		if (availableActions().contains(action)) {
			performAvailableAction(action);
		} else {
			client.logError("Unsupported action " + action.name() + " in logic module " + this.getClass().getCanonicalName());
		}
	}

	protected abstract void performAvailableAction(ClientAction action);
}
