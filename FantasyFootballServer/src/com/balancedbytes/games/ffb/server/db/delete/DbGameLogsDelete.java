package com.balancedbytes.games.ffb.server.db.delete;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.db.DbStatementId;
import com.balancedbytes.games.ffb.server.db.DbUpdateStatement;
import com.balancedbytes.games.ffb.server.db.IDbTableGameLogs;
import com.balancedbytes.games.ffb.server.db.IDbUpdateParameter;

/**
 * 
 * @author Kalimar
 */
public class DbGameLogsDelete extends DbUpdateStatement {
  
  private PreparedStatement fStatementDeleteAll;
  private PreparedStatement fStatementDeleteCommand;
  
  public DbGameLogsDelete(FantasyFootballServer pServer) {
    super(pServer);
  }
  
  public DbStatementId getId() {
    return DbStatementId.GAME_LOGS_DELETE;
  }
  
  public void prepare(Connection pConnection) {
    try {
      StringBuilder sqlDeleteAll = new StringBuilder();
      sqlDeleteAll.append("DELETE FROM ").append(IDbTableGameLogs.TABLE_NAME).append(" WHERE ").append(IDbTableGameLogs.COLUMN_GAME_STATE_ID).append("=?");
      fStatementDeleteAll = pConnection.prepareStatement(sqlDeleteAll.toString());      
      StringBuilder sqlDeleteCommand = new StringBuilder();
      sqlDeleteCommand.append("DELETE FROM ").append(IDbTableGameLogs.TABLE_NAME).append(" WHERE ").append(IDbTableGameLogs.COLUMN_GAME_STATE_ID).append("=? AND ").append(IDbTableGameLogs.COLUMN_COMMAND_NR).append("=?");
      fStatementDeleteCommand = pConnection.prepareStatement(sqlDeleteCommand.toString());      
    } catch (SQLException sqlE) {
      throw new FantasyFootballException(sqlE);
    }
  }
  
  private PreparedStatement fillDbStatement(IDbUpdateParameter pUpdateParameter) throws SQLException {
    DbGameLogsDeleteParameter parameter = (DbGameLogsDeleteParameter) pUpdateParameter;
    if (parameter.getCommandNr() >= 0) {
      fStatementDeleteCommand.clearParameters();
      fStatementDeleteCommand.setLong(1, parameter.getGameStateId());
      fStatementDeleteCommand.setShort(2, parameter.getCommandNr());
      return fStatementDeleteCommand;
    } else {
      fStatementDeleteAll.clearParameters();
      fStatementDeleteAll.setLong(1, parameter.getGameStateId());
      return fStatementDeleteAll;
    }
  }

	public int execute(IDbUpdateParameter pUpdateParameter) throws SQLException {
	  return fillDbStatement(pUpdateParameter).executeUpdate();
	}

	public String toString(IDbUpdateParameter pUpdateParameter) throws SQLException {
		return fillDbStatement(pUpdateParameter).toString();
	}
  
}
