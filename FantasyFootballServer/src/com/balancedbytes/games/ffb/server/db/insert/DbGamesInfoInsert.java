package com.balancedbytes.games.ffb.server.db.insert;

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
public class DbGamesInfoInsert extends DbUpdateStatement {
  
  private PreparedStatement fStatement;
  
  public DbGamesInfoInsert(FantasyFootballServer pServer) {
    super(pServer);
  }
  
  public DbStatementId getId() {
    return DbStatementId.GAMES_INFO_INSERT;
  }
  
  public void prepare(Connection pConnection) {
    try {
      StringBuilder sql = new StringBuilder();
      sql.append("INSERT INTO ").append(IDbTableGamesInfo.TABLE_NAME).append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
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
    DbGamesInfoInsertParameter parameter = (DbGamesInfoInsertParameter) pUpdateParameter;
    int col = 1;
    fStatement.setLong(col++, parameter.getId());              // 1
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
    return fStatement;
  }

}
