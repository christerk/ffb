package com.balancedbytes.games.ffb.server.db.old;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.GameResult;
import com.balancedbytes.games.ffb.model.TeamResult;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.db.DbStatement;
import com.balancedbytes.games.ffb.server.db.DbStatementId;
import com.balancedbytes.games.ffb.util.StringTool;

/**
 * 
 * @author Kalimar
 */
public class DbTeamResultsForGameStateQuery extends DbStatement {
  
  public class QueryResult {

    private long fGameStateId;
    private String fTeamId;
    private byte fScore;
    private boolean fConceded;
    private byte fFame;
    private int fSpectators;
    private int fWinnings;
    private int fSpirallingExpenses;
    private byte fFanFactorModifier;
    private byte fBadlyHurtSuffered;
    private byte fSeriousInjurySuffered;
    private byte fRipSuffered;
    private byte fRaisedDead;
    private int fPettyCashTransferred;
    private int fPettyCashUsed;
    private int fTeamValue;
    
    public QueryResult(ResultSet pResultSet) throws SQLException {
      if (pResultSet != null) {
        int col = 1;
        fGameStateId = pResultSet.getLong(col++);
        fTeamId = pResultSet.getString(col++);
        fScore = pResultSet.getByte(col++);
        fConceded = pResultSet.getBoolean(col++);
        fFame = pResultSet.getByte(col++);
        fSpectators = pResultSet.getInt(col++);
        fWinnings = pResultSet.getInt(col++);
        fSpirallingExpenses = pResultSet.getInt(col++);
        fFanFactorModifier = pResultSet.getByte(col++);
        fBadlyHurtSuffered = pResultSet.getByte(col++);
        fSeriousInjurySuffered = pResultSet.getByte(col++);
        fRipSuffered = pResultSet.getByte(col++);
        fRaisedDead = pResultSet.getByte(col++);
        fPettyCashTransferred = pResultSet.getInt(col++);
        fPettyCashUsed = pResultSet.getInt(col++);
        fTeamValue = pResultSet.getInt(col++);
      }
    }
    
    public void fillTeamResult(GameResult pGameResult) {
      Game game = pGameResult.getGame();
      TeamResult teamResult = StringTool.isEqual(game.getTeamHome().getId(), fTeamId) ? pGameResult.getTeamResultHome() : pGameResult.getTeamResultAway();
      teamResult.setScore(fScore);
      teamResult.setConceded(fConceded);
      teamResult.setFame(fFame);
      teamResult.setSpectators(fSpectators);
      teamResult.setWinnings(fWinnings);
      teamResult.setSpirallingExpenses(fSpirallingExpenses);
      teamResult.setFanFactorModifier(fFanFactorModifier);
      teamResult.setBadlyHurtSuffered(fBadlyHurtSuffered);
      teamResult.setSeriousInjurySuffered(fSeriousInjurySuffered);
      teamResult.setRipSuffered(fRipSuffered);
      teamResult.setRaisedDead(fRaisedDead);
      teamResult.setPettyCashTransferred(fPettyCashTransferred);
      teamResult.setPettyCashUsed(fPettyCashUsed);
      teamResult.setTeamValue(fTeamValue);
    }

    public long getGameStateId() {
      return fGameStateId;
    }
    
    public String getTeamId() {
      return fTeamId;
    }

  }
  
  private PreparedStatement fStatement;
  
  public DbTeamResultsForGameStateQuery(FantasyFootballServer pServer) {
    super(pServer);
  }
  
  public DbStatementId getId() {
    return DbStatementId.TEAM_RESULTS_FOR_GAME_STATE_QUERY;
  }
  
  public void prepare(Connection pConnection) {
    try {
      StringBuilder sql = new StringBuilder();
      sql.append("SELECT * FROM ").append(IDbTableTeamResults.TABLE_NAME).append(" WHERE ").append(IDbTableTeamResults.COLUMN_GAME_STATE_ID).append("=?");
      fStatement = pConnection.prepareStatement(sql.toString());
    } catch (SQLException sqlE) {
      throw new FantasyFootballException(sqlE);
    }
  }
  
  public void execute(GameState pGameState) {
    try {
      fStatement.setLong(1, pGameState.getId());
      List<QueryResult> queryResults = new ArrayList<QueryResult>();
      ResultSet resultSet = fStatement.executeQuery();
      while (resultSet.next()) {
        queryResults.add(new QueryResult(resultSet));
      }
      resultSet.close();
      Game game = pGameState.getGame();
      for (QueryResult queryResult : queryResults) {
        queryResult.fillTeamResult(game.getGameResult());
      }
    } catch (SQLException pSqlE) {
      throw new FantasyFootballException(pSqlE);
    }
  }
    
}
