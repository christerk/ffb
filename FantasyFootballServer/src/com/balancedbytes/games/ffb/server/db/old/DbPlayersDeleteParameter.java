package com.balancedbytes.games.ffb.server.db.old;

import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.db.DbStatementId;
import com.balancedbytes.games.ffb.server.db.DbUpdateStatement;
import com.balancedbytes.games.ffb.server.db.delete.DefaultDbUpdateParameter;

/**
 * 
 * @author Kalimar
 */
public class DbPlayersDeleteParameter extends DefaultDbUpdateParameter {
  
  private long fGameStateId;
  
  public DbPlayersDeleteParameter(long pGameStateId) {
    fGameStateId = pGameStateId;
  }
 
  public long getGameStateId() {
    return fGameStateId;
  }  
  
  public DbUpdateStatement getDbUpdateStatement(FantasyFootballServer pServer) {
    return (DbUpdateStatement) pServer.getDbUpdateFactory().getStatement(DbStatementId.PLAYERS_DELETE);
  }
  
}
