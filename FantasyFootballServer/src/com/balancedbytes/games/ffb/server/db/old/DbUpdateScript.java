package com.balancedbytes.games.ffb.server.db.old;

import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.db.DbTransaction;

/**
 * 
 * @author Kalimar
 */
public class DbUpdateScript {

  public static DbTransaction createDeleteGameStateTransaction(FantasyFootballServer pServer, long pGameStateId) {
    DbTransaction transaction = new DbTransaction();
    transaction.add(new DbPlayerIconsDeleteParameter(pGameStateId));
    transaction.add(new DbPlayerSkillsDeleteParameter(pGameStateId));
    transaction.add(new DbPlayerInjuriesDeleteParameter(pGameStateId));
    transaction.add(new DbPlayerResultsDeleteParameter(pGameStateId));
    transaction.add(new DbPlayersDeleteParameter(pGameStateId));
    transaction.add(new DbTeamResultsDeleteParameter(pGameStateId));
    transaction.add(new DbTeamsDeleteParameter(pGameStateId));
    transaction.add(new DbGameLogsDeleteParameter(pGameStateId));
    transaction.add(new DbFieldModelsDeleteParameter(pGameStateId));
    transaction.add(new DbDialogsDeleteParameter(pGameStateId));
    transaction.add(new DbActingPlayersDeleteParameter(pGameStateId));
    transaction.add(new DbTurnDataDeleteParameter(pGameStateId));
    transaction.add(new DbInducementsDeleteParameter(pGameStateId));
    transaction.add(new DbGameOptionsDeleteParameter(pGameStateId));
    transaction.add(new DbStepStackDeleteParameter(pGameStateId));
    transaction.add(new DbGameStatesDeleteParameter(pGameStateId));
    return transaction;
  }
  
}
