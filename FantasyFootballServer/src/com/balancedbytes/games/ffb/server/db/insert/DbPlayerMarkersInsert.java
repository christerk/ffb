package com.balancedbytes.games.ffb.server.db.insert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.db.DbStatementId;
import com.balancedbytes.games.ffb.server.db.DbUpdateStatement;
import com.balancedbytes.games.ffb.server.db.IDbTablePlayerMarkers;
import com.balancedbytes.games.ffb.server.db.IDbUpdateParameter;

/**
 * 
 * @author Kalimar
 */
public class DbPlayerMarkersInsert extends DbUpdateStatement {
  
  private PreparedStatement fStatement;
  
  public DbPlayerMarkersInsert(FantasyFootballServer pServer) {
    super(pServer);
  }
  
  public DbStatementId getId() {
    return DbStatementId.PLAYER_MARKERS_INSERT;
  }
  
  public void prepare(Connection pConnection) {
    try {
      StringBuilder sqlInsert = new StringBuilder();
      sqlInsert.append("INSERT INTO ").append(IDbTablePlayerMarkers.TABLE_NAME).append(" VALUES(?, ?, ?)");
      fStatement = pConnection.prepareStatement(sqlInsert.toString());      
    } catch (SQLException sqlE) {
      throw new FantasyFootballException(sqlE);
    }
  }
  
  private PreparedStatement fillDbStatement(IDbUpdateParameter pUpdateParameter) throws SQLException {
    DbPlayerMarkersInsertParameter parameter = (DbPlayerMarkersInsertParameter) pUpdateParameter;
    fStatement.clearParameters();
    int col = 1;
    fStatement.setString(col++, parameter.getTeamId());
    fStatement.setString(col++, parameter.getPlayerId());
    fStatement.setString(col++, parameter.getText());
    return fStatement;
  }

	public int execute(IDbUpdateParameter pUpdateParameter) throws SQLException {
	  return fillDbStatement(pUpdateParameter).executeUpdate();
	}

	public String toString(IDbUpdateParameter pUpdateParameter) throws SQLException {
		return fillDbStatement(pUpdateParameter).toString();
	}
  
}
