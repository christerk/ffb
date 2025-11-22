package com.fumbbl.ffb.client.state.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.state.AbstractClientStateBlitz;
import com.fumbbl.ffb.client.state.logic.bb2020.KickEmBlitzLogicModule;

@RulesCollection(RulesCollection.Rules.BB2020)
public class ClientStateKickEmBlitz extends AbstractClientStateBlitz<KickEmBlitzLogicModule> {
	public ClientStateKickEmBlitz(FantasyFootballClientAwt pClient) {
		super(pClient, new KickEmBlitzLogicModule(pClient));
	}
}
