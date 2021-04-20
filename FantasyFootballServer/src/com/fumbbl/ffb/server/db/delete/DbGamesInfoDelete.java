package com.fumbbl.ffb.server.db.delete;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.fumbbl.ffb.FantasyFootballException;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.db.DbStatementId;
import com.fumbbl.ffb.server.db.DbUpdateStatement;
import com.fumbbl.ffb.server.db.IDbTableGamesInfo;
import com.fumbbl.ffb.server.db.IDbUpdateParameter;

/**
 * 
 * @author Kalimar
 */
public class DbGamesInfoDelete extends DbUpdateStatement {

	private PreparedStatement fStatement;

	public DbGamesInfoDelete(FantasyFootballServer pServer) {
		super(pServer);
	}

	public DbStatementId getId() {
		return DbStatementId.GAMES_INFO_DELETE;
	}

	public void prepare(Connection pConnection) {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("DELETE FROM ").append(IDbTableGamesInfo.TABLE_NAME).append(" WHERE ")
					.append(IDbTableGamesInfo.COLUMN_ID).append("=?");
			fStatement = pConnection.prepareStatement(sql.toString());
		} catch (SQLException sqlE) {
			throw new FantasyFootballException(sqlE);
		}
	}

	private PreparedStatement fillDbStatement(IDbUpdateParameter pUpdateParameter) throws SQLException {
		DbGamesInfoDeleteParameter parameter = (DbGamesInfoDeleteParameter) pUpdateParameter;
		fStatement.clearParameters();
		fStatement.setLong(1, parameter.getGameStateId());
		return fStatement;
	}

	public int execute(IDbUpdateParameter pUpdateParameter) throws SQLException {
		return fillDbStatement(pUpdateParameter).executeUpdate();
	}

	public String toString(IDbUpdateParameter pUpdateParameter) throws SQLException {
		return fillDbStatement(pUpdateParameter).toString();
	}

}
