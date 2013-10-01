package com.balancedbytes.games.ffb.server.db.delete;

import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.db.DbStatementId;
import com.balancedbytes.games.ffb.server.db.DbUpdateStatement;

/**
 * 
 * @author Kalimar
 */
public class DbPlayerDeleteParameter extends DefaultDbUpdateParameter {
  
  private long fGameStateId;
  private String fPlayerId;
  
  public DbPlayerDeleteParameter(long pGameStateId, String pPlayerId) {
    fGameStateId = pGameStateId;
    fPlayerId = pPlayerId;
  }
 
  public long getGameStateId() {
    return fGameStateId;
  }  
  
  public String getPlayerId() {
    return fPlayerId;
  }
  
  public DbUpdateStatement getDbUpdateStatement(FantasyFootballServer pServer) {
    return (DbUpdateStatement) pServer.getDbUpdateFactory().getStatement(DbStatementId.PLAYER_DELETE);
  }
  
}
