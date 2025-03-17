package com.fumbbl.ffb.server.handler;

import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ClientCommandReplayStatus;
import com.fumbbl.ffb.net.commands.ServerCommandReplayStatus;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.net.ReplaySessionManager;

public class ServerCommandHandlerReplayStatus extends ServerCommandHandler {


	protected ServerCommandHandlerReplayStatus(FantasyFootballServer pServer) {
		super(pServer);
	}

	@Override
	public NetCommandId getId() {
		return NetCommandId.CLIENT_REPLAY_STATUS;
	}

	@Override
	public boolean handleCommand(ReceivedCommand receivedCommand) {
		ClientCommandReplayStatus clientCommandReplayStatus = (ClientCommandReplayStatus) receivedCommand.getCommand();
		ServerCommandReplayStatus serverCommandReplayStatus = new ServerCommandReplayStatus(clientCommandReplayStatus.getCommandNr(), clientCommandReplayStatus.getSpeed(), clientCommandReplayStatus.isRunning(), clientCommandReplayStatus.isForward());
		ReplaySessionManager sessionManager = getServer().getReplaySessionManager();
		sessionManager.otherSessions(receivedCommand.getSession())
			.forEach(session -> getServer().getCommunication().send(session, serverCommandReplayStatus, true));

		return true;
	}

}
