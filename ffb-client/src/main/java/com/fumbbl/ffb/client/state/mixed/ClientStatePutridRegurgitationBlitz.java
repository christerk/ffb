package com.fumbbl.ffb.client.state.mixed;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.state.AbstractClientStateBlitz;
import com.fumbbl.ffb.client.state.logic.bb2020.PutridRegurgitationBlitzLogicModule;

@RulesCollection(RulesCollection.Rules.BB2020)
@RulesCollection(RulesCollection.Rules.BB2025)
public class ClientStatePutridRegurgitationBlitz extends AbstractClientStateBlitz<PutridRegurgitationBlitzLogicModule> {
	public ClientStatePutridRegurgitationBlitz(FantasyFootballClientAwt pClient) {
		super(pClient, new PutridRegurgitationBlitzLogicModule(pClient));
	}
}
