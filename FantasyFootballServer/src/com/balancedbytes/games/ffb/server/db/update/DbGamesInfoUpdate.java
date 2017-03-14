package com.balancedbytes.games.ffb.server.db.update;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.db.DbStatementId;
import com.balancedbytes.games.ffb.server.db.DbUpdateStatement;
import com.balancedbytes.games.ffb.server.db.IDbTableGamesInfo;
import com.balancedbytes.games.ffb.server.db.IDbUpdateParameter;

/**
 * 
 * @author Kalimar
 */
public class DbGamesInfoUpdate extends DbUpdateStatement {
  
  private PreparedStatement fStatement;
  
  public DbGamesInfoUpdate(FantasyFootballServer pServer) {
    super(pServer);
  }
  
  public DbStatementId getId() {
    return DbStatementId.GAMES_INFO_UPDATE;
  }
  
  public void prepare(Connection pConnection) {
    try {
      StringBuilder sql = new StringBuilder();
      sql.append("UPDATE ").append(IDbTableGamesInfo.TABLE_NAME).append(" SET ");
      sql.append(IDbTableGamesInfo.COLUMN_SCHEDULED).append("=?,");            // 2
      sql.append(IDbTableGamesInfo.COLUMN_STARTED).append("=?,");              // 3
      sql.append(IDbTableGamesInfo.COLUMN_FINISHED).append("=?,");             // 4
      sql.append(IDbTableGamesInfo.COLUMN_COACH_HOME).append("=?,");           // 5
      sql.append(IDbTableGamesInfo.COLUMN_TEAM_HOME_ID).append("=?,");         // 6
      sql.append(IDbTableGamesInfo.COLUMN_TEAM_HOME_NAME).append("=?,");       // 7
      sql.append(IDbTableGamesInfo.COLUMN_COACH_AWAY).append("=?,");           // 8
      sql.append(IDbTableGamesInfo.COLUMN_TEAM_AWAY_ID).append("=?,");         // 9
      sql.append(IDbTableGamesInfo.COLUMN_TEAM_AWAY_NAME).append("=?,");       // 10
      sql.append(IDbTableGamesInfo.COLUMN_HALF).append("=?,");                 // 11
      sql.append(IDbTableGamesInfo.COLUMN_TURN).append("=?,");                 // 12
      sql.append(IDbTableGamesInfo.COLUMN_HOME_PLAYING).append("=?,");         // 13
      sql.append(IDbTableGamesInfo.COLUMN_STATUS).append("=?,");               // 14
      sql.append(IDbTableGamesInfo.COLUMN_TESTING).append("=?,");              // 15
      sql.append(IDbTableGamesInfo.COLUMN_ADMIN_MODE).append("=?");            // 16
      sql.append(" WHERE ").append(IDbTableGamesInfo.COLUMN_ID).append("=?");  // 1
      fStatement = pConnection.prepareStatement(sql.toString());    
    } catch (SQLException sqlE) {
      throw new FantasyFootballException(sqlE);
    }
  }
  
	public int execute(IDbUpdateParameter pUpdateParameter) throws SQLException {
	  return fillDbStatement(pUpdateParameter).executeUpdate();
	}

	public String toString(IDbUpdateParameter pUpdateParameter) throws SQLException {
		return fillDbStatement(pUpdateParameter).toString();
	}
  
  private PreparedStatement fillDbStatement(IDbUpdateParameter pUpdateParameter) throws SQLException {
    DbGamesInfoUpdateParameter parameter = (DbGamesInfoUpdateParameter) pUpdateParameter;
    int col = 1;
    fStatement.clearParameters();
    fStatement.setTimestamp(col++, parameter.getScheduled());  // 2
    fStatement.setTimestamp(col++, parameter.getStarted());    // 3
    fStatement.setTimestamp(col++, parameter.getFinished());   // 4
    fStatement.setString(col++, parameter.getCoachHome());     // 5
    fStatement.setString(col++, parameter.getTeamHomeId());    // 6
    fStatement.setString(col++, parameter.getTeamHomeName());  // 7
    fStatement.setString(col++, parameter.getCoachAway());     // 8
    fStatement.setString(col++, parameter.getTeamAwayId());    // 9
    fStatement.setString(col++, parameter.getTeamAwayName());  // 10
    fStatement.setByte(col++, parameter.getHalf());            // 11
    fStatement.setByte(col++, parameter.getTurn());            // 12
    fStatement.setBoolean(col++, parameter.isHomePlaying());   // 13
    fStatement.setString(col++, parameter.getStatus());        // 14
    fStatement.setBoolean(col++, parameter.isTesting());       // 15
    fStatement.setBoolean(col++, parameter.isAdminMode());     // 16
    fStatement.setLong(col++, parameter.getId());              // 1
    return fStatement;
  }

}
