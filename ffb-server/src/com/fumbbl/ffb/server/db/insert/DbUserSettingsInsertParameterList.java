package com.fumbbl.ffb.server.db.insert;

import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.server.db.IDbUpdateParameterList;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Kalimar
 */
public class DbUserSettingsInsertParameterList implements IDbUpdateParameterList {

	private final List<DbUserSettingsInsertParameter> fParameters;

	public DbUserSettingsInsertParameterList() {
		fParameters = new ArrayList<>();
	}

	public DbUserSettingsInsertParameter[] getParameters() {
		return fParameters.toArray(new DbUserSettingsInsertParameter[fParameters.size()]);
	}

	public void addParameter(DbUserSettingsInsertParameter pParameter) {
		fParameters.add(pParameter);
	}

	public void addParameter(String pCoach, CommonProperty pSettingName, String pSettingValue) {
		addParameter(new DbUserSettingsInsertParameter(pCoach, pSettingName, pSettingValue));
	}

}
