package com.balancedbytes.games.ffb.server.db.old;

import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.db.DbStatementId;
import com.balancedbytes.games.ffb.server.db.IDbStatementFactory;

/**
 * 
 * @author Kalimar
 */
public class DbQueryScript {

  public static GameState readGameState(FantasyFootballServer pServer, IDbStatementFactory pQueryFactory, long pGameStateId) {
    
    DbGameStatesQuery gameStatesQuery = (DbGameStatesQuery) pQueryFactory.getStatement(DbStatementId.GAME_STATES_QUERY);
    GameState gameState = gameStatesQuery.execute(pServer, pGameStateId);
    
    if (gameState != null) {
      
      DbGameLogsForGameStateQuery gameLogsQuery = (DbGameLogsForGameStateQuery) pQueryFactory.getStatement(DbStatementId.GAME_LOGS_FOR_GAME_STATE_QUERY);
      gameLogsQuery.execute(gameState);
      gameState.initCommandNrGenerator(gameState.getGameLog().getLastCommitedCommandNr());
      
      DbTeamsForGameStateQuery teamsQuery = (DbTeamsForGameStateQuery) pQueryFactory.getStatement(DbStatementId.TEAMS_FOR_GAME_STATE_QUERY);
      teamsQuery.execute(gameState);
      
      DbPlayersForGameStateQuery playersQuery = (DbPlayersForGameStateQuery) pQueryFactory.getStatement(DbStatementId.PLAYERS_FOR_GAME_STATE_QUERY);
      playersQuery.execute(gameState);

      DbPlayerSkillsForGameStateQuery playerSkillsQuery = (DbPlayerSkillsForGameStateQuery) pQueryFactory.getStatement(DbStatementId.PLAYER_SKILLS_FOR_GAME_STATE_QUERY);
      playerSkillsQuery.execute(gameState);
      
      DbPlayerInjuriesForGameStateQuery playerInjuriesQuery = (DbPlayerInjuriesForGameStateQuery) pQueryFactory.getStatement(DbStatementId.PLAYER_INJURIES_FOR_GAME_STATE_QUERY);
      playerInjuriesQuery.execute(gameState);

      DbPlayerIconsForGameStateQuery playerIconsQuery = (DbPlayerIconsForGameStateQuery) pQueryFactory.getStatement(DbStatementId.PLAYER_ICONS_FOR_GAME_STATE_QUERY);
      playerIconsQuery.execute(gameState);

      DbTurnDataForGameStateQuery turnDataQuery = (DbTurnDataForGameStateQuery) pQueryFactory.getStatement(DbStatementId.TURN_DATA_FOR_GAME_STATE_QUERY);
      turnDataQuery.execute(gameState);
      
      DbActingPlayersForGameStateQuery actingPlayersQuery = (DbActingPlayersForGameStateQuery) pQueryFactory.getStatement(DbStatementId.ACTING_PLAYERS_FOR_GAME_STATE_QUERY);
      actingPlayersQuery.execute(gameState);
      
      DbDialogsForGameStateQuery dialogsQuery = (DbDialogsForGameStateQuery) pQueryFactory.getStatement(DbStatementId.DIALOGS_FOR_GAME_STATE_QUERY);
      dialogsQuery.execute(gameState);
      
      DbFieldModelsForGameStateQuery fieldModelsQuery = (DbFieldModelsForGameStateQuery) pQueryFactory.getStatement(DbStatementId.FIELD_MODELS_QUERY);
      fieldModelsQuery.execute(gameState);

      DbGameOptionsForGameStateQuery gameOptionsQuery = (DbGameOptionsForGameStateQuery) pQueryFactory.getStatement(DbStatementId.GAME_OPTIONS_FOR_GAME_STATE_QUERY);
      gameOptionsQuery.execute(gameState);

      DbTeamResultsForGameStateQuery teamResultsQuery = (DbTeamResultsForGameStateQuery) pQueryFactory.getStatement(DbStatementId.TEAM_RESULTS_FOR_GAME_STATE_QUERY);
      teamResultsQuery.execute(gameState);
      
      DbPlayerResultsForGameStateQuery playerResultsQuery = (DbPlayerResultsForGameStateQuery) pQueryFactory.getStatement(DbStatementId.PLAYER_RESULTS_FOR_GAME_STATE_QUERY);
      playerResultsQuery.execute(gameState);
      
      DbInducementsForGameStateQuery inducementsQuery = (DbInducementsForGameStateQuery) pQueryFactory.getStatement(DbStatementId.INDUCEMENTS_FOR_GAME_STATE_QUERY);
      inducementsQuery.execute(gameState);
      
      DbStepStackForGameStateQuery stepStackQuery = (DbStepStackForGameStateQuery) pQueryFactory.getStatement(DbStatementId.STEP_STACK_FOR_GAME_STATE_QUERY);
      stepStackQuery.execute(gameState);
      
    }
    
    return gameState;

  }
  
}
