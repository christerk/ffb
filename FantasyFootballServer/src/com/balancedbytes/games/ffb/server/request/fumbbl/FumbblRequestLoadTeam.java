package com.balancedbytes.games.ffb.server.request.fumbbl;

import org.eclipse.jetty.websocket.api.Session;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.GameStatus;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Roster;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.net.ServerStatus;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerLogLevel;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.net.SessionManager;
import com.balancedbytes.games.ffb.server.net.commands.InternalServerCommandFumbblTeamLoaded;
import com.balancedbytes.games.ffb.server.request.ServerRequest;
import com.balancedbytes.games.ffb.server.request.ServerRequestProcessor;
import com.balancedbytes.games.ffb.util.StringTool;

/**
 * 
 * @author Kalimar
 */
public class FumbblRequestLoadTeam extends ServerRequest {

	private String fCoach;
	private String fTeamId;
	private boolean fHomeTeam;
	private GameState fGameState;

	private transient Session fSession;

	public FumbblRequestLoadTeam(GameState pGameState, String pCoach, String pTeamId, boolean pHomeTeam,
			Session pSession) {
		fGameState = pGameState;
		fCoach = pCoach;
		fTeamId = pTeamId;
		fHomeTeam = pHomeTeam;
		fSession = pSession;
	}

	public GameState getGameState() {
		return fGameState;
	}

	public String getCoach() {
		return fCoach;
	}

	public String getTeamId() {
		return fTeamId;
	}

	public boolean isHomeTeam() {
		return fHomeTeam;
	}

	public Session getSession() {
		return fSession;
	}

	@Override
	public void process(ServerRequestProcessor pRequestProcessor) {
		FantasyFootballServer server = pRequestProcessor.getServer();
		Game game = getGameState().getGame();
		Team team = null;
		try {
			team = UtilFumbblRequest.loadFumbblTeam(game, server, getTeamId());
		} catch (FantasyFootballException pFantasyFootballException) {
			handleInvalidTeam(pRequestProcessor, getTeamId(), pFantasyFootballException);
			return;
		}
		if ((team == null) || !StringTool.isProvided(team.getName())) {
			handleInvalidTeam(pRequestProcessor, getTeamId(), null);
			return;
		}
		Roster roster = null;
		try {
			roster = UtilFumbblRequest.loadFumbblRosterForTeam(game, server, getTeamId());
		} catch (FantasyFootballException pFantasyFootballException) {
			handleInvalidRoster(pRequestProcessor, getTeamId(), pFantasyFootballException);
			return;
		}
		if ((roster == null) || !StringTool.isProvided(roster.getName())) {
			handleInvalidRoster(pRequestProcessor, getTeamId(), null);
			return;
		}
		team.updateRoster(roster);
		server.getGameCache().addTeamToGame(getGameState(), team, isHomeTeam());
		if (GameStatus.SCHEDULED == getGameState().getStatus()) {
			if (StringTool.isProvided(game.getTeamHome().getId()) && StringTool.isProvided(game.getTeamAway().getId())) {
				// log game scheduled -->
				if (server.getDebugLog().isLogging(IServerLogLevel.WARN)) {
					StringBuilder logEntry = new StringBuilder();
					logEntry.append("GAME SCHEDULED ").append(StringTool.print(game.getTeamHome().getName())).append(" vs. ")
							.append(StringTool.print(game.getTeamAway().getName()));
					server.getDebugLog().log(IServerLogLevel.WARN, getGameState().getId(), logEntry.toString());
				}
				// <-- log game scheduled
			}
		} else {
			InternalServerCommandFumbblTeamLoaded loadedCommand = new InternalServerCommandFumbblTeamLoaded(
					getGameState().getId(), getCoach(), isHomeTeam());
			server.getCommunication().handleCommand(new ReceivedCommand(loadedCommand, getSession()));
		}
	}

	// this might be overkill, we'll see how it does in practice
	private void handleInvalidTeam(ServerRequestProcessor pRequestProcessor, String pTeamId, Throwable pThrowable) {
		FantasyFootballServer server = pRequestProcessor.getServer();
		server.getDebugLog().log(IServerLogLevel.ERROR, StringTool.bind("Error loading Team $1.", pTeamId));
		server.getDebugLog().log(pThrowable);
		server.getCommunication().sendStatus(getGameState(), ServerStatus.FUMBBL_ERROR,
				StringTool.bind("Unable to load Team with id $1.", pTeamId));
		closeGame(pRequestProcessor, getGameState());
	}

	// this might be overkill, we'll see how it does in practice
	private void handleInvalidRoster(ServerRequestProcessor pRequestProcessor, String pTeamId, Throwable pThrowable) {
		FantasyFootballServer server = pRequestProcessor.getServer();
		server.getDebugLog().log(IServerLogLevel.ERROR, StringTool.bind("Error loading Roster for Team $1.", pTeamId));
		server.getDebugLog().log(pThrowable);
		server.getCommunication().sendStatus(getGameState(), ServerStatus.FUMBBL_ERROR,
				StringTool.bind("Unable to load Roster with for Team $1.", pTeamId));
		closeGame(pRequestProcessor, getGameState());
	}

	private void closeGame(ServerRequestProcessor pRequestProcessor, GameState pGameState) {
		if (pGameState != null) {
			FantasyFootballServer server = pGameState.getServer();
			SessionManager sessionManager = server.getSessionManager();
			Session[] sessions = sessionManager.getSessionsForGameId(pGameState.getId());
			for (int i = 0; i < sessions.length; i++) {
				server.getCommunication().close(sessions[i]);
			}
		}
	}

}
