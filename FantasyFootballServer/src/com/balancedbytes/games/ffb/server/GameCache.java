package com.balancedbytes.games.ffb.server;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
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
import com.balancedbytes.games.ffb.server.db.insert.DbGamesInfoInsertParameter;
import com.balancedbytes.games.ffb.server.db.insert.DbGamesSerializedInsertParameter;
import com.balancedbytes.games.ffb.server.db.insert.DbPlayerMarkersInsertParameterList;
import com.balancedbytes.games.ffb.server.db.query.DbGameListQueryOpenGamesByCoach;
import com.balancedbytes.games.ffb.server.db.query.DbGamesSerializedQuery;
import com.balancedbytes.games.ffb.server.db.query.DbGamesSerializedQueryMaxId;
import com.balancedbytes.games.ffb.server.db.update.DbGamesInfoUpdateParameter;
import com.balancedbytes.games.ffb.server.db.update.DbGamesSerializedUpdateParameter;
import com.balancedbytes.games.ffb.server.net.SessionManager;
import com.balancedbytes.games.ffb.server.net.commands.InternalServerCommandBackupGame;
import com.balancedbytes.games.ffb.server.request.fumbbl.FumbblRequestRemoveGamestate;
import com.balancedbytes.games.ffb.server.util.UtilServerTimer;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.util.UtilBox;
import com.balancedbytes.games.ffb.util.UtilTeamValue;

/**
 * 
 * @author Kalimar
 */
public class GameCache {
	
  private FantasyFootballServer fServer;
  private IdGenerator fIdGenerator; 
  private Map<Long, GameState> fGameStateById;
  private Map<String, Long> fGameIdByName;
  private RosterCache fRosterCache;
  private TeamCache fTeamCache;  // used in standalone mode only
  
  private static final String _PITCHES_INI = "pitches.ini";
  private static final String _PITCH_PROPERTY_PREFIX = "pitch.";
  
  public GameCache(FantasyFootballServer pServer) {
    fServer = pServer;
    fIdGenerator = new IdGenerator(0);
    fGameStateById = new HashMap<Long, GameState>();
    fGameIdByName = new HashMap<String, Long>();
    fRosterCache = new RosterCache();
    fTeamCache = new TeamCache();
  }
  
