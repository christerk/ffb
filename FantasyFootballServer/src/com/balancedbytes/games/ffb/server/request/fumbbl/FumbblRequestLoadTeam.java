package com.balancedbytes.games.ffb.server.request.fumbbl;

import com.balancedbytes.games.ffb.server.util.UtilServerGame;
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

import static com.balancedbytes.games.ffb.server.util.UtilServerGame.handleInvalidTeam;

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
			team = UtilFumbblRequest.loadFumbblTeam(server, getTeamId());
		} catch (FantasyFootballException pFantasyFootballException) {
			handleInvalidTeam(getTeamId(), getGameState(), server, pFantasyFootballException);
			return;
		}
		if ((team == null) || !StringTool.isProvided(team.getName())) {
			handleInvalidTeam(getTeamId(), getGameState(), server, null);
			return;
		}
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
}
