package com.balancedbytes.games.ffb.server.db.delete;

import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.db.DbStatementId;
import com.balancedbytes.games.ffb.server.db.DbUpdateStatement;

/**
 * 
 * @author Kalimar
 */
public class DbTeamResultsDeleteParameter extends DefaultDbUpdateParameter {
  
  private long fGameStateId;
  
  public DbTeamResultsDeleteParameter(long pGameStateId) {
    fGameStateId = pGameStateId;
  }
 
  public long getGameStateId() {
    return fGameStateId;
  }  
  
  public DbUpdateStatement getDbUpdateStatement(FantasyFootballServer pServer) {
    return (DbUpdateStatement) pServer.getDbUpdateFactory().getStatement(DbStatementId.TEAM_RESULTS_DELETE);
  }
  
}
