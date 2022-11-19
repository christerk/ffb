package com.fumbbl.ffb.server;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.FantasyFootballException;
import com.fumbbl.ffb.GameList;
import com.fumbbl.ffb.GameListEntry;
import com.fumbbl.ffb.GameStatus;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.SendToBoxReason;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.dialog.DialogStartGameParameter;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.PlayerResult;
import com.fumbbl.ffb.model.RosterPlayer;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.TeamSkeleton;
import com.fumbbl.ffb.model.ZappedPlayer;
import com.fumbbl.ffb.server.db.DbStatementId;
import com.fumbbl.ffb.server.db.DbTransaction;
import com.fumbbl.ffb.server.db.delete.DbGamesInfoDeleteParameter;
import com.fumbbl.ffb.server.db.delete.DbGamesSerializedDeleteParameter;
import com.fumbbl.ffb.server.db.delete.DbPlayerMarkersDeleteParameter;
import com.fumbbl.ffb.server.db.insert.DbGamesSerializedInsertParameter;
import com.fumbbl.ffb.server.db.insert.DbPlayerMarkersInsertParameterList;
import com.fumbbl.ffb.server.db.query.DbGameListQueryOpenGamesByCoach;
import com.fumbbl.ffb.server.db.query.DbGamesInfoInsertQuery;
import com.fumbbl.ffb.server.db.query.DbGamesSerializedQuery;
import com.fumbbl.ffb.server.db.update.DbGamesInfoUpdateParameter;
import com.fumbbl.ffb.server.db.update.DbGamesSerializedUpdateParameter;
import com.fumbbl.ffb.server.net.SessionManager;
import com.fumbbl.ffb.server.request.fumbbl.FumbblRequestRemoveGamestate;
import com.fumbbl.ffb.util.DateTool;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilBox;
import com.fumbbl.ffb.util.UtilTeamValue;
import org.eclipse.jetty.websocket.api.Session;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Kalimar
 */
public class GameCache {

	private final FantasyFootballServer fServer;
	private final Map<Long, GameState> fGameStateById;
	private final Map<String, Long> fGameIdByName;
	private RosterCache rosterCache;
	private TeamCache teamCache; // used in standalone mode only

	public GameCache(FantasyFootballServer pServer) {
		fServer = pServer;
		fGameStateById = Collections.synchronizedMap(new HashMap<>());
		fGameIdByName = Collections.synchronizedMap(new HashMap<>());
	}

	public void init() {
		if (ServerMode.STANDALONE == getServer().getMode()) {
			rosterCache = new RosterCache();
			teamCache = new TeamCache();
			try {
				rosterCache.init(new File("rosters"));
			} catch (IOException ioe) {
				throw new FantasyFootballException(ioe);
			}
			try {
				teamCache.init(new File("teams"), fServer);
			} catch (IOException ioe) {
				throw new FantasyFootballException(ioe);
			}
		}
	}

	public FantasyFootballServer getServer() {
		return fServer;
	}

	public GameState getGameStateById(long pGameId) {
		return fGameStateById.get(pGameId);
	}

	public GameState[] allGameStates() {
		return fGameStateById.values().toArray(new GameState[0]);
	}

	public void addGame(GameState gameState) {
		if (gameState == null) {
			return;
		}
		GameState oldState = fGameStateById.put(gameState.getId(), gameState);
		if (oldState != null) {
			return;
		}
		// log game cache size
		FantasyFootballServer server = gameState.getServer();
		if (server.getDebugLog().isLogging(IServerLogLevel.WARN)) {
			StringBuilder log = new StringBuilder();
			log.append("ADD GAME");
			if (gameState.getGame().isTesting()) {
				log.append(" [test]");
			}
			if (gameState.getGame().isAdminMode()) {
				log.append(" [admin]");
			}
			log.append(" cache increases to ").append(fGameStateById.size()).append(" games.");
			server.getDebugLog().log(IServerLogLevel.WARN, gameState.getId(), log.toString());
		}
		// remove dead games from cache if there are no connections to the session
		SessionManager sessionManager = getServer().getSessionManager();
		Long[] gameIds = fGameStateById.keySet().toArray(new Long[0]);
		for (Long gameId : gameIds) {
			GameStatus status = fGameStateById.get(gameId).getStatus();
			if ((gameId == null) || (gameId == gameState.getId()) || (status == GameStatus.LOADING)) {
				continue;
			}
			Session[] sessions = sessionManager.getSessionsForGameId(gameId);
			if ((sessions.length == 0)
					|| ((sessions.length == 1) && ((ClientMode.SPECTATOR == sessionManager.getModeForSession(sessions[0]))
							|| (status == GameStatus.BACKUPED)))) {
				closeGame(gameId);
			}
		}
	}

	public GameState getGameStateByName(String pGameName) {
		Long gameId = fGameIdByName.get(pGameName);
		return (gameId != null) ? getGameStateById(gameId) : null;
	}

	public String getGameName(long id) {
		return fGameIdByName.entrySet().stream().filter(entry -> entry.getValue() == id).findFirst().map(Map.Entry::getKey).orElse("");
	}


