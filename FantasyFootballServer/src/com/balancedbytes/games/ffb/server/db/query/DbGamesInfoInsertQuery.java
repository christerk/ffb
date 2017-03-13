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
      sql.append("INSERT INTO ").append(IDbTableGamesInfo.TABLE_NAME);
      sql.append("(").append(IDbTableGamesInfo.COLUMN_SCHEDULED).append(","); // 1
      sql.append(IDbTableGamesInfo.COLUMN_STARTED).append(",");  // 2
      sql.append(IDbTableGamesInfo.COLUMN_FINISHED).append(",");  // 3
      sql.append(IDbTableGamesInfo.COLUMN_COACH_HOME).append(",");  // 4)
      sql.append(IDbTableGamesInfo.COLUMN_TEAM_HOME_ID).append(",");  // 5
      sql.append(IDbTableGamesInfo.COLUMN_TEAM_HOME_NAME).append(",");  // 6
      sql.append(IDbTableGamesInfo.COLUMN_COACH_AWAY).append(",");  // 7
      sql.append(IDbTableGamesInfo.COLUMN_TEAM_AWAY_ID).append(",");  // 8
      sql.append(IDbTableGamesInfo.COLUMN_TEAM_AWAY_NAME).append(",");  // 9
      sql.append(IDbTableGamesInfo.COLUMN_HALF).append(",");  // 10
      sql.append(IDbTableGamesInfo.COLUMN_TURN).append(",");  // 11
      sql.append(IDbTableGamesInfo.COLUMN_HOME_PLAYING).append(",");  // 12
      sql.append(IDbTableGamesInfo.COLUMN_STATUS).append(",");  // 13
      sql.append(IDbTableGamesInfo.COLUMN_TESTING).append(")");  // 14
      sql.append(IDbTableGamesInfo.COLUMN_ADMIN_MODE).append(")");  // 15
	  sql.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
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
      fStatement.setTimestamp(col++, (game.getScheduled() != null) ? new Timestamp(game.getScheduled().getTime()) : null);  // 1
      fStatement.setTimestamp(col++, (game.getStarted() != null) ? new Timestamp(game.getStarted().getTime()) : null);      // 2
      fStatement.setTimestamp(col++, (game.getFinished() != null) ? new Timestamp(game.getFinished().getTime()) : null);    // 3
      fStatement.setString(col++, (game.getTeamHome() != null) ? game.getTeamHome().getCoach() : null);                     // 4
      fStatement.setString(col++, (game.getTeamHome() != null) ? game.getTeamHome().getId() : null);                        // 5
      fStatement.setString(col++, (game.getTeamHome() != null) ? game.getTeamHome().getName() : null);                      // 6
      fStatement.setString(col++, (game.getTeamAway() != null) ? game.getTeamAway().getCoach() : null);                     // 7
      fStatement.setString(col++, (game.getTeamAway() != null) ? game.getTeamAway().getId() : null);                        // 8
      fStatement.setString(col++, (game.getTeamAway() != null) ? game.getTeamAway().getName() : null);                      // 9
      fStatement.setByte(col++, (byte) game.getHalf());                                                                     // 10
      fStatement.setByte(col++, (byte) Math.min(game.getTurnDataHome().getTurnNr(), game.getTurnDataAway().getTurnNr()));   // 11
      fStatement.setBoolean(col++, game.isHomePlaying());                                                                   // 12
      fStatement.setString(col++,(pGameState.getStatus() != null) ? pGameState.getStatus().getTypeString() : " ");          // 13
      fStatement.setBoolean(col++, game.isTesting());                                                                       // 14
      fStatement.setBoolean(col++, game.isAdminMode());                                                                     // 15
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
