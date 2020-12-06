package com.balancedbytes.games.ffb.server.db.query;

import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerLogLevel;
import com.balancedbytes.games.ffb.server.db.DbStatement;
import com.balancedbytes.games.ffb.server.db.DbStatementId;
import com.balancedbytes.games.ffb.server.db.IDbTableGamesSerialized;
import com.balancedbytes.games.ffb.util.StringTool;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class DbGamesSerializedQuery extends DbStatement {

	private PreparedStatement fStatement;

	public DbGamesSerializedQuery(FantasyFootballServer pServer) {
		super(pServer);
	}

	public DbStatementId getId() {
		return DbStatementId.GAMES_SERIALIZED_QUERY;
	}

	public void prepare(Connection pConnection) {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT ").append(IDbTableGamesSerialized.COLUMN_SERIALIZED);
			sql.append(" FROM ").append(IDbTableGamesSerialized.TABLE_NAME).append(" WHERE id=?");
			fStatement = pConnection.prepareStatement(sql.toString());
		} catch (SQLException sqlE) {
			throw new FantasyFootballException(sqlE);
		}
	}

	public GameState execute(FantasyFootballServer pServer, long pGameStateId) {
		GameState gameState = null;
		try {
			fStatement.setLong(1, pGameStateId);
			try (ResultSet resultSet = fStatement.executeQuery()) {
				while (resultSet.next()) {
					Blob blob = resultSet.getBlob(1);
					JsonValue jsonValue = UtilJson.gunzip(blob.getBytes(1, (int) blob.length()));
					gameState = new GameState(pServer).initFrom(jsonValue);
					if (getServer().getDebugLog().isLogging(IServerLogLevel.TRACE) && (gameState.getCurrentStep() != null)) {
						String currentStepName = (gameState.getCurrentStep() != null) ? gameState.getCurrentStep().getId().getName()
								: "null";
						getServer().getDebugLog().log(IServerLogLevel.TRACE,
								StringTool.bind("loaded CurrentStep $1", currentStepName));
					}
				}
			}
		} catch (IOException | SQLException pIOException) {
			throw new FantasyFootballException(pIOException);
		}
		return gameState;
	}

}
