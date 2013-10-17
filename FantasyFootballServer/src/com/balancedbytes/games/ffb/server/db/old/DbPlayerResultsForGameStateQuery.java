package com.balancedbytes.games.ffb.server.db.old;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.SendToBoxReasonFactory;
import com.balancedbytes.games.ffb.SeriousInjuryFactory;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.GameResult;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.PlayerResult;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.db.DbStatement;
import com.balancedbytes.games.ffb.server.db.DbStatementId;

/**
 * 
 * @author Kalimar
 */
public class DbPlayerResultsForGameStateQuery extends DbStatement {
  
  public class QueryResult {

    private long fGameStateId;
    private String fPlayerId;
    private byte fCompletions;
    private byte fTouchdowns;
    private byte fInterceptions;
    private byte fCasualties;
    private byte fPlayerAwards;
    private byte fPassing;
    private byte fRushing;
    private byte fBlocks;
    private byte fFouls;
    private short fCurrentSpps;
    private byte fSeriousInjury;
    private byte fSeriousInjuryDecay;
    private byte fSendToBoxReason;
    private byte fSendToBoxTurn;
    private byte fSendToBoxHalf;
    private String fSendToBoxByPlayerId;
    private byte fTurnsPlayed;
    private boolean fHasUsedSecretWeapon;
    private boolean fDefecting;
  
    public QueryResult(ResultSet pResultSet) throws SQLException {
      if (pResultSet != null) {
        int col = 1;
        fGameStateId = pResultSet.getLong(col++);
        fPlayerId = pResultSet.getString(col++);
        fCompletions = pResultSet.getByte(col++);
        fTouchdowns = pResultSet.getByte(col++);
        fInterceptions = pResultSet.getByte(col++);
        fCasualties = pResultSet.getByte(col++);
        fPlayerAwards = pResultSet.getByte(col++);
        fPassing = pResultSet.getByte(col++);
        fRushing = pResultSet.getByte(col++);
        fBlocks = pResultSet.getByte(col++);
        fFouls = pResultSet.getByte(col++);
        fCurrentSpps = pResultSet.getShort(col++);
        fSeriousInjury = pResultSet.getByte(col++);
        fSeriousInjuryDecay = pResultSet.getByte(col++);
        fSendToBoxReason = pResultSet.getByte(col++);
        fSendToBoxTurn = pResultSet.getByte(col++);
        fSendToBoxHalf = pResultSet.getByte(col++);
        fSendToBoxByPlayerId = pResultSet.getString(col++);
        fTurnsPlayed = pResultSet.getByte(col++);
        fHasUsedSecretWeapon = pResultSet.getBoolean(col++);
        fDefecting = pResultSet.getBoolean(col++);
      }
    }
    
    public void fillPlayerResult(GameResult pGameResult) {
      Game game = pGameResult.getGame();
      Player player = game.getPlayerById(fPlayerId); 
      PlayerResult playerResult = pGameResult.getPlayerResult(player);
      playerResult.setCompletions(fCompletions);
      playerResult.setTouchdowns(fTouchdowns);
      playerResult.setInterceptions(fInterceptions);
      playerResult.setCasualties(fCasualties);
      playerResult.setPlayerAwards(fPlayerAwards);
      playerResult.setPassing(fPassing);
      playerResult.setRushing(fRushing);
      playerResult.setBlocks(fBlocks);
      playerResult.setFouls(fFouls);
      playerResult.setCurrentSpps(fCurrentSpps);
      playerResult.setSeriousInjury(new SeriousInjuryFactory().forId(fSeriousInjury));
      playerResult.setSeriousInjuryDecay(new SeriousInjuryFactory().forId(fSeriousInjuryDecay));
      playerResult.setSendToBoxReason(new SendToBoxReasonFactory().forId(fSendToBoxReason));
      playerResult.setSendToBoxTurn(fSendToBoxTurn);
      playerResult.setSendToBoxHalf(fSendToBoxHalf);
      playerResult.setSendToBoxByPlayerId(fSendToBoxByPlayerId);
      playerResult.setTurnsPlayed(fTurnsPlayed);
      playerResult.setHasUsedSecretWeapon(fHasUsedSecretWeapon);
      playerResult.setDefecting(fDefecting);
    }
    
    public long getGameStateId() {
      return fGameStateId;
    }

  }
  
  private PreparedStatement fStatement;

  public DbPlayerResultsForGameStateQuery(FantasyFootballServer pServer) {
    super(pServer);
  }
  
  public DbStatementId getId() {
    return DbStatementId.PLAYER_RESULTS_FOR_GAME_STATE_QUERY;
  }
  
  public void prepare(Connection pConnection) {
    try {
      StringBuilder sql = new StringBuilder();
      sql.append("SELECT * FROM ").append(IDbTablePlayerResults.TABLE_NAME).append(" WHERE ").append(IDbTablePlayerResults.COLUMN_GAME_STATE_ID).append("=?");
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
        queryResult.fillPlayerResult(game.getGameResult());
      }
    } catch (SQLException pSqlE) {
      throw new FantasyFootballException(pSqlE);
    }
  }
    
}
