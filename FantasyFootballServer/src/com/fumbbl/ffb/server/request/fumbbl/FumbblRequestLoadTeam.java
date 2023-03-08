package com.fumbbl.ffb.server.request.fumbbl;

import com.fumbbl.ffb.FantasyFootballException;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.net.commands.InternalServerCommandAddLoadedTeam;
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
	private final Boolean fHomeTeam;
	private final GameState fGameState;
	private final List<String> fAccountProperties;

	private final transient Session fSession;

	public FumbblRequestLoadTeam(GameState pGameState, String pCoach, String pTeamId, Boolean pHomeTeam,
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


	public Session getSession() {
		return fSession;
	}
	
	public List<String> getAccountProperties() {
		return fAccountProperties;
	}

	@Override
	public void process(ServerRequestProcessor pRequestProcessor) {
		FantasyFootballServer server = pRequestProcessor.getServer();
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

		server.getCommunication().handleCommand(
			new ReceivedCommand(new InternalServerCommandAddLoadedTeam(fGameState, fCoach, fHomeTeam, team, fAccountProperties), fSession));
	}
}
