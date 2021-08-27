package com.fumbbl.ffb.server.db.insert;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.fumbbl.ffb.FantasyFootballException;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.IServerLogLevel;
import com.fumbbl.ffb.server.db.DbStatementId;
import com.fumbbl.ffb.server.db.DbUpdateStatement;
import com.fumbbl.ffb.server.db.IDbTableGamesSerialized;
import com.fumbbl.ffb.server.db.IDbUpdateParameter;
import com.fumbbl.ffb.util.StringTool;

/**
 * 
 * @author Kalimar
 */
public class DbGamesSerializedInsert extends DbUpdateStatement {

	private PreparedStatement fStatement;

	public DbGamesSerializedInsert(FantasyFootballServer pServer) {
		super(pServer);
	}

	public DbStatementId getId() {
		return DbStatementId.GAMES_SERIALIZED_INSERT;
	}

	public void prepare(Connection pConnection) {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("INSERT INTO ").append(IDbTableGamesSerialized.TABLE_NAME).append(" VALUES(?,?)");
			fStatement = pConnection.prepareStatement(sql.toString());
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
		DbGamesSerializedInsertParameter parameter = (DbGamesSerializedInsertParameter) pUpdateParameter;
		int col = 1;
		fStatement.setLong(col++, parameter.getId());
		try {
			byte[] blobData = pFillBlob ? parameter.gzip() : new byte[0];
			if (pFillBlob && getServer().getDebugLog().isLogging(IServerLogLevel.TRACE)) {
				int newLength = blobData.length;
				int oldLength = parameter.length();
				StringBuilder logMsg = new StringBuilder();
				logMsg.append("inserting compressed serialized game of ").append(StringTool.formatThousands(newLength))
						.append(" bytes");
				logMsg.append(" (").append(Math.round((double) newLength * 100 / oldLength)).append("%");
				logMsg.append(" of original ").append(StringTool.formatThousands(oldLength)).append(" bytes)");
				getServer().getDebugLog().log(IServerLogLevel.TRACE, parameter.getId(), logMsg.toString());
			}
			fStatement.setBlob(col++, new ByteArrayInputStream(blobData), blobData.length);
		} catch (IOException pIoException) {
			throw new SQLException("Error on compressing game", pIoException);
		}
		return fStatement;
	}

}
