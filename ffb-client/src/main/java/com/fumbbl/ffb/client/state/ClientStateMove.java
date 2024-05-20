package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.state.logic.MoveLogicModule;

public final class ClientStateMove extends AbstractClientStateMove<MoveLogicModule> {

	public ClientStateMove(FantasyFootballClientAwt pClient) {
		super(pClient, new MoveLogicModule(pClient));
	}

	public ClientStateId getId() {
		return ClientStateId.MOVE;
	}
}