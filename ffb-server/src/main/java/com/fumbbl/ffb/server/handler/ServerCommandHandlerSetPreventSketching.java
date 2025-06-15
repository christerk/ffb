package com.fumbbl.ffb.server.handler;

import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ClientCommandSetPreventSketching;
import com.fumbbl.ffb.net.commands.ServerCommandSetPreventSketching;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.ReplayCache;
import com.fumbbl.ffb.server.ReplayState;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.net.ReplaySessionManager;
import com.fumbbl.ffb.server.net.ServerCommunication;
import org.eclipse.jetty.websocket.api.Session;

import java.util.HashSet;
import java.util.Set;

public class ServerCommandHandlerSetPreventSketching extends ServerCommandHandler {

	protected ServerCommandHandlerSetPreventSketching(FantasyFootballServer pServer) {
		super(pServer);
	}

	@Override
	public NetCommandId getId() {
		return NetCommandId.CLIENT_SET_PREVENT_SKETCHING;
	}

	@Override
	public boolean handleCommand(ReceivedCommand receivedCommand) {
		ReplaySessionManager sessionManager = getServer().getReplaySessionManager();
		Session session = receivedCommand.getSession();
		if (sessionManager.has(session) && sessionManager.hasControl(session)) {
			ReplayCache replayCache = getServer().getReplayCache();
			ClientCommandSetPreventSketching command = (ClientCommandSetPreventSketching) receivedCommand.getCommand();

			ReplayState replayState = replayCache.replayState(sessionManager.replayForSession(session));

			synchronized (replayState) {
				if (command.isPreventSketching()) {
					replayState.preventCoachFromSketching(command.getCoach());
				} else {
					replayState.allowCoachToSketch(command.getCoach());
				}
			}

			ServerCommunication communication = getServer().getCommunication();
			Set<Session> sessions = new HashSet<>();
			sessions.add(session);
			sessions.addAll(sessionManager.otherSessions(session));
			sessions.forEach(replaySession -> communication.sendToReplaySession(replaySession,
				new ServerCommandSetPreventSketching(command.getCoach(), command.isPreventSketching())));
		}

		return true;
	}
}
