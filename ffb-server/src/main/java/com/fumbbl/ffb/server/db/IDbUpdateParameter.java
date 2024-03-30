package com.fumbbl.ffb.server.db;

import java.sql.SQLException;

import com.fumbbl.ffb.server.FantasyFootballServer;

/**
 * 
 * @author Kalimar
 */
public interface IDbUpdateParameter {

	public int getUpdatedRows();

	public void executeUpdate(FantasyFootballServer pServer) throws SQLException;

	public DbUpdateStatement getDbUpdateStatement(FantasyFootballServer pServer);

}
