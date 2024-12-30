package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.state.logic.BlitzLogicModule;

public class ClientStateBlitz extends AbstractClientStateBlitz<BlitzLogicModule> {
	protected ClientStateBlitz(FantasyFootballClientAwt client) {
		super(client, new BlitzLogicModule(client));
	}

}
