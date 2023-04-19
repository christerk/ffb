package com.fumbbl.ffb.server.db.query;

import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.FantasyFootballException;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.db.DbStatement;
import com.fumbbl.ffb.server.db.DbStatementId;
import com.fumbbl.ffb.server.db.IDbTableUserSettings;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Kalimar
 */
public class DbUserSettingsQuery extends DbStatement {

	private class QueryResult {

		private String fSettingName;
		private String fSettingValue;

		public QueryResult(ResultSet pResultSet) throws SQLException {
			if (pResultSet != null) {
				int col = 1;
				pResultSet.getString(col++); // coach
				fSettingName = pResultSet.getString(col++);
				fSettingValue = pResultSet.getString(col++);
			}
		}

		public String getSettingName() {
			return fSettingName;
		}

		public String getSettingValue() {
			return fSettingValue;
		}

	}

	private PreparedStatement fStatement;

	private String fCoach;

	private final Map<String, String> fSettings;

	public DbUserSettingsQuery(FantasyFootballServer pServer) {
		super(pServer);
		fSettings = new HashMap<>();
	}

	public DbStatementId getId() {
		return DbStatementId.USER_SETTINGS_QUERY;
	}

	public void prepare(Connection pConnection) {
		try {
			fStatement = pConnection.prepareStatement("SELECT * FROM " + IDbTableUserSettings.TABLE_NAME + " WHERE coach=? ORDER BY name");
		} catch (SQLException sqlE) {
			throw new FantasyFootballException(sqlE);
		}
	}

	public void execute(String pCoach) {
		fCoach = pCoach;
		fSettings.clear();
		try {
			fStatement.setString(1, pCoach);
			try (ResultSet resultSet = fStatement.executeQuery()) {
				while (resultSet.next()) {
					QueryResult queryResult = new QueryResult(resultSet);
					fSettings.put(queryResult.getSettingName(), queryResult.getSettingValue());
				}
			}
		} catch (SQLException sqlE) {
			throw new FantasyFootballException(sqlE);
		}
	}

	public String getCoach() {
		return fCoach;
	}

	public String[] getSettingNames() {
		String[] names = fSettings.keySet().toArray(new String[fSettings.size()]);
		Arrays.sort(names);
		return names;
	}

	public String getSettingValue(CommonProperty setting) {
		return getSettingValue(setting.getKey());
	}

	public String getSettingValue(String pSettingName) {
		return fSettings.get(pSettingName);
	}

	public String[] getSettingValues() {
		String[] names = getSettingNames();
		String[] values = new String[names.length];
		for (int i = 0; i < names.length; i++) {
			values[i] = getSettingValue(names[i]);
		}
		return values;
	}

}
