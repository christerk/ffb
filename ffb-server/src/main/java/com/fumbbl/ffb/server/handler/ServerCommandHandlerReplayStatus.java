package com.fumbbl.ffb.server.handler;

import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ClientCommandReplayStatus;
import com.fumbbl.ffb.net.commands.ServerCommandReplayStatus;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.ReplayCache;
import com.fumbbl.ffb.server.ReplayState;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.net.ReplaySessionManager;
import com.fumbbl.ffb.util.StringTool;
import org.eclipse.jetty.websocket.api.Session;

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
		ReplaySessionManager sessionManager = getServer().getReplaySessionManager();
		Session session = receivedCommand.getSession();
		String replayName = sessionManager.replayNameForSession(session);
		if (sessionManager.hasControl(session) && StringTool.isProvided(replayName)) {
			ReplayCache cache = getServer().getReplayCache();
			ReplayState state = cache.replayState(replayName);
			if (state != null && requiresPushToOtherClients(state, clientCommandReplayStatus)) {
				ServerCommandReplayStatus serverCommandReplayStatus = new ServerCommandReplayStatus(clientCommandReplayStatus.getCommandNr(), clientCommandReplayStatus.getSpeed(), clientCommandReplayStatus.isRunning(), clientCommandReplayStatus.isForward(), clientCommandReplayStatus.isSkip());
				sessionManager.otherSessions(session)
					.forEach(otherSession -> getServer().getCommunication().send(otherSession, serverCommandReplayStatus, true));
			}
		}
		return true;
	}

	private boolean requiresPushToOtherClients(ReplayState state, ClientCommandReplayStatus clientCommandReplayStatus) {
		return clientCommandReplayStatus.isSkip()
			|| clientCommandReplayStatus.isForward() != state.isForward()
			|| clientCommandReplayStatus.isRunning() != state.isRunning()
			|| clientCommandReplayStatus.getSpeed() != state.getSpeed();
	}

}
