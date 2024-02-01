package com.fumbbl.ffb.client.handler;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.net.NetCommand;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ServerCommandLeave;

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
