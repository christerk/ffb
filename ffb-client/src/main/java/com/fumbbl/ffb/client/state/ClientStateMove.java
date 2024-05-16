package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.state.logic.MoveLogicModule;

public class ClientStateMove extends AbstractClientStateMove<MoveLogicModule> {

	protected ClientStateMove(FantasyFootballClientAwt pClient) {
		super(pClient, new MoveLogicModule(pClient));
	}

	public ClientStateId getId() {
		return ClientStateId.MOVE;
	}
}