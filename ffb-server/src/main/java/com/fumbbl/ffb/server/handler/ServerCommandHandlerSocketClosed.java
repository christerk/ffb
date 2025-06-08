package com.fumbbl.ffb.server.handler;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.GameStatus;
import com.fumbbl.ffb.model.sketch.Sketch;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ServerCommandRemoveSketches;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameCache;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.ReplayCache;
import com.fumbbl.ffb.server.ReplayState;
import com.fumbbl.ffb.server.ServerSketchManager;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.net.ReplaySessionManager;
import com.fumbbl.ffb.server.net.ServerCommunication;
import com.fumbbl.ffb.server.net.SessionManager;
import com.fumbbl.ffb.server.util.UtilServerTimer;
import com.fumbbl.ffb.util.ArrayTool;
import org.eclipse.jetty.websocket.api.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author Kalimar
 */
public class ServerCommandHandlerSocketClosed extends ServerCommandHandler {

	protected ServerCommandHandlerSocketClosed(FantasyFootballServer pServer) {
		super(pServer);
	}

	public NetCommandId getId() {
		return NetCommandId.INTERNAL_SERVER_SOCKET_CLOSED;
	}

	public boolean handleCommand(ReceivedCommand pReceivedCommand) {
		getServer().getSketchManager().remove(pReceivedCommand.getSession());
		if (getServer().getReplaySessionManager().has(pReceivedCommand.getSession())) {
			return closeReplaySession(pReceivedCommand);
		} else {
			return closeGameSession(pReceivedCommand);
		}
	}

	public boolean closeGameSession(ReceivedCommand pReceivedCommand) {

		SessionManager sessionManager = getServer().getSessionManager();
		String coach = sessionManager.getCoachForSession(pReceivedCommand.getSession());
		ClientMode mode = sessionManager.getModeForSession(pReceivedCommand.getSession());
		long gameId = sessionManager.getGameIdForSession(pReceivedCommand.getSession());
		boolean isAdmin = sessionManager.isSessionAdmin(pReceivedCommand.getSession());
		sessionManager.removeSession(pReceivedCommand.getSession());

		Session[] sessions = sessionManager.getSessionsForGameId(gameId);

		GameCache gameCache = getServer().getGameCache();
		GameState gameState = gameCache.getGameStateById(gameId);
		if (gameState != null) {

			List<String> spectators = new ArrayList<>();
			for (Session session : sessions) {
				if (sessionManager.getModeForSession(session) == ClientMode.SPECTATOR) {
					if (!sessionManager.isSessionAdmin(session)) {
						spectators.add(sessionManager.getCoachForSession(session));
					}
				}
			}

			// stop timer whenever a player drops out
			if (ClientMode.PLAYER == mode) {
				long currentTimeMillis = System.currentTimeMillis();
				UtilServerTimer.syncTime(gameState, currentTimeMillis);
				UtilServerTimer.stopTurnTimer(gameState, currentTimeMillis);
			}

			Session homeSession = sessionManager.getSessionOfHomeCoach(gameId);
			Session awaySession = sessionManager.getSessionOfAwayCoach(gameId);

			if ((GameStatus.ACTIVE == gameState.getStatus()) && ((homeSession == null) || (awaySession == null))) {
				gameState.setStatus(GameStatus.PAUSED);
				gameCache.queueDbUpdate(gameState, true);
				gameState.fetchChanges(); // remove all changes from queue
			}

			if (ArrayTool.isProvided(sessions)) {
				boolean hideLeaveCommand = mode == ClientMode.SPECTATOR && isAdmin;
				if (!hideLeaveCommand) {
					getServer().getCommunication().sendLeave(sessions, coach, mode, spectators);
				}
			} else {
				getServer().getGameCache().closeGame(gameState.getId());
			}

		}

		return true;

	}

	public boolean closeReplaySession(ReceivedCommand pReceivedCommand) {

		ReplaySessionManager sessionManager = getServer().getReplaySessionManager();
		ReplayCache replayCache = getServer().getReplayCache();
		ServerSketchManager sketchManager = getServer().getSketchManager();

		Session closingSession = pReceivedCommand.getSession();
		String replayName = sessionManager.replayNameForSession(closingSession);
		Set<Session> sessions = sessionManager.otherSessions(closingSession);
		ReplayState state = replayCache.replayState(replayName);

		if (state != null) {
			if (sessions.isEmpty()) {
				getServer().getReplayCache().closeReplay(replayName);
			} else {
				ServerCommunication communication = getServer().getCommunication();
				List<String> sketchIds = sketchManager.getSketches(closingSession).stream()
						.map(Sketch::getId).collect(Collectors.toList());
				List<String> joinedCoaches = new ArrayList<>();
				for (Session session : sessions) {
					joinedCoaches.add(sessionManager.coach(session));
					communication.sendToReplaySession(session, new ServerCommandRemoveSketches(sessionManager.coach(closingSession), sketchIds));
				}

				String coach = sessionManager.coach(closingSession);
				Session[] sessionsArray = sessions.toArray(new Session[0]);
				communication.sendReplayLeave(sessionsArray, coach, ClientMode.REPLAY, joinedCoaches);

				if (sessionManager.transferControl(closingSession, sessionManager.coach(sessionsArray[0]))) {
					ReplayState replayState = getServer().getReplayCache().replayState(replayName);
					communication.sendReplayControlChange(replayState, sessionManager.coach(sessionsArray[0]));
				}
			}
			sketchManager.remove(closingSession);
			sessionManager.removeSession(closingSession);
		}

		return true;

	}

}
