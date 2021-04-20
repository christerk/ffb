package com.fumbbl.ffb.server.db;

import java.sql.Connection;

import com.fumbbl.ffb.server.FantasyFootballServer;

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
