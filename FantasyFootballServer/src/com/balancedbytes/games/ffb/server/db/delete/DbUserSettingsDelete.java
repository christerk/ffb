package com.balancedbytes.games.ffb.server.db.delete;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.db.DbStatementId;
import com.balancedbytes.games.ffb.server.db.DbUpdateStatement;
import com.balancedbytes.games.ffb.server.db.IDbTableUserSettings;
import com.balancedbytes.games.ffb.server.db.IDbUpdateParameter;

/**
 * 
 * @author Kalimar
 */
public class DbUserSettingsDelete extends DbUpdateStatement {

	private PreparedStatement fStatement;

	public DbUserSettingsDelete(FantasyFootballServer pServer) {
		super(pServer);
	}

	public DbStatementId getId() {
		return DbStatementId.USER_SETTINGS_DELETE;
	}

	public void prepare(Connection pConnection) {
		try {
			StringBuilder sqlInsert = new StringBuilder();
			sqlInsert.append("DELETE FROM ").append(IDbTableUserSettings.TABLE_NAME).append(" WHERE coach=?");
			fStatement = pConnection.prepareStatement(sqlInsert.toString());
		} catch (SQLException sqlE) {
			throw new FantasyFootballException(sqlE);
		}
	}

	private PreparedStatement fillDbStatement(IDbUpdateParameter pUpdateParameter) throws SQLException {
		DbUserSettingsDeleteParameter parameter = (DbUserSettingsDeleteParameter) pUpdateParameter;
		fStatement.clearParameters();
		fStatement.setString(1, parameter.getCoach());
		return fStatement;
	}

	public int execute(IDbUpdateParameter pUpdateParameter) throws SQLException {
		return fillDbStatement(pUpdateParameter).executeUpdate();
	}

	public String toString(IDbUpdateParameter pUpdateParameter) throws SQLException {
		return fillDbStatement(pUpdateParameter).toString();
	}

}
