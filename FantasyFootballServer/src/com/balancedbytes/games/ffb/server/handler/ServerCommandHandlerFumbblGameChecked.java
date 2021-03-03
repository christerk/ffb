package com.balancedbytes.games.ffb.server.handler;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.model.Roster;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.model.TeamSkeleton;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.net.ServerStatus;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerLogLevel;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.net.commands.InternalServerCommandFumbblGameChecked;
import com.balancedbytes.games.ffb.server.request.fumbbl.UtilFumbblRequest;
import com.balancedbytes.games.ffb.server.util.UtilServerGame;
import com.balancedbytes.games.ffb.server.util.UtilServerStartGame;
import com.balancedbytes.games.ffb.server.util.UtilSkillBehaviours;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.xml.XmlHandler;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

/**
 * 
 * @author Kalimar
 */
public class ServerCommandHandlerFumbblGameChecked extends ServerCommandHandler {

	protected ServerCommandHandlerFumbblGameChecked(FantasyFootballServer pServer) {
		super(pServer);
	}

	public NetCommandId getId() {
		return NetCommandId.INTERNAL_SERVER_FUMBBL_GAME_CHECKED;
	}

	public boolean handleCommand(ReceivedCommand pReceivedCommand) {
		InternalServerCommandFumbblGameChecked gameCheckedCommand = (InternalServerCommandFumbblGameChecked) pReceivedCommand
				.getCommand();
		GameState gameState = getServer().getGameCache().getGameStateById(gameCheckedCommand.getGameId());
		gameState.getGame().initializeRules();
		UtilSkillBehaviours.registerBehaviours(gameState.getGame(), getServer().getDebugLog());

		TeamSkeleton homeSkeleton = (TeamSkeleton) gameState.getGame().getTeamHome();
		TeamSkeleton awaySkeleton = (TeamSkeleton) gameState.getGame().getTeamAway();
		Team home = inflateTeam(homeSkeleton, gameState);
		Team away = inflateTeam(awaySkeleton, gameState);

		if (home == null || away == null) {
			return false;
		}

		Roster rosterHome = getRoster(gameState, home.getId());
		Roster rosterAway = getRoster(gameState, away.getId());
		if (rosterHome == null || rosterAway == null) {
			return false;
		}

		home.updateRoster(rosterHome, gameState.getGame().getRules());
		away.updateRoster(rosterAway, gameState.getGame().getRules());

		getServer().getGameCache().addTeamToGame(gameState, home, true);
		getServer().getGameCache().addTeamToGame(gameState, away, false);

		UtilServerStartGame.startGame(gameState);
		return true;
	}

	private Team inflateTeam(TeamSkeleton skeleton, GameState gameState) {
		Team team = new Team(gameState.getGame().getRules());
		try {
			try (BufferedReader xmlReader = new BufferedReader(new StringReader(skeleton.getXmlContent()))) {
				InputSource xmlSource = new InputSource(xmlReader);
				XmlHandler.parse(gameState.getGame(), xmlSource, team);
			}
		} catch (IOException e) {
			UtilServerGame.handleInvalidTeam(skeleton.getId(), gameState, getServer(), e);
			return null;
		}
		return team;
	}

	private Roster getRoster(GameState gameState, String teamId) {
		Roster roster;
		try {
			roster = UtilFumbblRequest.loadFumbblRosterForTeam(gameState.getGame(), getServer(), teamId);
		} catch (FantasyFootballException pFantasyFootballException) {
			handleInvalidRoster(teamId, gameState, getServer(), pFantasyFootballException);
			return null;
		}
		if ((roster == null) || !StringTool.isProvided(roster.getName())) {
			handleInvalidRoster(teamId, gameState, getServer(), null);
			return null;
		}
		return roster;
	}

	// this might be overkill, we'll see how it does in practice
	private void handleInvalidRoster(String pTeamId, GameState gameState, FantasyFootballServer server, Throwable pThrowable) {
		server.getDebugLog().log(IServerLogLevel.ERROR, StringTool.bind("Error loading Roster for Team $1.", pTeamId));
		server.getDebugLog().log(pThrowable);
		server.getCommunication().sendStatus(gameState, ServerStatus.FUMBBL_ERROR,
			StringTool.bind("Unable to load Roster with for Team $1.", pTeamId));
		UtilServerGame.closeGame(gameState);
	}
}
