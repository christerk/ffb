package com.balancedbytes.games.ffb.server;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.jetty.websocket.api.Session;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.GameList;
import com.balancedbytes.games.ffb.GameListEntry;
import com.balancedbytes.games.ffb.GameStatus;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.SendToBoxReason;
import com.balancedbytes.games.ffb.TurnMode;
import com.balancedbytes.games.ffb.Weather;
import com.balancedbytes.games.ffb.dialog.DialogStartGameParameter;
import com.balancedbytes.games.ffb.model.FieldModel;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.PlayerResult;
import com.balancedbytes.games.ffb.model.Roster;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.server.db.DbStatementId;
import com.balancedbytes.games.ffb.server.db.DbTransaction;
import com.balancedbytes.games.ffb.server.db.delete.DbGamesInfoDeleteParameter;
import com.balancedbytes.games.ffb.server.db.delete.DbGamesSerializedDeleteParameter;
import com.balancedbytes.games.ffb.server.db.delete.DbPlayerMarkersDeleteParameter;
import com.balancedbytes.games.ffb.server.db.insert.DbGamesSerializedInsertParameter;
import com.balancedbytes.games.ffb.server.db.insert.DbPlayerMarkersInsertParameterList;
import com.balancedbytes.games.ffb.server.db.query.DbGameListQueryOpenGamesByCoach;
import com.balancedbytes.games.ffb.server.db.query.DbGamesInfoInsertQuery;
import com.balancedbytes.games.ffb.server.db.query.DbGamesSerializedQuery;
import com.balancedbytes.games.ffb.server.db.update.DbGamesInfoUpdateParameter;
import com.balancedbytes.games.ffb.server.db.update.DbGamesSerializedUpdateParameter;
import com.balancedbytes.games.ffb.server.net.SessionManager;
import com.balancedbytes.games.ffb.server.request.fumbbl.FumbblRequestRemoveGamestate;
import com.balancedbytes.games.ffb.server.util.UtilServerTimer;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.DateTool;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.util.UtilBox;
import com.balancedbytes.games.ffb.util.UtilTeamValue;

/**
 * 
 * @author Kalimar
 */
public class GameCache {

  private FantasyFootballServer fServer;
  private Map<Long, GameState> fGameStateById;
  private Map<String, Long> fGameIdByName;
  private RosterCache fRosterCache;
  private TeamCache fTeamCache; // used in standalone mode only

  private static final String _PITCHES_INI = "pitches.ini";
  private static final String _PITCH_PROPERTY_PREFIX = "pitch.";
  
  public GameCache(FantasyFootballServer pServer) {
    fServer = pServer;
    fGameStateById = Collections.synchronizedMap(new HashMap<Long, GameState>());
    fGameIdByName = Collections.synchronizedMap(new HashMap<String, Long>());
    fRosterCache = new RosterCache();
    fTeamCache = new TeamCache();
  }

  public void init() {
    loadPitchProperties();
    if (ServerMode.STANDALONE == getServer().getMode()) {
      try {
        fRosterCache.init(new File("rosters"));
      } catch (IOException ioe) {
        throw new FantasyFootballException(ioe);
      }
      try {
        fTeamCache.init(new File("teams"));
      } catch (IOException ioe) {
        throw new FantasyFootballException(ioe);
      }
      for (Team team : fTeamCache.getTeams()) {
        team.updateRoster(getRosterById(team.getRosterId()));
        team.setTeamValue(UtilTeamValue.findTeamValue(team));
      }
    }
  }

  public FantasyFootballServer getServer() {
    return fServer;
  }

  public GameState getGameStateById(long pGameId) {
    return fGameStateById.get(pGameId);
  }

