package com.fumbbl.ffb.client.state.logic;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.client.FantasyFootballClient;

public class SolidDefenceLogicModule extends SetupLogicModule {
	public SolidDefenceLogicModule(FantasyFootballClient pClient) {
		super(pClient);
	}

	public ClientStateId getId() {
		return ClientStateId.SOLID_DEFENCE;
	}
}
