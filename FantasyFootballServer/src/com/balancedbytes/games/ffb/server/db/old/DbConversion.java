package com.balancedbytes.games.ffb.server.db.old;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Roster;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.db.DbStatementId;
import com.balancedbytes.games.ffb.server.db.DbTransaction;
import com.balancedbytes.games.ffb.server.db.delete.DbGamesInfoDeleteParameter;
import com.balancedbytes.games.ffb.server.db.delete.DbGamesSerializedDeleteParameter;
import com.balancedbytes.games.ffb.server.db.insert.DbGamesInfoInsertParameter;
import com.balancedbytes.games.ffb.server.db.insert.DbGamesSerializedInsertParameter;
import com.balancedbytes.games.ffb.server.fumbbl.FumbblRequestProcessor;
import com.balancedbytes.games.ffb.util.StringTool;

/**
 * 
 * @author Georg Seipler
 */
public class DbConversion {

  private FantasyFootballServer fServer;
  private DbConversionFactory fDbConversionFactory;
  
  public DbConversion(FantasyFootballServer pServer) {
    fServer = pServer;
    fDbConversionFactory = new DbConversionFactory(pServer);
  }
  
  public void convert(long pStartGameId, long pEndGameId) throws SQLException {
    fDbConversionFactory.prepareStatements();
    long startTime = System.currentTimeMillis();
    SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    System.out.println("Conversion started at " + timestampFormat.format(new Date(startTime)));
    DbGameStatesQueryFinishedGamesOld finishedGamesQuery = (DbGameStatesQueryFinishedGamesOld) fDbConversionFactory.getStatement(DbStatementId.GAME_STATES_QUERY_FINISHED_GAMES);
    long[] finishedGameStateIds = finishedGamesQuery.execute(pStartGameId, pEndGameId);
    List<Long> unconvertedGames = new ArrayList<Long>();
    for (long gameStateId : finishedGameStateIds) {
      System.out.print("load game " + gameStateId + " from old db");
      GameState gameState = readGameState(gameStateId);
      if (gameState != null) {
        Game game = gameState.getGame();
        FumbblRequestProcessor fumbblRequestProcessor = new FumbblRequestProcessor(fServer);
        System.out.print(", load rosters from fumbbl");
        Roster homeRoster = fumbblRequestProcessor.loadRoster(game.getTeamHome().getId());
        Roster awayRoster = fumbblRequestProcessor.loadRoster(game.getTeamAway().getId());
        if ((homeRoster != null) && (awayRoster != null)) {
          game.getTeamHome().setRoster(homeRoster);
          game.getTeamAway().setRoster(awayRoster);
        } else {
          gameState = null;
        }
      }
      if (gameState != null) {
        System.out.print(" and save game to new db");
        try {
          saveToDb(gameState);
          System.out.println(".");
        } catch (Exception pAny) {
          fServer.getDebugLog().log(gameStateId, pAny);
          gameState = null;
        }
      }
      if (gameState == null) {
        System.out.println(" -> unable to convert!");
        unconvertedGames.add(gameStateId);
      }
    }
    long endTime = System.currentTimeMillis();
    System.out.println("Conversion finished at " + timestampFormat.format(new Date(endTime)));
    System.out.println((finishedGameStateIds.length - unconvertedGames.size()) + " games converted in " + StringTool.formatThousands(((endTime - startTime) / 1000)) + " seconds.");
    if (unconvertedGames.size() > 0) {
      System.out.println("Unable to convert " + unconvertedGames.size() + " games:");
      boolean firstElement = true;
      for (long gameStateId : unconvertedGames) {
        if (firstElement) {
          firstElement = false;
        } else {
          System.out.print(", ");
        }
        System.out.print(gameStateId);
      }
      System.out.println();
    }
    fDbConversionFactory.closeDbConnection();
  } 
  
