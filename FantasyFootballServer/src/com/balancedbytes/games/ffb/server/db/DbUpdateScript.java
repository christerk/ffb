package com.balancedbytes.games.ffb.server.db;

import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.db.old.DbActingPlayersDeleteParameter;
import com.balancedbytes.games.ffb.server.db.old.DbDialogsDeleteParameter;
import com.balancedbytes.games.ffb.server.db.old.DbFieldModelsDeleteParameter;
import com.balancedbytes.games.ffb.server.db.old.DbGameLogsDeleteParameter;
import com.balancedbytes.games.ffb.server.db.old.DbGameOptionsDeleteParameter;
import com.balancedbytes.games.ffb.server.db.old.DbGameStatesDeleteParameter;
import com.balancedbytes.games.ffb.server.db.old.DbInducementsDeleteParameter;
import com.balancedbytes.games.ffb.server.db.old.DbPlayerIconsDeleteParameter;
import com.balancedbytes.games.ffb.server.db.old.DbPlayerInjuriesDeleteParameter;
import com.balancedbytes.games.ffb.server.db.old.DbPlayerResultsDeleteParameter;
import com.balancedbytes.games.ffb.server.db.old.DbPlayerSkillsDeleteParameter;
import com.balancedbytes.games.ffb.server.db.old.DbPlayersDeleteParameter;
import com.balancedbytes.games.ffb.server.db.old.DbStepStackDeleteParameter;
import com.balancedbytes.games.ffb.server.db.old.DbTeamResultsDeleteParameter;
import com.balancedbytes.games.ffb.server.db.old.DbTeamsDeleteParameter;
import com.balancedbytes.games.ffb.server.db.old.DbTurnDataDeleteParameter;

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
