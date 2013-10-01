package com.balancedbytes.games.ffb.server.db;

import java.sql.SQLException;

import com.balancedbytes.games.ffb.server.FantasyFootballServer;

/**
 * 
 * @author Kalimar
 */
public abstract class DbUpdateStatement extends DbStatement {

  public DbUpdateStatement(FantasyFootballServer pServer) {
    super(pServer);
  }
  
	public abstract int execute(IDbUpdateParameter pUpdateParameter) throws SQLException;

	public abstract String toString(IDbUpdateParameter pUpdateParameter) throws SQLException;
  
}
