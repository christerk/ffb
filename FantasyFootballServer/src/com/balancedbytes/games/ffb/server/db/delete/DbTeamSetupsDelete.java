package com.balancedbytes.games.ffb.server.db.delete;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
public class DbTeamSetupsDelete extends DbUpdateStatement {
  
  private PreparedStatement fStatement;
  
  public DbTeamSetupsDelete(FantasyFootballServer pServer) {
    super(pServer);
  }
  
  public DbStatementId getId() {
    return DbStatementId.TEAM_SETUPS_DELETE;
  }

  public void prepare(Connection pConnection) {
    try {
      StringBuilder sql = new StringBuilder();
      sql.append("DELETE FROM ").append(IDbTableTeamSetups.TABLE_NAME).append(" WHERE (team_id = ? AND name = ?)");
      fStatement = pConnection.prepareStatement(sql.toString());
    } catch (SQLException sqlE) {
      throw new FantasyFootballException(sqlE);
    }
  }
  
  private PreparedStatement fillDbStatement(IDbUpdateParameter pDbUpdateParameter) throws SQLException {
    DbTeamSetupsDeleteParameter parameter = (DbTeamSetupsDeleteParameter) pDbUpdateParameter;
    fStatement.setString(1, parameter.getTeamId());
    fStatement.setString(2, parameter.getName());
    return fStatement;
  }

	public int execute(IDbUpdateParameter pUpdateParameter) throws SQLException {
	  return fillDbStatement(pUpdateParameter).executeUpdate();
	}

	public String toString(IDbUpdateParameter pUpdateParameter) throws SQLException {
		return fillDbStatement(pUpdateParameter).toString();
	}

}
