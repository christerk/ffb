package com.fumbbl.ffb.server.handler;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.GameStatus;
import com.fumbbl.ffb.TeamList;
import com.fumbbl.ffb.TeamListEntry;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.ServerStatus;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameCache;
import com.fumbbl.ffb.server.GameStartMode;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerLogLevel;
import com.fumbbl.ffb.server.ServerMode;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.net.ServerCommunication;
import com.fumbbl.ffb.server.net.SessionManager;
import com.fumbbl.ffb.server.net.commands.InternalServerCommandJoinApproved;
import com.fumbbl.ffb.server.request.fumbbl.FumbblRequestCheckGamestate;
import com.fumbbl.ffb.server.request.fumbbl.FumbblRequestLoadTeam;
import com.fumbbl.ffb.server.request.fumbbl.FumbblRequestLoadTeamList;
import com.fumbbl.ffb.server.util.UtilServerStartGame;
import com.fumbbl.ffb.server.util.UtilServerTimer;
import com.fumbbl.ffb.server.util.UtilSkillBehaviours;
import com.fumbbl.ffb.util.StringTool;
import org.eclipse.jetty.websocket.api.Session;

/**
 * 
 * @author Kalimar
 */
public class ServerCommandHandlerJoinApproved extends ServerCommandHandler {

	private static final String _TEST_PREFIX = "test:";

	protected ServerCommandHandlerJoinApproved(FantasyFootballServer pServer) {
		super(pServer);
	}

	public NetCommandId getId() {
		return NetCommandId.INTERNAL_SERVER_JOIN_APPROVED;
	}

	public boolean handleCommand(ReceivedCommand receivedCommand) {

		InternalServerCommandJoinApproved joinApprovedCommand = (InternalServerCommandJoinApproved) receivedCommand
				.getCommand();
		ServerCommunication communication = getServer().getCommunication();
		SessionManager sessionManager = getServer().getSessionManager();
		GameCache gameCache = getServer().getGameCache();
		GameState gameState = null;
		Session session = receivedCommand.getSession();

		if (joinApprovedCommand.getGameId() > 0) {
			getServer().getDebugLog().log(IServerLogLevel.WARN, joinApprovedCommand.getGameId(),
				"Loading GameState by id");
			gameState = loadGameStateById(joinApprovedCommand);

		} else if (StringTool.isProvided(joinApprovedCommand.getGameName())) {
			getServer().getDebugLog().log(IServerLogLevel.WARN, joinApprovedCommand.getGameId(),
				"Loading GameState by name: " + joinApprovedCommand.getGameName());

			gameState = gameCache.getGameStateByName(joinApprovedCommand.getGameName());

			String info = "";
			long id = 0;
			if (gameState == null) {
				info = "not ";
			} else {
				id = gameState.getId();
			}
			getServer().getDebugLog().log(IServerLogLevel.WARN, id,
				"GameState " + info + "found by name: " + joinApprovedCommand.getGameName());

			if ((gameState == null) && !getServer().isBlockingNewGames()) {
				boolean testing = (joinApprovedCommand.getGameName().startsWith(_TEST_PREFIX)
					|| getServer().getMode() == ServerMode.STANDALONE) || getServer().isInTestMode();
				gameState = gameCache.createGameState(testing ? GameStartMode.START_TEST_GAME : GameStartMode.START_GAME);
				gameCache.mapGameNameToId(joinApprovedCommand.getGameName(), gameState.getId());
				getServer().getDebugLog().log(IServerLogLevel.WARN, gameState.getId(),
					"GameState created by name: " + joinApprovedCommand.getGameName());
			}
		}

		if (gameState != null) {

			Game game = gameState.getGame();

			if (joinApprovedCommand.getClientMode() == ClientMode.PLAYER) {
				getServer().getDebugLog().log(IServerLogLevel.WARN, gameState.getId(),
					"Joining as player");
				if (joinApprovedCommand.getCoach().equalsIgnoreCase(game.getTeamHome().getCoach())
					|| joinApprovedCommand.getCoach().equalsIgnoreCase(game.getTeamAway().getCoach())) {
					if ((gameState.getStatus() == GameStatus.SCHEDULED) || (game.getStarted() != null)) {
						joinWithoutTeam(gameState, joinApprovedCommand, session);
					} else {
						if (StringTool.isProvided(joinApprovedCommand.getTeamId())) {
							joinWithTeam(gameState, joinApprovedCommand, session);
						} else {
							sendTeamList(gameState, joinApprovedCommand, session);
						}
					}

				} else if (game.getStarted() != null) {
					communication.sendStatus(session, ServerStatus.ERROR_GAME_IN_USE, null);

				} else if (!StringTool.isProvided(joinApprovedCommand.getTeamId())) {
					sendTeamList(gameState, joinApprovedCommand, session);

				} else {
					joinWithTeam(gameState, joinApprovedCommand, session);
				}

				// ClientMode.SPECTATOR
			} else {

				getServer().getDebugLog().log(IServerLogLevel.WARN, gameState.getId(),
					"Joining as spectator");
				closeOtherSessionWithThisCoach(gameState, joinApprovedCommand.getCoach(), session);
				sessionManager.addSession(session, gameState.getId(), joinApprovedCommand.getCoach(),
					joinApprovedCommand.getClientMode(), false, joinApprovedCommand.getAccountProperties());
				UtilServerStartGame.sendServerJoin(gameState, session, joinApprovedCommand.getCoach(), false,
					ClientMode.SPECTATOR, joinApprovedCommand.getAccountProperties());
				if (gameState.getGame().getStarted() != null) {
					UtilServerTimer.syncTime(gameState, System.currentTimeMillis());
					communication.sendGameState(session, gameState);
				}

			}

		} else if (joinApprovedCommand.getClientMode() == ClientMode.REPLAY) {


			getServer().getDebugLog().logWithOutGameId(IServerLogLevel.WARN,
				"Authenticating coach " + joinApprovedCommand.getCoach() + " for replay");

			UtilServerStartGame.sendUserSettings(getServer(), joinApprovedCommand.getCoach(), session);
		}

		return true;

	}

