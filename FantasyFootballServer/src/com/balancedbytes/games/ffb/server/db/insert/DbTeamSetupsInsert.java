package com.balancedbytes.games.ffb.server.db.insert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.db.DbStatementId;
import com.balancedbytes.games.ffb.server.db.DbUpdateStatement;
import com.balancedbytes.games.ffb.server.db.IDbTableTeamSetups;
import com.balancedbytes.games.ffb.server.db.IDbUpdateParameter;

/**
 * 
 * @author Kalimar
 */
public class DbTeamSetupsInsert extends DbUpdateStatement {
  
  private PreparedStatement fStatement;
  
  public DbTeamSetupsInsert(FantasyFootballServer pServer) {
    super(pServer);    
  }
  
  public DbStatementId getId() {
    return DbStatementId.TEAM_SETUPS_INSERT;
  }

  public void prepare(Connection pConnection) {
    try {
      StringBuilder sql = new StringBuilder();
      sql.append("INSERT INTO ").append(IDbTableTeamSetups.TABLE_NAME).append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
      fStatement = pConnection.prepareStatement(sql.toString());
    } catch (SQLException sqlE) {
      throw new FantasyFootballException(sqlE);
    }
  }
  
  private PreparedStatement fillDbStatement(IDbUpdateParameter pUpdateParameter) throws SQLException {
    DbTeamSetupsInsertParameter parameter = (DbTeamSetupsInsertParameter) pUpdateParameter;
    fStatement.setString(1, parameter.getTeamId());
    fStatement.setString(2, parameter.getName());
    byte[] playerNumbers = parameter.getPlayerNumbers();
    byte[] xCoordinates = parameter.getXCoordinates();
    byte[] yCoordinates = parameter.getYCoordinates();
    for (int i = 0; i < Math.min(11, playerNumbers.length); i++) {
      fStatement.setByte(3 + (3 * i), playerNumbers[i]);
      fStatement.setByte(4 + (3 * i), xCoordinates[i]);
      fStatement.setByte(5 + (3 * i), yCoordinates[i]);
    }
    for (int i = playerNumbers.length; i < 11; i++) {
      fStatement.setNull(3 + (3 * i), Types.TINYINT);
      fStatement.setNull(4 + (3 * i), Types.TINYINT);
      fStatement.setNull(5 + (3 * i), Types.TINYINT);
    }
    return fStatement;
  }

	public int execute(IDbUpdateParameter pUpdateParameter) throws SQLException {
	  return fillDbStatement(pUpdateParameter).executeUpdate();
	}

	public String toString(IDbUpdateParameter pUpdateParameter) throws SQLException {
		return fillDbStatement(pUpdateParameter).toString();
	}
  
}
