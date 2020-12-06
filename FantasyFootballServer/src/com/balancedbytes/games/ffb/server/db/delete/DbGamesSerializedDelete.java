package com.balancedbytes.games.ffb.server.db.delete;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.db.DbStatementId;
import com.balancedbytes.games.ffb.server.db.DbUpdateStatement;
import com.balancedbytes.games.ffb.server.db.IDbTableGamesInfo;
import com.balancedbytes.games.ffb.server.db.IDbTableGamesSerialized;
import com.balancedbytes.games.ffb.server.db.IDbUpdateParameter;

/**
 * 
 * @author Kalimar
 */
public class DbGamesSerializedDelete extends DbUpdateStatement {

	private PreparedStatement fStatement;

	public DbGamesSerializedDelete(FantasyFootballServer pServer) {
		super(pServer);
	}

	public DbStatementId getId() {
		return DbStatementId.GAMES_SERIALIZED_DELETE;
	}

	public void prepare(Connection pConnection) {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("DELETE FROM ").append(IDbTableGamesSerialized.TABLE_NAME).append(" WHERE ")
					.append(IDbTableGamesInfo.COLUMN_ID).append("=?");
			fStatement = pConnection.prepareStatement(sql.toString());
		} catch (SQLException sqlE) {
			throw new FantasyFootballException(sqlE);
		}
	}

	private PreparedStatement fillDbStatement(IDbUpdateParameter pUpdateParameter) throws SQLException {
		DbGamesSerializedDeleteParameter parameter = (DbGamesSerializedDeleteParameter) pUpdateParameter;
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
