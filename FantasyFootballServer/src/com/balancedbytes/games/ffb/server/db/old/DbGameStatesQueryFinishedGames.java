package com.balancedbytes.games.ffb.server.db.old;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.db.DbStatement;
import com.balancedbytes.games.ffb.server.db.DbStatementId;

/**
 * 
 * @author Georg Seipler
 */
public class DbGameStatesQueryFinishedGames extends DbStatement {
  
  private PreparedStatement fStatement;
  
  public DbGameStatesQueryFinishedGames(FantasyFootballServer pServer) {
    super(pServer);
  }
  
  public DbStatementId getId() {
    return DbStatementId.GAME_STATES_QUERY_FINISHED_GAMES;
  }
  
  public void prepare(Connection pConnection) {
    try {
      StringBuilder sql = new StringBuilder();
      sql.append("SELECT id FROM ").append(IDbTableGameStates.TABLE_NAME);
      sql.append(" WHERE ").append(IDbTableGameStates.COLUMN_ID).append(">=?");
      sql.append(" AND ").append(IDbTableGameStates.COLUMN_ID).append("<?");
      sql.append(" AND ").append(IDbTableGameStates.COLUMN_FINISHED).append(" IS NOT NULL");
      fStatement = pConnection.prepareStatement(sql.toString());
    } catch (SQLException sqlE) {
      throw new FantasyFootballException(sqlE);
    }
  }
  
  public long[] execute(long pStartGameId, long pEndGameId) {
  	List<Long> gameIdList = new ArrayList<Long>();
    try {
      fStatement.setLong(1, pStartGameId);
      fStatement.setLong(2, pEndGameId);
      ResultSet resultSet = fStatement.executeQuery();
      while (resultSet.next()) {
      	gameIdList.add(resultSet.getLong(1));
      }
      resultSet.close();
    } catch (SQLException pSqlE) {
      throw new FantasyFootballException(pSqlE);
    }
    long[] gameIds = new long[gameIdList.size()];
    for (int i = 0; i < gameIds.length; i++) {
    	gameIds[i] = gameIdList.get(i);
    }
    return gameIds;
  }
    
}
