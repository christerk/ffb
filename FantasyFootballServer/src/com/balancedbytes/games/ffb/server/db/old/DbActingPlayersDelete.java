package com.balancedbytes.games.ffb.server.db.old;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.db.DbStatementId;
import com.balancedbytes.games.ffb.server.db.DbUpdateStatement;
import com.balancedbytes.games.ffb.server.db.IDbUpdateParameter;

/**
 * 
 * @author Kalimar
 */
public class DbActingPlayersDelete extends DbUpdateStatement {
  
  private PreparedStatement fStatement;
  
  public DbActingPlayersDelete(FantasyFootballServer pServer) {
    super(pServer);
  }
  
  public DbStatementId getId() {
    return DbStatementId.ACTING_PLAYERS_DELETE;
  }
  
  public void prepare(Connection pConnection) {
    try {
      StringBuilder sql = new StringBuilder();
      sql.append("DELETE FROM ").append(IDbTableActingPlayers.TABLE_NAME).append(" WHERE ").append(IDbTableActingPlayers.COLUMN_GAME_STATE_ID).append("=?");
      fStatement = pConnection.prepareStatement(sql.toString());      
    } catch (SQLException sqlE) {
      throw new FantasyFootballException(sqlE);
    }
  }
  
	private PreparedStatement fillDbStatement(IDbUpdateParameter pUpdateParameter) throws SQLException {
    DbActingPlayersDeleteParameter parameter = (DbActingPlayersDeleteParameter) pUpdateParameter;
    fStatement.clearParameters();
    fStatement.setLong(1, parameter.getGameStateId());
    return fStatement;
  }

	public int execute(IDbUpdateParameter pUpdateParameter) throws SQLException {
	  return fillDbStatement(pUpdateParameter).executeUpdate();
	}

	public String toString(IDbUpdateParameter pUpdateParameter) throws SQLException {
		return fillDbStatement(pUpdateParameter).toString();
	}
  
}
