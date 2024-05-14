package com.fumbbl.ffb.client.state.logic;

import com.fumbbl.ffb.model.Player;

import java.util.Collections;
import java.util.Set;

public class NoLogicModule extends LogicModule {

	public NoLogicModule() {
		super(null);
	}

	@Override
	public Set<ClientAction> availableActions() {
		return Collections.emptySet();
	}

	@Override
	protected void performAvailableAction(Player<?> player, ClientAction action) {

	}
}