	private void removeGame(long gameId) {
		GameState cachedGameState = fGameStateById.remove(gameId);
		if (cachedGameState != null) {
			Game game = cachedGameState.getGame();
			removeMappingForGameId(cachedGameState.getId());
			// log game cache size
			getServer().getDebugLog().log(IServerLogLevel.WARN, cachedGameState.getId(),
				StringTool.bind("REMOVE GAME cache decreases to $1 games.", fGameStateById.size()));
			// remove gameState from db if only one team has joined
			// or the game hasn't even started yet (and isn't scheduled)
			if (!StringTool.isProvided(game.getTeamHome().getId()) || !StringTool.isProvided(game.getTeamAway().getId())
				|| ((game.getScheduled() == null)
				&& ((game.getStarted() == null) || DateTool.isEqual(new Date(0), game.getStarted())))) {
				queueDbDelete(cachedGameState.getId(), true);
			}
		}
	}

	public void mapGameNameToId(String pGameName, long pGameId) {
		GameState gameState = getGameStateByName(pGameName);
		boolean mappingOk = ((gameState == null) || (pGameId == gameState.getId()));
		if (mappingOk) {
			fGameIdByName.put(pGameName, pGameId);
		}
	}

	public void removeMappingForGameId(long pGameId) {
		String gameNameForId = null;
		for (String gameName : fGameIdByName.keySet()) {
			long gameId = fGameIdByName.get(gameName);
			if (pGameId == gameId) {
				gameNameForId = gameName;
				break;
			}
		}
		fGameIdByName.remove(gameNameForId);
	}

	public GameList findActiveGames() {
		GameList gameList = new GameList();
		for (GameState gameState : fGameStateById.values()) {
			if (GameStatus.ACTIVE == gameState.getStatus()) {
				GameListEntry listEntry = new GameListEntry();
				listEntry.init(gameState.getGame());
				gameList.add(listEntry);
			}
		}
		return gameList;
	}

	public GameList findOpenGamesForCoach(String pCoach) {
		GameList gameList = new GameList();
		DbGameListQueryOpenGamesByCoach queryOpenGames = (DbGameListQueryOpenGamesByCoach) getServer().getDbQueryFactory()
				.getStatement(DbStatementId.GAME_LIST_QUERY_OPEN_GAMES_BY_COACH);
		queryOpenGames.execute(gameList, pCoach);
		// old version:
		// DbGameListQueryOpenGamesByCoachOld queryOpenGamesOld =
		// (DbGameListQueryOpenGamesByCoachOld)
		// getServer().getDbQueryFactory().getStatement(DbStatementId.GAME_LIST_QUERY_OPEN_GAMES_BY_COACH_OLD);
		// queryOpenGamesOld.execute(gameList, pCoach);
		return gameList;
	}

	public GameState createGameState(GameStartMode pMode) {
		Game game = new Game(fServer.getFactorySource(), fServer.getFactoryManager());
		game.setId(0L); // id is unknown - will be determined by the db
		game.setTesting(GameStartMode.START_TEST_GAME == pMode);
		game.setHomePlaying(true);
		game.setTurnMode(TurnMode.START_GAME);
		game.setDialogParameter(new DialogStartGameParameter());
		game.getFieldModel().setWeather(Weather.NICE);
		GameState gameState = new GameState(getServer());
		gameState.setGame(game);
		if (GameStartMode.SCHEDULE_GAME == pMode) {
			gameState.setStatus(GameStatus.SCHEDULED);
			gameState.getGame().setScheduled(new Date());
		} else {
			gameState.setStatus(GameStatus.STARTING);
		}
		// insert the games info directly and generate an id
		DbGamesInfoInsertQuery insertQuery = (DbGamesInfoInsertQuery) getServer().getDbQueryFactory()
				.getStatement(DbStatementId.GAMES_INFO_INSERT_QUERY);
		insertQuery.execute(gameState);
		if (GameStartMode.SCHEDULE_GAME != pMode) {
			addGame(gameState);
		}
		// queue the game serialization
		DbTransaction transaction = new DbTransaction();
		transaction.add(new DbGamesSerializedInsertParameter(gameState));
		getServer().getDbUpdater().add(transaction);
		return gameState;
	}

	private Team updateRoster(Team team, Game game) {
		if (ServerMode.STANDALONE == getServer().getMode()) {
			team.updateRoster(rosterCache.getRosterById(team.getRosterId(), game), game.getRules());
			team.setTeamValue(UtilTeamValue.findTeamValue(team));
		}
		return team;
	}

	public Team getTeamById(String pTeamId, Game game) {
		return updateRoster(teamCache.getTeamById(pTeamId, game), game);
	}

	public TeamSkeleton getTeamSkeleton(String teamId) {
		return teamCache.getSkeleton(teamId);
	}

