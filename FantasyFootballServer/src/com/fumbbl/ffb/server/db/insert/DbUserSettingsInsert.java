package com.fumbbl.ffb.server.db.insert;

import com.fumbbl.ffb.FantasyFootballException;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.db.DbStatementId;
import com.fumbbl.ffb.server.db.DbUpdateStatement;
import com.fumbbl.ffb.server.db.IDbTableUserSettings;
import com.fumbbl.ffb.server.db.IDbUpdateParameter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 
 * @author Kalimar
 */
public class DbUserSettingsInsert extends DbUpdateStatement {

	private PreparedStatement fStatement;

	public DbUserSettingsInsert(FantasyFootballServer pServer) {
		super(pServer);
	}

	public DbStatementId getId() {
		return DbStatementId.USER_SETTINGS_INSERT;
	}

	public void prepare(Connection pConnection) {
		try {
			fStatement = pConnection.prepareStatement("INSERT INTO " + IDbTableUserSettings.TABLE_NAME + " VALUES(?, ?, ?)");
		} catch (SQLException sqlE) {
			throw new FantasyFootballException(sqlE);
		}
	}

	private PreparedStatement fillDbStatement(IDbUpdateParameter pUpdateParameter) throws SQLException {
		DbUserSettingsInsertParameter parameter = (DbUserSettingsInsertParameter) pUpdateParameter;
		fStatement.clearParameters();
		int col = 1;
		fStatement.setString(col++, parameter.getCoach());
		fStatement.setString(col++, parameter.getSettingName().getKey());
		fStatement.setString(col++, parameter.getSettingValue());
		return fStatement;
	}

	public int execute(IDbUpdateParameter pUpdateParameter) throws SQLException {
		return fillDbStatement(pUpdateParameter).executeUpdate();
	}

	public String toString(IDbUpdateParameter pUpdateParameter) throws SQLException {
		return fillDbStatement(pUpdateParameter).toString();
	}

}
