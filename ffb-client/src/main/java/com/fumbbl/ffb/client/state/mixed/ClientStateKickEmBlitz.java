package com.fumbbl.ffb.client.state.mixed;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.state.AbstractClientStateBlitz;
import com.fumbbl.ffb.client.state.logic.mixed.KickEmBlitzLogicModule;

@RulesCollection(RulesCollection.Rules.BB2020)
@RulesCollection(RulesCollection.Rules.BB2025)
public class ClientStateKickEmBlitz extends AbstractClientStateBlitz<KickEmBlitzLogicModule> {
	public ClientStateKickEmBlitz(FantasyFootballClientAwt pClient) {
		super(pClient, new KickEmBlitzLogicModule(pClient));
	}
}
