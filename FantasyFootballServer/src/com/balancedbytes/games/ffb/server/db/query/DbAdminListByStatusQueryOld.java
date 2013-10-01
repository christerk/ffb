package com.balancedbytes.games.ffb.server.db.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.GameStatus;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.admin.AdminList;
import com.balancedbytes.games.ffb.server.admin.AdminListEntry;
import com.balancedbytes.games.ffb.server.db.DbStatement;
import com.balancedbytes.games.ffb.server.db.DbStatementId;
import com.balancedbytes.games.ffb.server.db.IDbTableGameStates;
import com.balancedbytes.games.ffb.server.db.IDbTableTeams;

/**
 * 
 * @author Kalimar
 */
public class DbAdminListByStatusQueryOld extends DbStatement {
  
  private PreparedStatement fStatement;
  
  public DbAdminListByStatusQueryOld(FantasyFootballServer pServer) {
    super(pServer);
  }
  
  public DbStatementId getId() {
    return DbStatementId.ADMIN_LIST_BY_STATUS_QUERY_OLD;
  }
  
  public void prepare(Connection pConnection) {
    try {
      StringBuilder sql = new StringBuilder();
      sql.append("SELECT ");
        sql.append(IDbTableGameStates.TABLE_NAME).append(".").append(IDbTableGameStates.COLUMN_ID);
        sql.append(", ").append(IDbTableGameStates.TABLE_NAME).append(".").append(IDbTableGameStates.COLUMN_STARTED);
        sql.append(", ").append(IDbTableGameStates.TABLE_NAME).append(".").append(IDbTableGameStates.COLUMN_FINISHED);
        sql.append(", ").append(IDbTableGameStates.TABLE_NAME).append(".").append(IDbTableGameStates.COLUMN_STATUS);
        sql.append(", ").append(IDbTableTeams.TABLE_NAME).append(".").append(IDbTableTeams.COLUMN_HOME_TEAM);
        sql.append(", ").append(IDbTableTeams.TABLE_NAME).append(".").append(IDbTableTeams.COLUMN_ID);
        sql.append(", ").append(IDbTableTeams.TABLE_NAME).append(".").append(IDbTableTeams.COLUMN_NAME);
        sql.append(", ").append(IDbTableTeams.TABLE_NAME).append(".").append(IDbTableTeams.COLUMN_COACH);
      sql.append(" FROM ").append(IDbTableGameStates.TABLE_NAME);
      sql.append(" INNER JOIN ").append(IDbTableTeams.TABLE_NAME);
      sql.append(" ON ").append(IDbTableGameStates.TABLE_NAME).append(".").append(IDbTableGameStates.COLUMN_ID).append("=").append(IDbTableTeams.TABLE_NAME).append(".").append(IDbTableTeams.COLUMN_GAME_STATE_ID);
      sql.append(" WHERE ").append(IDbTableGameStates.TABLE_NAME).append(".").append(IDbTableGameStates.COLUMN_STATUS).append("=? AND ").append(IDbTableGameStates.TABLE_NAME).append(".").append(IDbTableGameStates.COLUMN_TESTING).append("=false");
      fStatement = pConnection.prepareStatement(sql.toString());
    } catch (SQLException sqlE) {
      throw new FantasyFootballException(sqlE);
    }
  }
  
  public void execute(AdminList pAdminList, GameStatus pStatus) {
    if ((pAdminList == null) || (pStatus == null)) {
    	return;
    }
    Map<Long, AdminListEntry> resultMap = new HashMap<Long, AdminListEntry>();
    try {
      fStatement.setString(1, pStatus.getTypeString());
      ResultSet resultSet = fStatement.executeQuery();
      while (resultSet.next()) {
        int col = 1;
        long gameId = resultSet.getLong(col++);
        AdminListEntry entry = resultMap.get(gameId);
        if (entry == null) {
          entry = new AdminListEntry();
          entry.setGameId(gameId);
          resultMap.put(gameId, entry);
        }
        Timestamp started = resultSet.getTimestamp(col++);
        if (started != null) {
          entry.setStarted(new Date(started.getTime()));
        }
        Timestamp finished = resultSet.getTimestamp(col++);
        if (finished != null) {
          entry.setFinished(new Date(finished.getTime()));
        }
        entry.setStatus(GameStatus.fromTypeString(resultSet.getString(col++)));
        boolean homeTeam = resultSet.getBoolean(col++);
        if (homeTeam) {
          entry.setTeamHomeId(resultSet.getString(col++));
          entry.setTeamHomeName(resultSet.getString(col++));
          entry.setTeamHomeCoach(resultSet.getString(col++));
        } else {
          entry.setTeamAwayId(resultSet.getString(col++));
          entry.setTeamAwayName(resultSet.getString(col++));
          entry.setTeamAwayCoach(resultSet.getString(col++));
        }
      }
      resultSet.close();
    } catch (SQLException pSqlE) {
      throw new FantasyFootballException(pSqlE);
    }
    for (AdminListEntry entry : resultMap.values()) {
    	pAdminList.add(entry);
    }
  }
    
}
