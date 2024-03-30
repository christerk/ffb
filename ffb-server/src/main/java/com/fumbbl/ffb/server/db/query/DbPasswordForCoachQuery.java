package com.fumbbl.ffb.server.db.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.fumbbl.ffb.FantasyFootballException;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.db.DbStatement;
import com.fumbbl.ffb.server.db.DbStatementId;
import com.fumbbl.ffb.server.db.IDbTableCoaches;

/**
 * 
 * @author Kalimar
 */
public class DbPasswordForCoachQuery extends DbStatement {

	private PreparedStatement fStatement;

	public DbPasswordForCoachQuery(FantasyFootballServer pServer) {
		super(pServer);
	}

	public DbStatementId getId() {
		return DbStatementId.PASSWORD_FOR_COACH_QUERY;
	}

	public void prepare(Connection pConnection) {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT password FROM ").append(IDbTableCoaches.TABLE_NAME).append(" WHERE name = ?");
			fStatement = pConnection.prepareStatement(sql.toString());
		} catch (SQLException sqlE) {
			throw new FantasyFootballException(sqlE);
		}
	}

	public String execute(String pCoach) {
		String password = null;
		try {
			fStatement.setString(1, pCoach);
			try (ResultSet resultSet = fStatement.executeQuery()) {
				while (resultSet.next()) {
					password = resultSet.getString(1);
				}
			}
		} catch (SQLException sqlE) {
			throw new FantasyFootballException(sqlE);
		}
		return password;
	}

}
