package com.fumbbl.ffb.server.db.insert;

import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.db.DbStatementId;
import com.fumbbl.ffb.server.db.DbUpdateStatement;
import com.fumbbl.ffb.server.db.DefaultDbUpdateParameter;

/**
 * 
 * @author Kalimar
 */
public class DbUserSettingsInsertParameter extends DefaultDbUpdateParameter {

	private String fCoach;
	private String fSettingName;
	private String fSettingValue;

	public DbUserSettingsInsertParameter(String pCoach, String pSettingName, String pSettingValue) {
		fCoach = pCoach;
		fSettingName = pSettingName;
		fSettingValue = pSettingValue;
	}

	public String getCoach() {
		return fCoach;
	}

	public String getSettingName() {
		return fSettingName;
	}

	public String getSettingValue() {
		return fSettingValue;
	}

	public DbUpdateStatement getDbUpdateStatement(FantasyFootballServer pServer) {
		return (DbUpdateStatement) pServer.getDbUpdateFactory().getStatement(DbStatementId.USER_SETTINGS_INSERT);
	}

}
