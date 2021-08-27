package com.fumbbl.ffb.server.db.delete;

import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.db.DbStatementId;
import com.fumbbl.ffb.server.db.DbUpdateStatement;

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
