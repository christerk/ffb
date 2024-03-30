package com.fumbbl.ffb.server.db.query;

import com.fumbbl.ffb.FantasyFootballException;
import com.fumbbl.ffb.factory.GameStatusFactory;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.admin.AdminList;
import com.fumbbl.ffb.server.admin.AdminListEntry;
import com.fumbbl.ffb.server.db.DbStatement;
import com.fumbbl.ffb.server.db.DbStatementId;
import com.fumbbl.ffb.server.db.IDbTableGamesInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

public class DbTestGameListQuery extends DbStatement {

	private PreparedStatement fStatement;

	public DbTestGameListQuery(FantasyFootballServer pServer) {
		super(pServer);
	}

	public DbStatementId getId() {
		return DbStatementId.TEST_GAME_LIST_QUERY;
	}

	public void prepare(Connection pConnection) {
		try {
			//noinspection StringBufferReplaceableByString
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT ").append(IDbTableGamesInfo.COLUMN_ID).append(",").append(IDbTableGamesInfo.COLUMN_STARTED)
				.append(",").append(IDbTableGamesInfo.COLUMN_FINISHED).append(",")
				.append(IDbTableGamesInfo.COLUMN_LAST_UPDATED).append(",").append(IDbTableGamesInfo.COLUMN_COACH_HOME)
				.append(",").append(IDbTableGamesInfo.COLUMN_TEAM_HOME_ID).append(",")
				.append(IDbTableGamesInfo.COLUMN_TEAM_HOME_NAME).append(",").append(IDbTableGamesInfo.COLUMN_COACH_AWAY)
				.append(",").append(IDbTableGamesInfo.COLUMN_TEAM_AWAY_ID).append(",")
				.append(IDbTableGamesInfo.COLUMN_TEAM_AWAY_NAME).append(",").append(IDbTableGamesInfo.COLUMN_HALF).append(",")
				.append(IDbTableGamesInfo.COLUMN_TURN).append(",").append(IDbTableGamesInfo.COLUMN_STATUS)
				.append(",").append(IDbTableGamesInfo.COLUMN_TESTING)
				.append(" FROM ").append(IDbTableGamesInfo.TABLE_NAME)
				.append(" WHERE ").append(IDbTableGamesInfo.COLUMN_TESTING).append("=true")
				.append(" ORDER BY ").append(IDbTableGamesInfo.COLUMN_STARTED).append(" ASC ")
				.append(" LIMIT ?");
			fStatement = pConnection.prepareStatement(sql.toString());
		} catch (SQLException sqlE) {
			throw new FantasyFootballException(sqlE);
		}
	}

	public void execute(AdminList pAdminList, long limit) {
		if (limit < 1) {
			return;
		}
		try {
			fStatement.setLong(1, limit);
			try (ResultSet resultSet = fStatement.executeQuery()) {
				while (resultSet.next()) {
					int col = 1;
					AdminListEntry entry = new AdminListEntry();
					entry.setGameId(resultSet.getLong(col++));
					Timestamp started = resultSet.getTimestamp(col++);
					if (started != null) {
						entry.setStarted(new Date(started.getTime()));
					}
					Timestamp finished = resultSet.getTimestamp(col++);
					if (finished != null) {
						entry.setFinished(new Date(finished.getTime()));
					}
					Timestamp lastUpdated = resultSet.getTimestamp(col++);
					if (lastUpdated != null) {
						entry.setLastUpdated(new Date(lastUpdated.getTime()));
					}
					entry.setTeamHomeCoach(resultSet.getString(col++));
					entry.setTeamHomeId(resultSet.getString(col++));
					entry.setTeamHomeName(resultSet.getString(col++));
					entry.setTeamAwayCoach(resultSet.getString(col++));
					entry.setTeamAwayId(resultSet.getString(col++));
					entry.setTeamAwayName(resultSet.getString(col++));
					entry.setHalf(resultSet.getInt(col++));
					entry.setTurn(resultSet.getInt(col++));
					entry.setStatus(new GameStatusFactory().forTypeString(resultSet.getString(col++)));
					entry.setTestMode(resultSet.getBoolean(col++));
					pAdminList.add(entry);
				}
			}
		} catch (SQLException pSqlE) {
			throw new FantasyFootballException(pSqlE);
		}
	}

}
