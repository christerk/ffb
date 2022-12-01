package com.fumbbl.ffb.server.db.update;

import com.fumbbl.ffb.FantasyFootballException;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.IServerLogLevel;
import com.fumbbl.ffb.server.db.DbStatementId;
import com.fumbbl.ffb.server.db.DbUpdateStatement;
import com.fumbbl.ffb.server.db.IDbTableGamesSerialized;
import com.fumbbl.ffb.server.db.IDbUpdateParameter;
import com.fumbbl.ffb.util.StringTool;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 
 * @author Kalimar
 */
public class DbGamesSerializedUpdate extends DbUpdateStatement {

	private PreparedStatement fStatement;

	public DbGamesSerializedUpdate(FantasyFootballServer pServer) {
		super(pServer);
	}

	public DbStatementId getId() {
		return DbStatementId.GAMES_SERIALIZED_UPDATE;
	}

	public void prepare(Connection pConnection) {
		try {
			String sql = "UPDATE " + IDbTableGamesSerialized.TABLE_NAME + " SET " +
				IDbTableGamesSerialized.COLUMN_SERIALIZED + "=?" + // 2
				" WHERE " + IDbTableGamesSerialized.COLUMN_ID + "=?"; // 1
			fStatement = pConnection.prepareStatement(sql);
		} catch (SQLException sqlE) {
			throw new FantasyFootballException(sqlE);
		}
	}

	public int execute(IDbUpdateParameter pUpdateParameter) throws SQLException {
		return fillDbStatement(pUpdateParameter, true).executeUpdate();
	}

	public String toString(IDbUpdateParameter pUpdateParameter) throws SQLException {
		return fillDbStatement(pUpdateParameter, false).toString();
	}

	private PreparedStatement fillDbStatement(IDbUpdateParameter pUpdateParameter, boolean pFillBlob)
			throws SQLException {
		DbGamesSerializedUpdateParameter parameter = (DbGamesSerializedUpdateParameter) pUpdateParameter;
		fStatement.clearParameters();
		int col = 1;
		try {
			byte[] blobData = pFillBlob ? parameter.gzip() : new byte[0];
			if (pFillBlob && getServer().getDebugLog().isLogging(IServerLogLevel.TRACE)) {
				int newLength = blobData.length;
				int oldLength = parameter.length();
				String logMsg = "updating compressed serialized game of " + StringTool.formatThousands(newLength) +
					" bytes" +
					" (" + Math.round((double) newLength * 100 / oldLength) + "%" +
					" of original " + StringTool.formatThousands(oldLength) + " bytes)";
				getServer().getDebugLog().log(IServerLogLevel.TRACE, parameter.getId(), logMsg);
			}
			fStatement.setBinaryStream(col++, new ByteArrayInputStream(blobData), blobData.length);
		} catch (IOException pIoException) {
			throw new SQLException("Error on compressing game", pIoException);
		}
		fStatement.setLong(col++, parameter.getId());
		return fStatement;
	}

}