  private GameState readGameState(long pGameStateId) {
    
    try {
    
      DbGameStatesQueryOld gameStatesQuery = (DbGameStatesQueryOld) fDbConversionFactory.getStatement(DbStatementId.GAME_STATES_QUERY);
      GameState gameState = gameStatesQuery.execute(fServer, pGameStateId);
      
      if (gameState == null) {
        return null;
      }
      
      DbTeamsForGameStateQueryOld teamsQuery = (DbTeamsForGameStateQueryOld) fDbConversionFactory.getStatement(DbStatementId.TEAMS_FOR_GAME_STATE_QUERY);
      teamsQuery.execute(gameState);
      
      DbPlayersForGameStateQueryOld playersQuery = (DbPlayersForGameStateQueryOld) fDbConversionFactory.getStatement(DbStatementId.PLAYERS_FOR_GAME_STATE_QUERY);
      playersQuery.execute(gameState);

      DbPlayerSkillsForGameStateQueryOld playerSkillsQuery = (DbPlayerSkillsForGameStateQueryOld) fDbConversionFactory.getStatement(DbStatementId.PLAYER_SKILLS_FOR_GAME_STATE_QUERY);
      playerSkillsQuery.execute(gameState);
      
      DbPlayerInjuriesForGameStateQueryOld playerInjuriesQuery = (DbPlayerInjuriesForGameStateQueryOld) fDbConversionFactory.getStatement(DbStatementId.PLAYER_INJURIES_FOR_GAME_STATE_QUERY);
      playerInjuriesQuery.execute(gameState);

      DbPlayerIconsForGameStateQueryOld playerIconsQuery = (DbPlayerIconsForGameStateQueryOld) fDbConversionFactory.getStatement(DbStatementId.PLAYER_ICONS_FOR_GAME_STATE_QUERY);
      playerIconsQuery.execute(gameState);

      DbTurnDataForGameStateQueryOld turnDataQuery = (DbTurnDataForGameStateQueryOld) fDbConversionFactory.getStatement(DbStatementId.TURN_DATA_FOR_GAME_STATE_QUERY);
      turnDataQuery.execute(gameState);
      
      DbActingPlayersForGameStateQueryOld actingPlayersQuery = (DbActingPlayersForGameStateQueryOld) fDbConversionFactory.getStatement(DbStatementId.ACTING_PLAYERS_FOR_GAME_STATE_QUERY);
      actingPlayersQuery.execute(gameState);
      
      DbDialogsForGameStateQueryOld dialogsQuery = (DbDialogsForGameStateQueryOld) fDbConversionFactory.getStatement(DbStatementId.DIALOGS_FOR_GAME_STATE_QUERY);
      dialogsQuery.execute(gameState);
      
      DbFieldModelsForGameStateQueryOld fieldModelsQuery = (DbFieldModelsForGameStateQueryOld) fDbConversionFactory.getStatement(DbStatementId.FIELD_MODELS_QUERY);
      fieldModelsQuery.execute(gameState);

      DbGameOptionsForGameStateQueryOld gameOptionsQuery = (DbGameOptionsForGameStateQueryOld) fDbConversionFactory.getStatement(DbStatementId.GAME_OPTIONS_FOR_GAME_STATE_QUERY);
      gameOptionsQuery.execute(gameState);

      DbTeamResultsForGameStateQueryOld teamResultsQuery = (DbTeamResultsForGameStateQueryOld) fDbConversionFactory.getStatement(DbStatementId.TEAM_RESULTS_FOR_GAME_STATE_QUERY);
      teamResultsQuery.execute(gameState);
      
      DbPlayerResultsForGameStateQueryOld playerResultsQuery = (DbPlayerResultsForGameStateQueryOld) fDbConversionFactory.getStatement(DbStatementId.PLAYER_RESULTS_FOR_GAME_STATE_QUERY);
      playerResultsQuery.execute(gameState);
      
      DbInducementsForGameStateQueryOld inducementsQuery = (DbInducementsForGameStateQueryOld) fDbConversionFactory.getStatement(DbStatementId.INDUCEMENTS_FOR_GAME_STATE_QUERY);
      inducementsQuery.execute(gameState);
      
      DbGameLogsForGameStateQueryOld gameLogsQuery = (DbGameLogsForGameStateQueryOld) fDbConversionFactory.getStatement(DbStatementId.GAME_LOGS_FOR_GAME_STATE_QUERY);
      gameLogsQuery.execute(gameState);
    
      return gameState;
  
    } catch (Exception pAny) {
      fServer.getDebugLog().log(pGameStateId, pAny);
      return null;
    }
    
  }
  
  public void saveToDb(GameState pGameState) {
    DbTransaction transaction = new DbTransaction();
    transaction.add(new DbGamesInfoDeleteParameter(pGameState.getId()));
    transaction.add(new DbGamesInfoInsertParameter(pGameState));
    transaction.add(new DbGamesSerializedDeleteParameter(pGameState.getId()));
    transaction.add(new DbGamesSerializedInsertParameter(pGameState));
    transaction.executeUpdate(fServer);
  }
  
}
