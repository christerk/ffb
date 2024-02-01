package com.fumbbl.ffb.client.handler;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.net.NetCommand;
import com.fumbbl.ffb.net.NetCommandId;

public class ClientCommandHandlerSocketClosed extends ClientCommandHandler {

	protected ClientCommandHandlerSocketClosed(FantasyFootballClient pClient) {
		super(pClient);
	}

	public NetCommandId getId() {
		return NetCommandId.INTERNAL_SERVER_SOCKET_CLOSED;
	}

	public boolean handleNetCommand(NetCommand pNetCommand, ClientCommandHandlerMode pMode) {

		// InternalCommandSocketClosed socketClosedCommand =
		// (InternalCommandSocketClosed) pNetCommand;

		UserInterface userInterface = getClient().getUserInterface();
		userInterface.getStatusReport().reportSocketClosed();
		System.out.println("Connection closed by server.");

		return true;

	}

}