	public void addTeamToGame(GameState pGameState, Team pTeam, boolean pHomeTeam) {
		Game game = pGameState.getGame();
		Player<?>[] players = pTeam.getPlayers();
		if (pHomeTeam) {
			game.getFieldModel().remove(game.getTeamHome());
			game.setTeamHome(pTeam);
		} else {
			game.getFieldModel().remove(game.getTeamAway());
			game.setTeamAway(pTeam);
		}
		FieldModel fieldModel = pGameState.getGame().getFieldModel();
		for (Player<?> player : players) {
			if (player instanceof ZappedPlayer) {
				if (!pGameState.isZapped(player)) {
					player = ((ZappedPlayer) player).getOriginalPlayer();
					pTeam.addPlayer(player);
				}
			} else if (player instanceof RosterPlayer) {
				if (pGameState.isZapped(player)) {
					ZappedPlayer zappedPlayer = new ZappedPlayer();
					zappedPlayer.init((RosterPlayer) player, game.getRules());
					player = zappedPlayer;
					pTeam.addPlayer(player);
				}
			}

			if (player.getRecoveringInjury() != null) {
				fieldModel.setPlayerState(player, new PlayerState(PlayerState.MISSING));
				game.getGameResult().getPlayerResult(player).setSendToBoxReason(SendToBoxReason.MNG);
			} else {
				fieldModel.setPlayerState(player, new PlayerState(PlayerState.RESERVE));
			}
			UtilBox.putPlayerIntoBox(game, player);
			if (player.getCurrentSpps() > 0) {
				PlayerResult playerResult = game.getGameResult().getPlayerResult(player);
				playerResult.setCurrentSpps(player.getCurrentSpps());
			}
		}
		queueDbUpdate(pGameState, true);
	}

	public Team[] getTeamsForCoach(String pCoach, Game game) {
		Team[] teams = teamCache.getTeamsForCoach(pCoach, game);
		if (ServerMode.STANDALONE == getServer().getMode()) {
			return Arrays.stream(teams).map(team -> updateRoster(team, game)).toArray(Team[]::new);
		}

		return teams;
	}

	public void queueDbUpdate(GameState pGameState, boolean pWithSerialization) {
		if (pGameState == null) {
			return;
		}
		DbTransaction transaction = new DbTransaction();
		transaction.add(new DbGamesInfoUpdateParameter(pGameState));
		if (pWithSerialization) {
			transaction.add(new DbGamesSerializedUpdateParameter(pGameState));
		}
		getServer().getDbUpdater().add(transaction);
	}

	public void queueDbDelete(long pGameStateId, boolean pWithGamesInfo) {
		if (pGameStateId <= 0) {
			return;
		}
		DbTransaction deleteTransaction = new DbTransaction();
		if (pWithGamesInfo) {
			deleteTransaction.add(new DbGamesInfoDeleteParameter(pGameStateId));
		}
		deleteTransaction.add(new DbGamesSerializedDeleteParameter(pGameStateId));
		getServer().getDbUpdater().add(deleteTransaction);
	}

	public GameState queryFromDb(long gameId) {
		if (gameId <= 0) {
			return null;
		}
		DbGamesSerializedQuery gameQuery = (DbGamesSerializedQuery) getServer().getDbQueryFactory()
				.getStatement(DbStatementId.GAMES_SERIALIZED_QUERY);
		return gameQuery.execute(getServer(), gameId);
	}

	public void closeAllGames() {
		for (GameState gameState : allGameStates()) {
			closeGame(gameState.getId());
		}
	}

	public void closeGame(long gameId) {
		if (gameId <= 0) {
			return;
		}
		GameState gameState = getGameStateById(gameId);
		if (gameState != null) {
			SessionManager sessionManager = getServer().getSessionManager();
			Session[] sessions = sessionManager.getSessionsForGameId(gameState.getId());
			for (Session session : sessions) {
				getServer().getCommunication().close(session);
			}
			removeGame(gameId);
			if ((getServer().getMode() == ServerMode.FUMBBL) && (gameState.getStatus() != GameStatus.REPLAYING)
					&& (gameState.getStatus() != GameStatus.LOADING)) {
				getServer().getRequestProcessor().add(new FumbblRequestRemoveGamestate(gameState));
			}
		}
	}

	public void queueDbPlayerMarkersUpdate(GameState pGameState) {
		if (pGameState == null) {
			return;
		}
		DbTransaction transaction = new DbTransaction();
		Team teamHome = pGameState.getGame().getTeamHome();
		if ((teamHome != null) && StringTool.isProvided(teamHome.getId())) {
			transaction.add(new DbPlayerMarkersDeleteParameter(teamHome.getId()));
		}
		Team teamAway = pGameState.getGame().getTeamAway();
		if ((teamAway != null) && StringTool.isProvided(teamAway.getId())) {
			transaction.add(new DbPlayerMarkersDeleteParameter(teamAway.getId()));
		}
		DbPlayerMarkersInsertParameterList playerMarkersInsert = new DbPlayerMarkersInsertParameterList();
		playerMarkersInsert.initFrom(pGameState);
		transaction.add(playerMarkersInsert);
		getServer().getDbUpdater().add(transaction);
	}

}
