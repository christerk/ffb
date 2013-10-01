package com.balancedbytes.games.ffb.server.db;

import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.db.query.DbActingPlayersForGameStateQuery;
import com.balancedbytes.games.ffb.server.db.query.DbDialogsForGameStateQuery;
import com.balancedbytes.games.ffb.server.db.query.DbFieldModelsForGameStateQuery;
import com.balancedbytes.games.ffb.server.db.query.DbGameLogsForGameStateQuery;
import com.balancedbytes.games.ffb.server.db.query.DbGameOptionsForGameStateQuery;
import com.balancedbytes.games.ffb.server.db.query.DbGameStatesQuery;
import com.balancedbytes.games.ffb.server.db.query.DbInducementsForGameStateQuery;
import com.balancedbytes.games.ffb.server.db.query.DbPlayerIconsForGameStateQuery;
import com.balancedbytes.games.ffb.server.db.query.DbPlayerInjuriesForGameStateQuery;
import com.balancedbytes.games.ffb.server.db.query.DbPlayerResultsForGameStateQuery;
import com.balancedbytes.games.ffb.server.db.query.DbPlayerSkillsForGameStateQuery;
import com.balancedbytes.games.ffb.server.db.query.DbPlayersForGameStateQuery;
import com.balancedbytes.games.ffb.server.db.query.DbStepStackForGameStateQuery;
import com.balancedbytes.games.ffb.server.db.query.DbTeamResultsForGameStateQuery;
import com.balancedbytes.games.ffb.server.db.query.DbTeamsForGameStateQuery;
import com.balancedbytes.games.ffb.server.db.query.DbTurnDataForGameStateQuery;

/**
 * 
 * @author Kalimar
 */
public class DbQueryScript {

  public static GameState readGameState(FantasyFootballServer pServer, long pGameStateId) {
    
    IDbStatementFactory queryFactory = pServer.getDbQueryFactory();
    
    DbGameStatesQuery gameStatesQuery = (DbGameStatesQuery) queryFactory.getStatement(DbStatementId.GAME_STATES_QUERY);
    GameState gameState = gameStatesQuery.execute(pServer, pGameStateId);
    
    if (gameState != null) {
      
      DbGameLogsForGameStateQuery gameLogsQuery = (DbGameLogsForGameStateQuery) queryFactory.getStatement(DbStatementId.GAME_LOGS_FOR_GAME_STATE_QUERY);
      gameLogsQuery.execute(gameState);
      gameState.initCommandNrGenerator(gameState.getGameLog().getLastCommitedCommandNr());
      
      DbTeamsForGameStateQuery teamsQuery = (DbTeamsForGameStateQuery) queryFactory.getStatement(DbStatementId.TEAMS_FOR_GAME_STATE_QUERY);
      teamsQuery.execute(gameState);
      
      DbPlayersForGameStateQuery playersQuery = (DbPlayersForGameStateQuery) queryFactory.getStatement(DbStatementId.PLAYERS_FOR_GAME_STATE_QUERY);
      playersQuery.execute(gameState);

      DbPlayerSkillsForGameStateQuery playerSkillsQuery = (DbPlayerSkillsForGameStateQuery) queryFactory.getStatement(DbStatementId.PLAYER_SKILLS_FOR_GAME_STATE_QUERY);
      playerSkillsQuery.execute(gameState);
      
      DbPlayerInjuriesForGameStateQuery playerInjuriesQuery = (DbPlayerInjuriesForGameStateQuery) queryFactory.getStatement(DbStatementId.PLAYER_INJURIES_FOR_GAME_STATE_QUERY);
      playerInjuriesQuery.execute(gameState);

      DbPlayerIconsForGameStateQuery playerIconsQuery = (DbPlayerIconsForGameStateQuery) queryFactory.getStatement(DbStatementId.PLAYER_ICONS_FOR_GAME_STATE_QUERY);
      playerIconsQuery.execute(gameState);

      DbTurnDataForGameStateQuery turnDataQuery = (DbTurnDataForGameStateQuery) queryFactory.getStatement(DbStatementId.TURN_DATA_FOR_GAME_STATE_QUERY);
      turnDataQuery.execute(gameState);
      
      DbActingPlayersForGameStateQuery actingPlayersQuery = (DbActingPlayersForGameStateQuery) queryFactory.getStatement(DbStatementId.ACTING_PLAYERS_FOR_GAME_STATE_QUERY);
      actingPlayersQuery.execute(gameState);
      
      DbDialogsForGameStateQuery dialogsQuery = (DbDialogsForGameStateQuery) queryFactory.getStatement(DbStatementId.DIALOGS_FOR_GAME_STATE_QUERY);
      dialogsQuery.execute(gameState);
      
      DbFieldModelsForGameStateQuery fieldModelsQuery = (DbFieldModelsForGameStateQuery) queryFactory.getStatement(DbStatementId.FIELD_MODELS_QUERY);
      fieldModelsQuery.execute(gameState);

      DbGameOptionsForGameStateQuery gameOptionsQuery = (DbGameOptionsForGameStateQuery) queryFactory.getStatement(DbStatementId.GAME_OPTIONS_FOR_GAME_STATE_QUERY);
      gameOptionsQuery.execute(gameState);

      DbTeamResultsForGameStateQuery teamResultsQuery = (DbTeamResultsForGameStateQuery) queryFactory.getStatement(DbStatementId.TEAM_RESULTS_FOR_GAME_STATE_QUERY);
      teamResultsQuery.execute(gameState);
      
      DbPlayerResultsForGameStateQuery playerResultsQuery = (DbPlayerResultsForGameStateQuery) queryFactory.getStatement(DbStatementId.PLAYER_RESULTS_FOR_GAME_STATE_QUERY);
      playerResultsQuery.execute(gameState);
      
      DbInducementsForGameStateQuery inducementsQuery = (DbInducementsForGameStateQuery) queryFactory.getStatement(DbStatementId.INDUCEMENTS_FOR_GAME_STATE_QUERY);
      inducementsQuery.execute(gameState);
      
      DbStepStackForGameStateQuery stepStackQuery = (DbStepStackForGameStateQuery) queryFactory.getStatement(DbStatementId.STEP_STACK_FOR_GAME_STATE_QUERY);
      stepStackQuery.execute(gameState);
      
    }
    
    return gameState;

  }
  
}
