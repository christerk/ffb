package com.balancedbytes.games.ffb.server;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
import com.balancedbytes.games.ffb.server.db.insert.DbGamesInfoInsertParameter;
import com.balancedbytes.games.ffb.server.db.insert.DbGamesSerializedInsertParameter;
import com.balancedbytes.games.ffb.server.db.old.DbGameListQueryOpenGamesByCoachOld;
import com.balancedbytes.games.ffb.server.db.old.DbQueryScript;
import com.balancedbytes.games.ffb.server.db.query.DbGameListQueryOpenGamesByCoach;
import com.balancedbytes.games.ffb.server.db.query.DbGamesSerializedQuery;
import com.balancedbytes.games.ffb.server.db.query.DbGamesSerializedQueryMaxId;
import com.balancedbytes.games.ffb.server.db.update.DbGamesInfoUpdateParameter;
import com.balancedbytes.games.ffb.server.db.update.DbGamesSerializedUpdateParameter;
import com.balancedbytes.games.ffb.server.fumbbl.FumbblRequestRemoveGamestate;
import com.balancedbytes.games.ffb.server.net.SessionManager;
import com.balancedbytes.games.ffb.server.util.UtilTimer;
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
    if (pGameState != null) {
      if (GameStatus.PAUSED == pGameState.getStatus()) {
        pGameState.setStatus(GameStatus.ACTIVE);
      }
      if (GameStatus.STARTING != pGameState.getStatus()) {
        UtilTimer.syncTime(pGameState);
        UtilTimer.startTurnTimer(pGameState);
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
  	// add open games from the old version (will be removed later)
  	DbGameListQueryOpenGamesByCoachOld queryOpenGamesOld = (DbGameListQueryOpenGamesByCoachOld) getServer().getDbQueryFactory().getStatement(DbStatementId.GAME_LIST_QUERY_OPEN_GAMES_BY_COACH_OLD);
  	queryOpenGamesOld.execute(gameList, pCoach);
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
  
  public void clearRosterCache() {
    fRosterCache.clear();
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
    queueDbUpdate(pGameState);
  }
    
  public Team[] getTeamsForCoach(String pCoach) {
    return fTeamCache.getTeamsForCoach(pCoach);
  }
  
  private void queueDbInsert(GameState pGameState) {
    DbTransaction transaction = new DbTransaction();
		transaction.add(new DbGamesInfoInsertParameter(pGameState));
		transaction.add(new DbGamesSerializedInsertParameter(pGameState));
	  getServer().getDbUpdater().add(transaction);
  }
  
	public void queueDbUpdate(GameState pGameState) {
	  DbTransaction transaction = new DbTransaction();
	  transaction.add(new DbGamesInfoUpdateParameter(pGameState));
	  transaction.add(new DbGamesSerializedUpdateParameter(pGameState));
	  getServer().getDbUpdater().add(transaction);
	}

	public void queueDbDelete(long pGameStateId) {
    DbTransaction deleteTransaction = new DbTransaction();
    deleteTransaction.add(new DbGamesInfoDeleteParameter(pGameStateId));
    deleteTransaction.add(new DbGamesSerializedDeleteParameter(pGameStateId));
    getServer().getDbUpdater().add(deleteTransaction);
	}	
  
	public GameState queryFromDb(long pGameStateId) {
	  DbGamesSerializedQuery gameQuery = (DbGamesSerializedQuery) getServer().getDbQueryFactory().getStatement(DbStatementId.GAMES_SERIALIZED_QUERY);
	  GameState gameState = gameQuery.execute(getServer(), pGameStateId);
	  // if no new version is found, try to load an old version
	  if (gameState == null) {
	  	gameState = DbQueryScript.readGameState(getServer(), pGameStateId);
	  }
	  return gameState;
	}

	public GameState closeGame(long pGameId) {
		GameState gameState = getGameStateById(pGameId);
		if (gameState != null) {
      SessionManager sessionManager = getServer().getSessionManager();
      Session[] sessions = sessionManager.getSessionsForGameId(gameState.getId());
      for (Session session : sessions) {
    		getServer().getCommunication().close(session);
      }
      removeGameStateFromCache(gameState);
      if (getServer().getMode() == ServerMode.FUMBBL) {
      	getServer().getFumbblRequestProcessor().add(new FumbblRequestRemoveGamestate(gameState));
      }
		}
		return gameState;
  }

}
 