  public void init() {
    DbGamesSerializedQueryMaxId queryMaxId = (DbGamesSerializedQueryMaxId) getServer().getDbQueryFactory().getStatement(DbStatementId.GAMES_SERIALIZED_QUERY_MAX_ID);
    fIdGenerator = new IdGenerator(queryMaxId.execute());
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
  
  public void add(GameState pGameState, GameCacheMode pMode) {
    if (pGameState == null) {
      return;
    }
    if (GameStatus.PAUSED == pGameState.getStatus()) {
      pGameState.setStatus(GameStatus.ACTIVE);
    }
    if (GameStatus.STARTING != pGameState.getStatus()) {
      UtilServerTimer.syncTime(pGameState);
      UtilServerTimer.startTurnTimer(pGameState);
    }
		fGameStateById.put(pGameState.getId(), pGameState);
    // log game cache size -->
    FantasyFootballServer server = pGameState.getServer();
    if (server.getDebugLog().isLogging(IServerLogLevel.WARN)) {
    	StringBuilder log = new StringBuilder();
    	log.append(pMode.getName()).append(" cache increases to ").append(fGameStateById.size()).append(" games.");
    	server.getDebugLog().log(IServerLogLevel.WARN, pGameState.getId(), log.toString());
    }
    // <-- log game cache size
  }
    
  public GameState getGameStateByName(String pGameName, boolean pLoadFromDb) {
    Long gameId = fGameIdByName.get(pGameName);
    return (gameId != null) ? getGameStateById(gameId) : null;
  }
  
  public void removeGameStateFromCache(GameState pGameState) {
    if (pGameState != null) {
    	GameState cachedGameState = fGameStateById.remove(pGameState.getId());
  		if (cachedGameState != null) {
        removeMappingForGameId(pGameState.getId());
        // log game cache size -->
        FantasyFootballServer server = pGameState.getServer();
        server.getDebugLog().log(IServerLogLevel.WARN, cachedGameState.getId(), StringTool.bind("REMOVE GAME cache decreases to $1 games.", fGameStateById.size()));
        // <-- log game cache size
        if (pGameState.getGame().getFinished() != null) {
          server.getCommunication().handleCommand(new InternalServerCommandBackupGame(pGameState.getId()));
          queueDbPlayerMarkersUpdate(pGameState);
        }
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
  	DbGameListQueryOpenGamesByCoach queryOpenGames = (DbGameListQueryOpenGamesByCoach) getServer().getDbQueryFactory().getStatement(DbStatementId.GAME_LIST_QUERY_OPEN_GAMES_BY_COACH);
  	queryOpenGames.execute(gameList, pCoach);
  	// old version:
  	// DbGameListQueryOpenGamesByCoachOld queryOpenGamesOld = (DbGameListQueryOpenGamesByCoachOld) getServer().getDbQueryFactory().getStatement(DbStatementId.GAME_LIST_QUERY_OPEN_GAMES_BY_COACH_OLD);
  	// queryOpenGamesOld.execute(gameList, pCoach);
  	return gameList;
  }
  
  public GameState createGameState(GameCacheMode pMode) {
    Game game = new Game();
    game.setId(fIdGenerator.generateId());
  	game.setTesting(GameCacheMode.START_TEST_GAME == pMode);
    game.setHomePlaying(true);
    game.setTurnMode(TurnMode.START_GAME);
    game.setDialogParameter(new DialogStartGameParameter());
    game.getFieldModel().setWeather(Weather.NICE);
    GameState gameState = new GameState(getServer());
    gameState.setGame(game);
    if (GameCacheMode.SCHEDULE_GAME == pMode) {
      gameState.setStatus(GameStatus.SCHEDULED);
	    gameState.getGame().setScheduled(new Date());
    } else {
      gameState.setStatus(GameStatus.STARTING);
    	add(gameState, pMode);
    }
    queueDbInsert(gameState);
    return gameState;
  }
  
  public void add(Roster pRoster) {
    fRosterCache.add(pRoster);
  }
  
  public Roster getRosterById(String pRosterId) {
    return fRosterCache.getRosterById(pRosterId);
  }
  
  public Team getTeamById(String pTeamId) {
    Team originalTeam = fTeamCache.getTeamById(pTeamId);
    if (originalTeam != null) {
	    // create a new team object by serializing & deserializing it
	    ByteList byteList = new ByteList();
	    originalTeam.addTo(byteList);
	    ByteArray byteArray = new ByteArray(byteList.toBytes());
	    Team team = new Team();
	    team.initFrom(byteArray);
	    team.setDivision(originalTeam.getDivision());  // non-serialized value
	    return team;
    } else {
    	return null;
    }
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
  
  private void queueDbInsert(GameState pGameState) {
    if (pGameState == null) {
      return;
    }
    DbTransaction transaction = new DbTransaction();
		transaction.add(new DbGamesInfoInsertParameter(pGameState));
		transaction.add(new DbGamesSerializedInsertParameter(pGameState));
	  getServer().getDbUpdater().add(transaction);
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
  
	public GameState queryFromDb(long pGameId) {
    if (pGameId <= 0) {
      return null;
    }
	  DbGamesSerializedQuery gameQuery = (DbGamesSerializedQuery) getServer().getDbQueryFactory().getStatement(DbStatementId.GAMES_SERIALIZED_QUERY);
	  GameState gameState = gameQuery.execute(getServer(), pGameId);
	  // the old way to do this:
    // gameState = DbQueryScript.readGameState(getServer(), pGameId);
	  return gameState;
	}

	public GameState closeGame(long pGameId) {
	  if (pGameId <= 0) {
      return null;
    }
		GameState gameState = getGameStateById(pGameId);
		if (gameState != null) {
      SessionManager sessionManager = getServer().getSessionManager();
      Session[] sessions = sessionManager.getSessionsForGameId(gameState.getId());
      for (Session session : sessions) {
    		getServer().getCommunication().close(session);
      }
      removeGameStateFromCache(gameState);
      if (getServer().getMode() == ServerMode.FUMBBL) {
      	getServer().getRequestProcessor().add(new FumbblRequestRemoveGamestate(gameState));
      }
		}
		return gameState;
  }
	
  private void queueDbPlayerMarkersUpdate(GameState pGameState) {
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
 