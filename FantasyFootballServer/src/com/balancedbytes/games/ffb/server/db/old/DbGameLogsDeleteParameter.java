package com.balancedbytes.games.ffb.server.db.old;

import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.db.DbStatementId;
import com.balancedbytes.games.ffb.server.db.DbUpdateStatement;
import com.balancedbytes.games.ffb.server.db.delete.DefaultDbUpdateParameter;

/**
 * 
 * @author Kalimar
 */
public class DbGameLogsDeleteParameter extends DefaultDbUpdateParameter {
  
  private long fGameStateId;
  private short fCommandNr;

  public DbGameLogsDeleteParameter(long pGameStateId) {
    this(pGameStateId, (short) -1);
  }

  public DbGameLogsDeleteParameter(long pGameStateId, short pCommandNr) {
    fGameStateId = pGameStateId;
    fCommandNr = pCommandNr;
  }

  public long getGameStateId() {
    return fGameStateId;
  }  
  
  public short getCommandNr() {
	  return fCommandNr;
  }
  
  public DbUpdateStatement getDbUpdateStatement(FantasyFootballServer pServer) {
    return (DbUpdateStatement) pServer.getDbUpdateFactory().getStatement(DbStatementId.GAME_LOGS_DELETE);
  }
  
}
