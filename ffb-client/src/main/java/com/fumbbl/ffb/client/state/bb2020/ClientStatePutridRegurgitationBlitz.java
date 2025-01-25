package com.fumbbl.ffb.client.state.bb2020;

import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.state.AbstractClientStateBlitz;
import com.fumbbl.ffb.client.state.logic.bb2020.PutridRegurgitationBlitzLogicModule;


public class ClientStatePutridRegurgitationBlitz extends AbstractClientStateBlitz<PutridRegurgitationBlitzLogicModule> {
	public ClientStatePutridRegurgitationBlitz(FantasyFootballClientAwt pClient) {
		super(pClient, new PutridRegurgitationBlitzLogicModule(pClient));
	}
}
