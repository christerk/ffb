package com.balancedbytes.games.ffb.client.handler;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.UserInterface;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.net.commands.ServerCommandLeave;

/**
 * 
 * @author Kalimar
 */
public class ClientCommandHandlerLeave extends ClientCommandHandler {

	protected ClientCommandHandlerLeave(FantasyFootballClient pClient) {
		super(pClient);
	}

	public NetCommandId getId() {
		return NetCommandId.SERVER_LEAVE;
	}

	public boolean handleNetCommand(NetCommand pNetCommand, ClientCommandHandlerMode pMode) {

		if (pMode == ClientCommandHandlerMode.QUEUING) {
			return true;
		}

		ServerCommandLeave leaveCommand = (ServerCommandLeave) pNetCommand;

		if (ClientMode.PLAYER == leaveCommand.getClientMode()) {
			getClient().getClientData().setTurnTimerStopped(true);
		}

		getClient().getClientData().setSpectators(leaveCommand.getSpectators());

		if (pMode != ClientCommandHandlerMode.REPLAYING) {
			UserInterface userInterface = getClient().getUserInterface();
			userInterface.getLog().markCommandBegin(leaveCommand.getCommandNr());
			userInterface.getStatusReport().reportLeave(leaveCommand);
			userInterface.getLog().markCommandEnd(leaveCommand.getCommandNr());
			refreshSideBars();
		}

		return true;

	}

}
