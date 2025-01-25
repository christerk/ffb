package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.state.logic.SolidDefenceLogicModule;

public class ClientStateSolidDefence extends AbstractClientStateSetup<SolidDefenceLogicModule> {
	protected ClientStateSolidDefence(FantasyFootballClientAwt pClient) {
		super(pClient, new SolidDefenceLogicModule(pClient));
	}
}
