package com.fumbbl.ffb.client.state.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.state.AbstractClientStateSetup;
import com.fumbbl.ffb.client.state.logic.SolidDefenceLogicModule;

@RulesCollection(RulesCollection.Rules.BB2020)
public class ClientStateSolidDefence extends AbstractClientStateSetup<SolidDefenceLogicModule> {
	public ClientStateSolidDefence(FantasyFootballClientAwt pClient) {
		super(pClient, new SolidDefenceLogicModule(pClient));
	}
}
