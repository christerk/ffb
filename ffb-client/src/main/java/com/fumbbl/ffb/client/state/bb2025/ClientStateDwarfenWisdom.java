package com.fumbbl.ffb.client.state.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.state.AbstractClientStateSetup;
import com.fumbbl.ffb.client.state.logic.DwarfenWisdomLogicModule;

@RulesCollection(RulesCollection.Rules.BB2025)
public class ClientStateDwarfenWisdom extends AbstractClientStateSetup<DwarfenWisdomLogicModule> {

	public ClientStateDwarfenWisdom(FantasyFootballClientAwt pClient) {
		super(pClient, new DwarfenWisdomLogicModule(pClient));
	}
}