package com.fumbbl.ffb.client.state.logic;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.client.FantasyFootballClient;

public class DwarfenWisdomLogicModule extends SetupLogicModule {
	public DwarfenWisdomLogicModule(FantasyFootballClient pClient) {
		super(pClient);
	}

	public ClientStateId getId() {
		return ClientStateId.DWARFEN_WISDOM;
	}
}