package com.balancedbytes.games.ffb.server.db.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.GameList;
import com.balancedbytes.games.ffb.GameListEntry;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.db.DbStatement;
import com.balancedbytes.games.ffb.server.db.DbStatementId;
import com.balancedbytes.games.ffb.server.db.IDbTableGameStates;
import com.balancedbytes.games.ffb.server.db.IDbTableTeams;

/**
 * 
 * @author Kalimar
 */
public class DbGameListQueryOpenGamesByCoachOld extends DbStatement {
  
  private class QueryResult {

    private long fGameStateId;
    private Timestamp fStarted;
    private boolean fHomeTeam;
    private String fTeamId;
    private String fTeamName;
    private String fCoach;
    
    public QueryResult(ResultSet pResultSet) throws SQLException {
      if (pResultSet != null) {
        int col = 1;
        fGameStateId = pResultSet.getLong(col++);
        fStarted = pResultSet.getTimestamp(col++);
        fHomeTeam = pResultSet.getBoolean(col++);
        fTeamId = pResultSet.getString(col++);
        fTeamName = pResultSet.getString(col++);
        fCoach = pResultSet.getString(col++);
      }
    }
    
    public long getGameStateId() {
      return fGameStateId;
    }

    public Timestamp getStarted() {
      return fStarted;
    }

    public boolean isHomeTeam() {
      return fHomeTeam;
    }

    public String getTeamId() {
      return fTeamId;
    }

    public String getTeamName() {
      return fTeamName;
    }

    public String getCoach() {
      return fCoach;
    }
    
  }
  
  private PreparedStatement fStatement;
  
  public DbGameListQueryOpenGamesByCoachOld(FantasyFootballServer pServer) {
    super(pServer);
  }
  
  public DbStatementId getId() {
    return DbStatementId.GAME_LIST_QUERY_OPEN_GAMES_BY_COACH_OLD;
  }

  public void prepare(Connection pConnection) {
    try {
      StringBuilder sql = new StringBuilder();
      sql.append("SELECT ")
      .append(IDbTableGameStates.TABLE_NAME).append(".id,")
      .append(IDbTableGameStates.TABLE_NAME).append(".started,")
      .append(IDbTableTeams.TABLE_NAME).append(".home_team,")
      .append(IDbTableTeams.TABLE_NAME).append(".id,")
      .append(IDbTableTeams.TABLE_NAME).append(".name,")
      .append(IDbTableTeams.TABLE_NAME).append(".coach")
      .append(" FROM ").append(IDbTableGameStates.TABLE_NAME).append(",").append(IDbTableTeams.TABLE_NAME)
      .append(" WHERE ").append(IDbTableGameStates.TABLE_NAME).append(".id=").append(IDbTableTeams.TABLE_NAME).append(".game_state_id")
      .append(" AND ").append(IDbTableGameStates.TABLE_NAME).append(".finished IS NULL")
      .append(" AND ").append(IDbTableGameStates.TABLE_NAME).append(".id IN (")
        .append("SELECT ").append(IDbTableGameStates.TABLE_NAME).append(".id")
        .append(" FROM ").append(IDbTableGameStates.TABLE_NAME).append(",").append(IDbTableTeams.TABLE_NAME)
        .append(" WHERE ").append(IDbTableGameStates.TABLE_NAME).append(".id=").append(IDbTableTeams.TABLE_NAME).append(".game_state_id")
        .append(" AND ").append(IDbTableTeams.TABLE_NAME).append(".coach=?")
      .append(")");
      fStatement = pConnection.prepareStatement(sql.toString());
    } catch (SQLException sqlE) {
      throw new FantasyFootballException(sqlE);
    }
  }
  
  public void execute(GameList pGameList, String pCoach) {
    Map<Long, QueryResult> queryResultsHome = new HashMap<Long, QueryResult>();
    Map<Long, QueryResult> queryResultsAway = new HashMap<Long, QueryResult>();
    try {
      fStatement.setString(1, pCoach);
      ResultSet resultSet = fStatement.executeQuery();
      while (resultSet.next()) {
        QueryResult queryResult = new QueryResult(resultSet);
        if (queryResult.isHomeTeam()) {
          queryResultsHome.put(queryResult.getGameStateId(), queryResult);
        } else {
          queryResultsAway.put(queryResult.getGameStateId(), queryResult);
        }
      }
    } catch (SQLException sqlE) {
      throw new FantasyFootballException(sqlE);
    }
    for (long gameStateId : queryResultsHome.keySet()) {
      GameListEntry gameListEntry = createGameListEntry(queryResultsHome.get(gameStateId), queryResultsAway.get(gameStateId));
      if (gameListEntry != null) {
      	pGameList.add(gameListEntry);
      }
    }
  }
  
  private GameListEntry createGameListEntry(QueryResult pQueryResultHome, QueryResult pQueryResultAway) {
    GameListEntry gameListEntry = null;
    if ((pQueryResultHome != null) && (pQueryResultAway != null)) {
      gameListEntry = new GameListEntry();
      gameListEntry.setGameId(pQueryResultHome.getGameStateId());
      gameListEntry.setStarted(pQueryResultHome.getStarted());
      gameListEntry.setTeamHomeId(pQueryResultHome.getTeamId());
      gameListEntry.setTeamHomeName(pQueryResultHome.getTeamName());
      gameListEntry.setTeamHomeCoach(pQueryResultHome.getCoach());
      gameListEntry.setTeamAwayId(pQueryResultAway.getTeamId());
      gameListEntry.setTeamAwayName(pQueryResultAway.getTeamName());
      gameListEntry.setTeamAwayCoach(pQueryResultAway.getCoach());
    }
    return gameListEntry;
  }
  
}
