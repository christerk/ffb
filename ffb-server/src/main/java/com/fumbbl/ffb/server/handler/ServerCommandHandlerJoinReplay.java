package com.fumbbl.ffb.server.handler;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ClientCommandJoinReplay;
import com.fumbbl.ffb.net.commands.ServerCommandJoin;
import com.fumbbl.ffb.net.commands.ServerCommandReplayControl;
import com.fumbbl.ffb.net.commands.ServerCommandReplayStatus;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.ReplayCache;
import com.fumbbl.ffb.server.ReplayState;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.net.ReplaySessionManager;
import com.fumbbl.ffb.util.ArrayTool;
import org.eclipse.jetty.websocket.api.Session;

import java.util.ArrayList;
import java.util.Arrays;

public class ServerCommandHandlerJoinReplay extends ServerCommandHandler {


	protected ServerCommandHandlerJoinReplay(FantasyFootballServer pServer) {
		super(pServer);
	}

	@Override
	public NetCommandId getId() {
		return NetCommandId.CLIENT_JOIN_REPLAY;
	}

	@Override
	public boolean handleCommand(ReceivedCommand receivedCommand) {
		ClientCommandJoinReplay clientCommandJoinReplay = (ClientCommandJoinReplay) receivedCommand.getCommand();
		ReplaySessionManager sessionManager = getServer().getReplaySessionManager();
		synchronized (sessionManager) {
			ReplayCache replayCache = getServer().getReplayCache();
			String replayName = clientCommandJoinReplay.getReplayName();

			sessionManager.addSession(receivedCommand.getSession(), replayName, clientCommandJoinReplay.getCoach());

			String coach = sessionManager.coach(receivedCommand.getSession());

			Session[] sessions = sessionManager.sessionsForReplay(replayName);

			if (ArrayTool.isProvided(sessions)) {
				String[] coaches = Arrays.stream(sessions).map(sessionManager::coach).toArray(String[]::new);

				Arrays.stream(sessions).forEach(session -> getServer().getCommunication()
					.send(session, new ServerCommandJoin(coach, ClientMode.REPLAY, coaches, new ArrayList<>()), true));
			}

			ReplayState replayState = replayCache.replayState(replayName);
			if (replayState == null) {
				replayState = new ReplayState(replayName);
				replayCache.add(replayState);
				getServer().getCommunication().send(receivedCommand.getSession(), new ServerCommandReplayControl(true), true);
			} else {
				ServerCommandReplayStatus command = new ServerCommandReplayStatus(replayState.getCommandNr(), replayState.getSpeed(), replayState.isRunning(), replayState.isForward(), true);
				getServer().getCommunication().send(receivedCommand.getSession(), command, true);
			}
		}

		return true;
	}

}
