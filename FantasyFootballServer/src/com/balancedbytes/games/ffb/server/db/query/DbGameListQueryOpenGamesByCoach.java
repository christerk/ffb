package com.balancedbytes.games.ffb.server.db.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.GameList;
import com.balancedbytes.games.ffb.GameListEntry;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.db.DbStatement;
import com.balancedbytes.games.ffb.server.db.DbStatementId;
import com.balancedbytes.games.ffb.server.db.IDbTableGamesInfo;
import com.balancedbytes.games.ffb.util.StringTool;

/**
 * 
 * @author Kalimar
 */
public class DbGameListQueryOpenGamesByCoach extends DbStatement {
    
  private PreparedStatement fStatement;
  
  public DbGameListQueryOpenGamesByCoach(FantasyFootballServer pServer) {
    super(pServer);
  }
  
  public DbStatementId getId() {
    return DbStatementId.GAME_LIST_QUERY_OPEN_GAMES_BY_COACH;
  }

  public void prepare(Connection pConnection) {
    try {
      StringBuilder sql = new StringBuilder();
      sql.append("SELECT ")
      .append(IDbTableGamesInfo.COLUMN_ID).append(",")
      .append(IDbTableGamesInfo.COLUMN_STARTED).append(",")
      .append(IDbTableGamesInfo.COLUMN_TEAM_HOME_ID).append(",")
      .append(IDbTableGamesInfo.COLUMN_TEAM_HOME_NAME).append(",")
      .append(IDbTableGamesInfo.COLUMN_COACH_HOME).append(",")
      .append(IDbTableGamesInfo.COLUMN_TEAM_AWAY_ID).append(",")
      .append(IDbTableGamesInfo.COLUMN_TEAM_AWAY_NAME).append(",")
      .append(IDbTableGamesInfo.COLUMN_COACH_AWAY)
      .append(" FROM ").append(IDbTableGamesInfo.TABLE_NAME)
      .append(" WHERE ").append(IDbTableGamesInfo.COLUMN_FINISHED).append(" IS NULL")
      .append(" AND (").append(IDbTableGamesInfo.COLUMN_COACH_HOME).append("=?").append(" OR ").append(IDbTableGamesInfo.COLUMN_COACH_AWAY).append("=?").append(")");
      fStatement = pConnection.prepareStatement(sql.toString());
    } catch (SQLException sqlE) {
      throw new FantasyFootballException(sqlE);
    }
  }
  
  public void execute(GameList pGameList, String pCoach) {
  	if ((pGameList == null) || StringTool.isProvided(pCoach)) {
  		return;
  	}
    try {
      fStatement.setString(1, pCoach);
      fStatement.setString(2, pCoach);
      ResultSet resultSet = fStatement.executeQuery();
      while (resultSet.next()) {
        int col = 1;
      	GameListEntry gameListEntry = new GameListEntry();
        gameListEntry.setGameId(resultSet.getLong(col++));
        gameListEntry.setStarted(resultSet.getTimestamp(col++));
        gameListEntry.setTeamHomeId(resultSet.getString(col++));
        gameListEntry.setTeamHomeName(resultSet.getString(col++));
        gameListEntry.setTeamHomeCoach(resultSet.getString(col++));
        gameListEntry.setTeamAwayId(resultSet.getString(col++));
        gameListEntry.setTeamAwayName(resultSet.getString(col++));
        gameListEntry.setTeamAwayCoach(resultSet.getString(col++));
      	pGameList.add(gameListEntry);
      }
    } catch (SQLException sqlE) {
      throw new FantasyFootballException(sqlE);
    }
  }

}
