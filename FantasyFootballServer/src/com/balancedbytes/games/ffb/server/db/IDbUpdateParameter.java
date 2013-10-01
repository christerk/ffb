package com.balancedbytes.games.ffb.server.db;

import java.sql.SQLException;

import com.balancedbytes.games.ffb.server.FantasyFootballServer;

/**
 * 
 * @author Kalimar
 */
public interface IDbUpdateParameter {

  public int getUpdatedRows();
  
  public void executeUpdate(FantasyFootballServer pServer) throws SQLException;
  
  public DbUpdateStatement getDbUpdateStatement(FantasyFootballServer pServer);
    
}
