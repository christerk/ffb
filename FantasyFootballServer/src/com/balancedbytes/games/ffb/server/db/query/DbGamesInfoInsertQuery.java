package com.balancedbytes.games.ffb.server.db.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.db.DbStatement;
import com.balancedbytes.games.ffb.server.db.DbStatementId;
import com.balancedbytes.games.ffb.server.db.IDbTableGamesInfo;

/**
 * 
 * @author Kalimar
 */
public class DbGamesInfoInsertQuery extends DbStatement {

  private PreparedStatement fStatement;

  public DbGamesInfoInsertQuery(FantasyFootballServer pServer) {
    super(pServer);
  }

  public DbStatementId getId() {
    return DbStatementId.GAMES_INFO_INSERT_QUERY;
  }
    
  public void prepare(Connection pConnection) {
    try {
      StringBuilder sql = new StringBuilder();
      sql.append("INSERT INTO ").append(IDbTableGamesInfo.TABLE_NAME).append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
      fStatement = pConnection.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
    } catch (SQLException sqlE) {
      throw new FantasyFootballException(sqlE);
    }
  }
  
  public void execute(GameState pGameState) {
    if (pGameState == null) {
      return;
    }
    try {
      Game game = pGameState.getGame();
      int col = 1;
      fStatement.setLong(col++, game.getId());                                                                              // 1
      fStatement.setTimestamp(col++, (game.getScheduled() != null) ? new Timestamp(game.getScheduled().getTime()) : null);  // 2
      fStatement.setTimestamp(col++, (game.getStarted() != null) ? new Timestamp(game.getStarted().getTime()) : null);      // 3
      fStatement.setTimestamp(col++, (game.getFinished() != null) ? new Timestamp(game.getFinished().getTime()) : null);    // 4
      fStatement.setString(col++, (game.getTeamHome() != null) ? game.getTeamHome().getCoach() : null);                     // 5
      fStatement.setString(col++, (game.getTeamHome() != null) ? game.getTeamHome().getId() : null);                        // 6
      fStatement.setString(col++, (game.getTeamHome() != null) ? game.getTeamHome().getName() : null);                      // 7
      fStatement.setString(col++, (game.getTeamAway() != null) ? game.getTeamAway().getCoach() : null);                     // 8
      fStatement.setString(col++, (game.getTeamAway() != null) ? game.getTeamAway().getId() : null);                        // 9
      fStatement.setString(col++, (game.getTeamAway() != null) ? game.getTeamAway().getName() : null);                      // 10
      fStatement.setByte(col++, (byte) game.getHalf());                                                                     // 11
      fStatement.setByte(col++, (byte) Math.min(game.getTurnDataHome().getTurnNr(), game.getTurnDataAway().getTurnNr()));   // 12
      fStatement.setBoolean(col++, game.isHomePlaying());                                                                   // 13
      fStatement.setString(col++,(pGameState.getStatus() != null) ? pGameState.getStatus().getTypeString() : " ");          // 14
      fStatement.setBoolean(col++, game.isTesting());                                                                       // 15
      fStatement.setTimestamp(col++, null);                                                                                 // 16
      fStatement.executeUpdate();
      ResultSet rs = fStatement.getGeneratedKeys();
      if (rs.next()) {
        pGameState.getGame().setId(rs.getLong(1));
      }
    } catch (SQLException pSqlException) {
      throw new FantasyFootballException(pSqlException);
    }
  }

}