  public void addGame(GameState gameState) {
    if (gameState == null) {
      return;
    }
    GameState oldState = fGameStateById.put(gameState.getId(), gameState);
    if (oldState != null) {
      return;
    }
    if (GameStatus.PAUSED == gameState.getStatus()) {
      gameState.setStatus(GameStatus.ACTIVE);
    }
    if (GameStatus.STARTING != gameState.getStatus()) {
      UtilServerTimer.syncTime(gameState);
      UtilServerTimer.startTurnTimer(gameState);
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
    Long[] gameIds = fGameStateById.keySet().toArray(new Long[fGameStateById.size()]);
    for (Long gameId : gameIds) {
      GameStatus status = fGameStateById.get(gameId).getStatus();
      if ((gameId != null) && (gameId != gameState.getId()) && (status != GameStatus.LOADING) && !checkGameOpen(gameId)) {
        removeGame(gameId);
      }
    }
  }

  public GameState getGameStateByName(String pGameName, boolean pLoadFromDb) {
    Long gameId = fGameIdByName.get(pGameName);
    return (gameId != null) ? getGameStateById(gameId) : null;
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
        || ((game.getScheduled() == null) && ((game.getStarted() == null) || DateTool.isEqual(new Date(0), game.getStarted())))) {
        queueDbDelete(cachedGameState.getId(), true);
      }
    }
  }

  public boolean mapGameNameToId(String pGameName, long pGameId) {
    GameState gameState = getGameStateByName(pGameName, true);
    boolean mappingOk = ((gameState == null) || (pGameId == gameState.getId()));
    if (mappingOk) {
      fGameIdByName.put(pGameName, pGameId);
    }
    return mappingOk;
  }

  public boolean removeMappingForGameId(long pGameId) {
    String gameNameForId = null;
    for (String gameName : fGameIdByName.keySet()) {
      long gameId = fGameIdByName.get(gameName);
      if (pGameId == gameId) {
        gameNameForId = gameName;
        break;
      }
    }
    return (fGameIdByName.remove(gameNameForId) != null);
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
    // DbGameListQueryOpenGamesByCoachOld queryOpenGamesOld = (DbGameListQueryOpenGamesByCoachOld)
    // getServer().getDbQueryFactory().getStatement(DbStatementId.GAME_LIST_QUERY_OPEN_GAMES_BY_COACH_OLD);
    // queryOpenGamesOld.execute(gameList, pCoach);
    return gameList;
  }

  public GameState createGameState(GameStartMode pMode) {
    Game game = new Game();
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
    DbGamesInfoInsertQuery insertQuery = (DbGamesInfoInsertQuery) getServer().getDbQueryFactory().getStatement(DbStatementId.GAMES_INFO_INSERT_QUERY);
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

  public void add(Roster pRoster) {
    fRosterCache.add(pRoster);
  }

  public Roster getRosterById(String pRosterId) {
    return fRosterCache.getRosterById(pRosterId);
  }

  public Team getTeamById(String pTeamId) {
    return fTeamCache.getTeamById(pTeamId);
  }

  public void refresh() {
    fRosterCache.clear();
    loadPitchProperties();
  }

  private void loadPitchProperties() {
    Properties pitchProperties = new Properties();
    try {
      // load pitch properties
      BufferedInputStream propertyInputStream = new BufferedInputStream(new FileInputStream(_PITCHES_INI));
      pitchProperties.load(propertyInputStream);
      propertyInputStream.close();
    } catch (IOException pIoException) {
      getServer().getDebugLog().log(pIoException);
    }
    // clear old pitch properties
    for (String serverProperty : getServer().getProperties()) {
      if (StringTool.isProvided(serverProperty) && serverProperty.startsWith(_PITCH_PROPERTY_PREFIX)) {
        getServer().removeProperty(serverProperty);
      }
    }
    // add new pitch properties
    String[] pitchKeys = pitchProperties.keySet().toArray(new String[pitchProperties.size()]);
    for (String pitchKey : pitchKeys) {
      String pitchUrl = pitchProperties.getProperty(pitchKey);
      if (!pitchKey.startsWith(_PITCH_PROPERTY_PREFIX)) {
        getServer().setProperty(_PITCH_PROPERTY_PREFIX + pitchKey, pitchUrl);
      } else {
        getServer().setProperty(pitchKey, pitchUrl);
      }
    }
  }

  public void addTeamToGame(GameState pGameState, Team pTeam, boolean pHomeTeam) {
    Game game = pGameState.getGame();
    Player[] players = pTeam.getPlayers();
    if (pHomeTeam) {
      game.getFieldModel().remove(game.getTeamHome());
      game.setTeamHome(pTeam);
    } else {
      game.getFieldModel().remove(game.getTeamAway());
      game.setTeamAway(pTeam);
    }
    FieldModel fieldModel = pGameState.getGame().getFieldModel();
    for (int i = 0; i < players.length; i++) {
      if (players[i].getRecoveringInjury() != null) {
        fieldModel.setPlayerState(players[i], new PlayerState(PlayerState.MISSING));
        game.getGameResult().getPlayerResult(players[i]).setSendToBoxReason(SendToBoxReason.MNG);
      } else {
        fieldModel.setPlayerState(players[i], new PlayerState(PlayerState.RESERVE));
      }
      UtilBox.putPlayerIntoBox(game, players[i]);
      if (players[i].getCurrentSpps() > 0) {
        PlayerResult playerResult = game.getGameResult().getPlayerResult(players[i]);
        playerResult.setCurrentSpps(players[i].getCurrentSpps());
      }
    }
    queueDbUpdate(pGameState, true);
  }

  public Team[] getTeamsForCoach(String pCoach) {
    return fTeamCache.getTeamsForCoach(pCoach);
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
    GameState gameState = getServer().getDbUpdater().findGameState(gameId);
    if (gameState == null) {
      DbGamesSerializedQuery gameQuery = (DbGamesSerializedQuery) getServer().getDbQueryFactory().getStatement(DbStatementId.GAMES_SERIALIZED_QUERY);
      gameState = gameQuery.execute(getServer(), gameId);
    }
    return gameState;
  }

  public GameState closeGame(long gameId) {
    if (gameId <= 0) {
      return null;
    }
    GameState gameState = getGameStateById(gameId);
    if (gameState != null) {
      SessionManager sessionManager = getServer().getSessionManager();
      Session[] sessions = sessionManager.getSessionsForGameId(gameState.getId());
      for (Session session : sessions) {
        getServer().getCommunication().close(session);
      }
      removeGame(gameId);
      if (getServer().getMode() == ServerMode.FUMBBL) {
        getServer().getRequestProcessor().add(new FumbblRequestRemoveGamestate(gameState));
      }
    }
    return gameState;
  }

  private boolean checkGameOpen(long pGameId) {
    GameState gameState = getGameStateById(pGameId);
    if (gameState != null) {
      SessionManager sessionManager = getServer().getSessionManager();
      Session[] sessions = sessionManager.getSessionsForGameId(gameState.getId());
      // no sessions connected yet - game starting
      if (!ArrayTool.isProvided(sessions)) {
        return true;
      }
      // check connected sessions and find an open one
      for (Session session : sessions) {
        if ((session != null) && session.isOpen()) {
          return true;
        }
      }
    }
    return false;
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
