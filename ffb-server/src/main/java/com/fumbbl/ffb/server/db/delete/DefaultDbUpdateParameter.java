package com.fumbbl.ffb.server.db.delete;

import java.sql.SQLException;

import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.db.IDbUpdateParameter;

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

	public void doAfterCommit(FantasyFootballServer pServer) {
		// implemented in subclasses if necessary
	}

	public void doAfterRollback(FantasyFootballServer pServer) {
		// implemented in subclasses if necessary
	}

}