	private void joinWithoutTeam(GameState pGameState, InternalServerCommandJoinApproved pJoinApprovedCommand,
			Session pSession) {
		Game game = pGameState.getGame();
		if (pJoinApprovedCommand.getCoach().equalsIgnoreCase(game.getTeamHome().getCoach())
				|| pJoinApprovedCommand.getCoach().equalsIgnoreCase(game.getTeamAway().getCoach())) {
			getServer().getDebugLog().log(IServerLogLevel.WARN, pGameState.getId(),
				"Joining without team");
			if (!game.isTesting()) {
				closeOtherSessionWithThisCoach(pGameState, pJoinApprovedCommand.getCoach(), pSession);
			}
			boolean homeTeam = pJoinApprovedCommand.getCoach().equalsIgnoreCase(game.getTeamHome().getCoach());
			if (UtilServerStartGame.joinGameAsPlayerAndCheckIfReadyToStart(pGameState, pSession,
				pJoinApprovedCommand.getCoach(), homeTeam, pJoinApprovedCommand.getAccountProperties())) {
				if (getServer().getMode() == ServerMode.FUMBBL) {
					if (game.getStarted() != null) {
						getServer().getDebugLog().log(IServerLogLevel.WARN, pGameState.getId(),
							"Kick starting");
						//noinspection SynchronizationOnLocalVariableOrMethodParameter
						synchronized (pGameState) {
							// Game is already initialized, so we just need to kickstart it
							UtilSkillBehaviours.registerBehaviours(pGameState.getGame(), getServer().getDebugLog());
							UtilServerStartGame.startGame(pGameState);
						}
					} else {
						getServer().getDebugLog().log(IServerLogLevel.WARN, pGameState.getId(),
							"Check gamestate");
						// This is a new game and we need to get options from FUMBBL
						getServer().getRequestProcessor().add(new FumbblRequestCheckGamestate(pGameState));
					}
				} else {
					UtilServerStartGame.addDefaultGameOptions(pGameState);
					UtilSkillBehaviours.registerBehaviours(pGameState.getGame(), getServer().getDebugLog());
					UtilServerStartGame.startGame(pGameState);
				}
			}
		}
	}

