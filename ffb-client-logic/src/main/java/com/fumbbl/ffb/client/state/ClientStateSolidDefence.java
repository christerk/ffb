package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.net.NetCommand;

public class ClientStateSolidDefence extends ClientStateSetup {
	protected ClientStateSolidDefence(FantasyFootballClient pClient) {
		super(pClient);
	}

	@Override
	public ClientStateId getId() {
		return ClientStateId.SOLID_DEFENCE;
	}

	@Override
	public boolean actionKeyPressed(ActionKey pActionKey) {
		return false;
	}

	@Override
	public void handleCommand(NetCommand pNetCommand) {
	}
}
