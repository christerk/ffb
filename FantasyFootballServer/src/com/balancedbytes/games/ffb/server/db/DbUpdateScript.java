package com.balancedbytes.games.ffb.server.db;

import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.db.delete.DbActingPlayersDeleteParameter;
import com.balancedbytes.games.ffb.server.db.delete.DbDialogsDeleteParameter;
import com.balancedbytes.games.ffb.server.db.delete.DbFieldModelsDeleteParameter;
import com.balancedbytes.games.ffb.server.db.delete.DbGameLogsDeleteParameter;
import com.balancedbytes.games.ffb.server.db.delete.DbGameOptionsDeleteParameter;
import com.balancedbytes.games.ffb.server.db.delete.DbGameStatesDeleteParameter;
import com.balancedbytes.games.ffb.server.db.delete.DbInducementsDeleteParameter;
import com.balancedbytes.games.ffb.server.db.delete.DbPlayerIconsDeleteParameter;
import com.balancedbytes.games.ffb.server.db.delete.DbPlayerInjuriesDeleteParameter;
import com.balancedbytes.games.ffb.server.db.delete.DbPlayerResultsDeleteParameter;
import com.balancedbytes.games.ffb.server.db.delete.DbPlayerSkillsDeleteParameter;
import com.balancedbytes.games.ffb.server.db.delete.DbPlayersDeleteParameter;
import com.balancedbytes.games.ffb.server.db.delete.DbStepStackDeleteParameter;
import com.balancedbytes.games.ffb.server.db.delete.DbTeamResultsDeleteParameter;
import com.balancedbytes.games.ffb.server.db.delete.DbTeamsDeleteParameter;
import com.balancedbytes.games.ffb.server.db.delete.DbTurnDataDeleteParameter;

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
