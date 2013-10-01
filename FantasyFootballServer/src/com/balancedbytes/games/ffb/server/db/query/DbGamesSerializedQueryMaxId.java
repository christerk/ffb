package com.balancedbytes.games.ffb.server.db.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.db.DbStatement;
import com.balancedbytes.games.ffb.server.db.DbStatementId;
import com.balancedbytes.games.ffb.server.db.IDbTableGamesSerialized;

/**
 * 
 * @author Kalimar
 */
public class DbGamesSerializedQueryMaxId extends DbStatement {
  
  private PreparedStatement fStatement;
  
  public DbGamesSerializedQueryMaxId(FantasyFootballServer pServer) {
    super(pServer);
  }
  
  public DbStatementId getId() {
    return DbStatementId.GAMES_SERIALIZED_QUERY_MAX_ID;
  }
  
  public void prepare(Connection pConnection) {
    try {
      StringBuilder sql = new StringBuilder();
      sql.append("SELECT MAX(").append(IDbTableGamesSerialized.COLUMN_ID).append(") FROM ").append(IDbTableGamesSerialized.TABLE_NAME);
      fStatement = pConnection.prepareStatement(sql.toString());
    } catch (SQLException sqlE) {
      throw new FantasyFootballException(sqlE);
    }
  }
  
  public long execute() {
    long maxId = 0;
    try {
      ResultSet resultSet = fStatement.executeQuery();
      while (resultSet.next()) {
        maxId = resultSet.getLong(1);
      }
      resultSet.close();
    } catch (SQLException pSqlE) {
      throw new FantasyFootballException(pSqlE);
    }
    return maxId;
  }
    
}
