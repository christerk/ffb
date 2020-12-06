package com.balancedbytes.games.ffb.server.db;

import java.sql.Connection;

import com.balancedbytes.games.ffb.server.FantasyFootballServer;

/**
 * 
 * @author Kalimar
 */
public abstract class DbStatement {

	private FantasyFootballServer fServer;

	public DbStatement(FantasyFootballServer pServer) {
		fServer = pServer;
	}

	public FantasyFootballServer getServer() {
		return fServer;
	}

	public abstract DbStatementId getId();

	public abstract void prepare(Connection pConnection);

}