	private void joinWithTeam(GameState pGameState, InternalServerCommandJoinApproved pJoinApprovedCommand,
			Session pSession) {
		if ((pGameState != null) && StringTool.isProvided(pJoinApprovedCommand.getTeamId())) {
			Game game = pGameState.getGame();
			if (!game.isTesting()) {
				closeOtherSessionWithThisCoach(pGameState, pJoinApprovedCommand.getCoach(), pSession);
			}
			if (getServer().getMode() == ServerMode.FUMBBL) {
				getServer().getRequestProcessor().add(new FumbblRequestLoadTeam(pGameState, pJoinApprovedCommand.getCoach(),
					pJoinApprovedCommand.getTeamId(), null, pSession, pJoinApprovedCommand.getAccountProperties()));
			} else {
				boolean homeTeam = (!StringTool.isProvided(game.getTeamHome().getId())
					|| pJoinApprovedCommand.getTeamId().equals(game.getTeamHome().getId()));
				Team teamSkeleton = getServer().getGameCache().getTeamSkeleton(pJoinApprovedCommand.getTeamId());
				getServer().getGameCache().addTeamToGame(pGameState, teamSkeleton, homeTeam);
				if (UtilServerStartGame.joinGameAsPlayerAndCheckIfReadyToStart(pGameState, pSession,
					pJoinApprovedCommand.getCoach(), homeTeam, pJoinApprovedCommand.getAccountProperties())) {
					UtilServerStartGame.addDefaultGameOptions(pGameState);
					pGameState.initRulesDependentMembers();
					pGameState.getGame().initializeRules();
					UtilSkillBehaviours.registerBehaviours(pGameState.getGame(), getServer().getDebugLog());
					Team teamHome = getServer().getGameCache().getTeamById(game.getTeamHome().getId(), game);
					getServer().getGameCache().addTeamToGame(pGameState, teamHome, true);
					Team teamAway = getServer().getGameCache().getTeamById(game.getTeamAway().getId(), game);
					getServer().getGameCache().addTeamToGame(pGameState, teamAway, false);
					UtilServerStartGame.startGame(pGameState);
				}
			}
		}
	}

	private void closeOtherSessionWithThisCoach(GameState gameState, String coach, Session session) {
		SessionManager sessionManager = getServer().getSessionManager();
		Session[] allSessions = sessionManager.getSessionsForGameId(gameState.getId());
		for (Session existingSession : allSessions) {
			if ((session != existingSession) && coach.equalsIgnoreCase(sessionManager.getCoachForSession(existingSession))) {
				getServer().getCommunication().close(existingSession);
				break;
			}
		}
	}

	private GameState loadGameStateById(InternalServerCommandJoinApproved pJoinApprovedCommand) {
		GameCache gameCache = getServer().getGameCache();
		GameState gameState = gameCache.getGameStateById(pJoinApprovedCommand.getGameId());
		if (gameState != null) {
			getServer().getDebugLog().log(IServerLogLevel.WARN, pJoinApprovedCommand.getGameId(),
				"GameState found in cache");
			return gameState;
		}
		getServer().getDebugLog().log(IServerLogLevel.WARN, pJoinApprovedCommand.getGameId(),
			"GameState not found in cache, checking database");
		gameState = gameCache.queryFromDb(pJoinApprovedCommand.getGameId());
		if (gameState == null) {
			getServer().getDebugLog().log(IServerLogLevel.WARN, pJoinApprovedCommand.getGameId(),
				"GameState not found in database returning null");
			return null;
		}
		getServer().getDebugLog().log(IServerLogLevel.WARN, pJoinApprovedCommand.getGameId(),
			"GameState found in database");

		gameCache.addGame(gameState);
		gameCache.queueDbUpdate(gameState, true); // persist status update
		return gameState;
	}

	private void sendTeamList(GameState pGameState, InternalServerCommandJoinApproved pJoinApprovedCommand,
			Session pSession) {
		if (getServer().getMode() == ServerMode.FUMBBL) {
			getServer().getRequestProcessor()
					.add(new FumbblRequestLoadTeamList(pGameState, pJoinApprovedCommand.getCoach(), pSession));
		} else {
			TeamList teamList = new TeamList();
			// In STANDALONE mode, we need to initialize the rules before we start parsing teams.
			pGameState.initRulesDependentMembers();
			pGameState.getGame().initializeRules();
			Team[] teams = getServer().getGameCache().getTeamsForCoach(pJoinApprovedCommand.getCoach(), pGameState.getGame());
			for (Team team : teams) {
				TeamListEntry teamEntry = new TeamListEntry();
				teamEntry.init(team);
				teamList.add(teamEntry);
			}
			getServer().getCommunication().sendTeamList(pSession, teamList);
		}
	}

}
