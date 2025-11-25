package com.fumbbl.ffb.client.state.common;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.state.AbstractClientStateMove;
import com.fumbbl.ffb.client.state.logic.MoveLogicModule;

@RulesCollection(RulesCollection.Rules.COMMON)
public class ClientStateMove extends AbstractClientStateMove<MoveLogicModule> {

	public ClientStateMove(FantasyFootballClientAwt pClient) {
		super(pClient, new MoveLogicModule(pClient));
	}

}