package com.balancedbytes.games.ffb.server.db;

/**
 * 
 * @author Kalimar
 */
public interface IDbStatementFactory {

	public DbStatement getStatement(DbStatementId pStatementId);

}
