package com.fumbbl.ffb.server.db.insert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.fumbbl.ffb.FantasyFootballException;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.db.DbStatementId;
import com.fumbbl.ffb.server.db.DbUpdateStatement;
import com.fumbbl.ffb.server.db.IDbTableUserSettings;
import com.fumbbl.ffb.server.db.IDbUpdateParameter;

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
			StringBuilder sqlInsert = new StringBuilder();
			sqlInsert.append("INSERT INTO ").append(IDbTableUserSettings.TABLE_NAME).append(" VALUES(?, ?, ?)");
			fStatement = pConnection.prepareStatement(sqlInsert.toString());
		} catch (SQLException sqlE) {
			throw new FantasyFootballException(sqlE);
		}
	}

	private PreparedStatement fillDbStatement(IDbUpdateParameter pUpdateParameter) throws SQLException {
		DbUserSettingsInsertParameter parameter = (DbUserSettingsInsertParameter) pUpdateParameter;
		fStatement.clearParameters();
		int col = 1;
		fStatement.setString(col++, parameter.getCoach());
		fStatement.setString(col++, parameter.getSettingName());
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
