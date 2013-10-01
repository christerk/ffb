package com.balancedbytes.games.ffb.server.db.delete;

import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.db.DbStatementId;
import com.balancedbytes.games.ffb.server.db.DbUpdateStatement;

/**
 * 
 * @author Kalimar
 */
public class DbUserSettingsDeleteParameter extends DefaultDbUpdateParameter {
  
  private String fCoach;
  
  public DbUserSettingsDeleteParameter(String pCoach) {
    fCoach = pCoach;
  }
 
  public String getCoach() {
    return fCoach;
  }
  
  public DbUpdateStatement getDbUpdateStatement(FantasyFootballServer pServer) {
  	return (DbUpdateStatement) pServer.getDbUpdateFactory().getStatement(DbStatementId.USER_SETTINGS_DELETE);
  }
  
}
