package com.balancedbytes.games.ffb.server.db;

import java.sql.SQLException;

import com.balancedbytes.games.ffb.server.FantasyFootballServer;

/**
 * 
 * @author Kalimar
 */
public abstract class DefaultDbUpdateParameter implements IDbUpdateParameter {

  private int fUpdatedRows;
  
  public void executeUpdate(FantasyFootballServer pServer) throws SQLException {
    fUpdatedRows = getDbUpdateStatement(pServer).execute(this);
  }
  
  public int getUpdatedRows() {
    return fUpdatedRows;
  }
      
}
