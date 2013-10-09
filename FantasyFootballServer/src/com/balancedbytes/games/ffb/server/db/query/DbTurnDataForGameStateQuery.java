package com.balancedbytes.games.ffb.server.db.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.LeaderStateFactory;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.TurnData;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.db.DbStatement;
import com.balancedbytes.games.ffb.server.db.DbStatementId;
import com.balancedbytes.games.ffb.server.db.IDbTableActingPlayers;
import com.balancedbytes.games.ffb.server.db.IDbTableTurnData;

/**
 * 
 * @author Kalimar
 */
public class DbTurnDataForGameStateQuery extends DbStatement {
    
  private PreparedStatement fStatement;
  
  public DbTurnDataForGameStateQuery(FantasyFootballServer pServer) {
    super(pServer);
  }
  
  public DbStatementId getId() {
    return DbStatementId.TURN_DATA_FOR_GAME_STATE_QUERY;
  }
  
  public void prepare(Connection pConnection) {
    try {
      StringBuilder sql = new StringBuilder();
      sql.append("SELECT * FROM ").append(IDbTableTurnData.TABLE_NAME).append(" WHERE ").append(IDbTableActingPlayers.COLUMN_GAME_STATE_ID).append("=?");
      fStatement = pConnection.prepareStatement(sql.toString());
    } catch (SQLException sqlE) {
      throw new FantasyFootballException(sqlE);
    }
  }
  
  public void execute(GameState pGameState) {
    try {
      Game game = pGameState.getGame();
      LeaderStateFactory leaderStateFactory = new LeaderStateFactory();
      fStatement.setLong(1, pGameState.getId());
      ResultSet resultSet = fStatement.executeQuery();
      while (resultSet.next()) {
        int col = 1;
        col++;  // gameStateId
        boolean homeTurnData = resultSet.getBoolean(col++);
        TurnData turnData = homeTurnData ? game.getTurnDataHome() : game.getTurnDataAway();
        turnData.setTurnNr(resultSet.getByte(col++));
        turnData.setFirstTurnAfterKickoff(resultSet.getBoolean(col++));
        turnData.setReRolls(resultSet.getByte(col++));
        turnData.setApothecaries(resultSet.getByte(col++));
        turnData.setReRollUsed(resultSet.getBoolean(col++));
        turnData.setBlitzUsed(resultSet.getBoolean(col++));
        turnData.setFoulUsed(resultSet.getBoolean(col++));
        turnData.setHandOverUsed(resultSet.getBoolean(col++));
        turnData.setPassUsed(resultSet.getBoolean(col++));
        turnData.setLeaderState(leaderStateFactory.forId(resultSet.getByte(col++)));
        turnData.setTurnStarted(resultSet.getBoolean(col++));
      }
      resultSet.close();
    } catch (SQLException pSqlE) {
      throw new FantasyFootballException(pSqlE);
    }
  }
    
}
