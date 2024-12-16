package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.state.logic.SolidDefenceLogicModule;

public class ClientStateSolidDefence extends AbstractClientStateSetup<SolidDefenceLogicModule> {
	protected ClientStateSolidDefence(FantasyFootballClientAwt pClient) {
		super(pClient, new SolidDefenceLogicModule(pClient));
	}

	@Override
	public ClientStateId getId() {
		return ClientStateId.SOLID_DEFENCE;
	}

	@Override
	public boolean actionKeyPressed(ActionKey pActionKey) {
		return false;
	}

}
