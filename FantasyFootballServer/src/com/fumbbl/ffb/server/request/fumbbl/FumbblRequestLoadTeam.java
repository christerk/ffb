package com.fumbbl.ffb.server.request.fumbbl;

import com.fumbbl.ffb.FantasyFootballException;
import com.fumbbl.ffb.GameStatus;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerLogLevel;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.net.commands.InternalServerCommandFumbblTeamLoaded;
import com.fumbbl.ffb.server.request.ServerRequest;
import com.fumbbl.ffb.server.request.ServerRequestProcessor;
import com.fumbbl.ffb.util.StringTool;
import org.eclipse.jetty.websocket.api.Session;

import java.util.List;

import static com.fumbbl.ffb.server.util.UtilServerGame.handleInvalidTeam;

/**
 * @author Kalimar
 */
public class FumbblRequestLoadTeam extends ServerRequest {

	private final String fCoach;
	private final String fTeamId;
	private final boolean fHomeTeam;
	private final GameState fGameState;
	private final List<String> fAccountProperties;

	private final transient Session fSession;

	public FumbblRequestLoadTeam(GameState pGameState, String pCoach, String pTeamId, boolean pHomeTeam,
	                             Session pSession, List<String> pAccountProperties) {
		fGameState = pGameState;
		fCoach = pCoach;
		fTeamId = pTeamId;
		fHomeTeam = pHomeTeam;
		fSession = pSession;
		fAccountProperties = pAccountProperties;
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
	
	public List<String> getAccountProperties() {
		return fAccountProperties;
	}

	@Override
	public void process(ServerRequestProcessor pRequestProcessor) {
		FantasyFootballServer server = pRequestProcessor.getServer();
		Game game = getGameState().getGame();
		Team team;
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
		game.teamsAreSkeletons();
		server.getGameCache().addTeamToGame(getGameState(), team, isHomeTeam());
		if (GameStatus.SCHEDULED == getGameState().getStatus()) {
			if (StringTool.isProvided(game.getTeamHome().getId()) && StringTool.isProvided(game.getTeamAway().getId())) {
				// log game scheduled -->
				if (server.getDebugLog().isLogging(IServerLogLevel.WARN)) {
					String logEntry = "GAME SCHEDULED " + StringTool.print(game.getTeamHome().getName()) + " vs. " +
						StringTool.print(game.getTeamAway().getName());
					server.getDebugLog().log(IServerLogLevel.WARN, getGameState().getId(), logEntry);
				}
				// <-- log game scheduled
			}
		} else {
			InternalServerCommandFumbblTeamLoaded loadedCommand = new InternalServerCommandFumbblTeamLoaded(
					getGameState().getId(), getCoach(), isHomeTeam(), getAccountProperties());
			server.getCommunication().handleCommand(new ReceivedCommand(loadedCommand, getSession()));
		}
	}
}
