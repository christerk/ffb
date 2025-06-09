package com.fumbbl.ffb.server.handler;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.Constant;
import com.fumbbl.ffb.model.sketch.Sketch;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ClientCommandJoinReplay;
import com.fumbbl.ffb.net.commands.ServerCommandAddSketches;
import com.fumbbl.ffb.net.commands.ServerCommandJoin;
import com.fumbbl.ffb.net.commands.ServerCommandReplayControl;
import com.fumbbl.ffb.net.commands.ServerCommandReplayStatus;
import com.fumbbl.ffb.net.commands.ServerCommandSetPreventSketching;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.ReplayCache;
import com.fumbbl.ffb.server.ReplayState;
import com.fumbbl.ffb.server.ServerSketchManager;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.net.ReplaySessionManager;
import com.fumbbl.ffb.server.net.ServerCommunication;
import com.fumbbl.ffb.util.ArrayTool;
import org.eclipse.jetty.websocket.api.Session;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
		ServerCommunication communication = getServer().getCommunication();
		ServerSketchManager sketchManager = getServer().getSketchManager();

		synchronized (sessionManager) {
			ReplayCache replayCache = getServer().getReplayCache();
			String plainReplayName = clientCommandJoinReplay.getReplayName();
			String sanitizedReplayName = plainReplayName.substring(0, Math.min(Constant.REPLAY_NAME_MAX_LENGTH, plainReplayName.length()));

			String replayName = plainReplayName + "_" + clientCommandJoinReplay.getGameId();

			Session session = receivedCommand.getSession();
			sessionManager.addSession(session, replayName, clientCommandJoinReplay.getCoach());

			String coach = sessionManager.coach(session);

			Session[] sessions = sessionManager.sessionsForReplay(replayName);

			if (ArrayTool.isProvided(sessions)) {
				List<String> coaches = Arrays.stream(sessions).map(sessionManager::coach).collect(Collectors.toList());

				Arrays.stream(sessions).forEach(storedSession -> communication
					.send(storedSession, new ServerCommandJoin(coach, ClientMode.REPLAY, new String[0], coaches, sanitizedReplayName), true));
			}

			ReplayState replayState = replayCache.replayState(replayName);
			if (replayState == null) {
				replayState = new ReplayState(replayName);
				replayCache.add(replayState);
				communication.sendToReplaySession(session, new ServerCommandReplayControl(coach));
			} else {
				ServerCommandReplayStatus command = new ServerCommandReplayStatus(replayState.getCommandNr(), replayState.getSpeed(), replayState.isRunning(), replayState.isForward(), true);
				communication.sendToReplaySession(session, command);
				communication.sendToReplaySession(session, new ServerCommandReplayControl(sessionManager.controllingCoach(session)));
				sessionManager.otherSessions(session).forEach(otherSession -> {
					String otherCoach = sessionManager.coach(otherSession);
					List<Sketch> sketches = sketchManager.getSketches(otherSession);
					communication.sendToReplaySession(session, new ServerCommandAddSketches(otherCoach, sketches));
					if (sessionManager.isPreventedFromSketching(otherSession)) {
						communication.sendToReplaySession(session, new ServerCommandSetPreventSketching(otherCoach, true));
					}
				});
			}
		}

		return true;
	}

}
