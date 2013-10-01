package com.balancedbytes.games.ffb.server.db.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.db.DbStatement;
import com.balancedbytes.games.ffb.server.db.DbStatementId;
import com.balancedbytes.games.ffb.server.db.IDbTableActingPlayers;

/**
 * 
 * @author Kalimar
 */
public class DbActingPlayersForGameStateQuery extends DbStatement {
  
  private class QueryResult {

    private String fPlayerId;
    private byte fStrength;
    private byte fCurrentMove;
    private boolean fGoingForIt;
    private boolean fDodging;
    private boolean fLeaping;
    private boolean fHasBlocked;
    private boolean fHasFouled;
    private boolean fHasPassed;
    private boolean fHasMoved;
    private PlayerAction fPlayerAction;
    private boolean fStandingUp;
    private boolean fSufferingBloodLust;
    private boolean fSufferingAnimosity;
    private Skill fUsedSkill1;
    private Skill fUsedSkill2;
    private Skill fUsedSkill3;
    private Skill fUsedSkill4;
    private Skill fUsedSkill5;
    
    public QueryResult(ResultSet pResultSet) throws SQLException {
      if (pResultSet != null) {
        int col = 1;
        pResultSet.getLong(col++);  // gameStateId
        fPlayerId = pResultSet.getString(col++);
        fStrength = pResultSet.getByte(col++);
        fCurrentMove = pResultSet.getByte(col++);
        fGoingForIt = pResultSet.getBoolean(col++);
        fDodging = pResultSet.getBoolean(col++);
        fLeaping = pResultSet.getBoolean(col++);
        fHasBlocked = pResultSet.getBoolean(col++);
        fHasFouled = pResultSet.getBoolean(col++);
        fHasPassed = pResultSet.getBoolean(col++);
        fHasMoved = pResultSet.getBoolean(col++);
        fPlayerAction = PlayerAction.fromId(pResultSet.getByte(col++));
        fStandingUp = pResultSet.getBoolean(col++);
        fSufferingBloodLust = pResultSet.getBoolean(col++);
        fSufferingAnimosity = pResultSet.getBoolean(col++);
        fUsedSkill1 = Skill.fromId(pResultSet.getByte(col++));
        fUsedSkill2 = Skill.fromId(pResultSet.getByte(col++));
        fUsedSkill3 = Skill.fromId(pResultSet.getByte(col++));
        fUsedSkill4 = Skill.fromId(pResultSet.getByte(col++));
        fUsedSkill5 = Skill.fromId(pResultSet.getByte(col++));
      }
    }

    public String getPlayerId() {
      return fPlayerId;
    }

    public byte getStrength() {
      return fStrength;
    }

    public byte getCurrentMove() {
      return fCurrentMove;
    }

    public boolean isGoingForIt() {
      return fGoingForIt;
    }

    public boolean isDodging() {
      return fDodging;
    }

    public boolean isLeaping() {
      return fLeaping;
    }

    public boolean isHasBlocked() {
      return fHasBlocked;
    }

    public boolean isHasFouled() {
      return fHasFouled;
    }

    public boolean isHasPassed() {
      return fHasPassed;
    }

    public boolean isHasMoved() {
      return fHasMoved;
    }

    public PlayerAction getPlayerAction() {
      return fPlayerAction;
    }
    
    public boolean isStandingUp() {
      return fStandingUp;
    }
    
    public boolean isSufferingBloodLust() {
      return fSufferingBloodLust;
    }
    
    public boolean isSufferingAnimosity() {
      return fSufferingAnimosity;
    }

    public Skill getUsedSkill1() {
      return fUsedSkill1;
    }

    public Skill getUsedSkill2() {
      return fUsedSkill2;
    }

    public Skill getUsedSkill3() {
      return fUsedSkill3;
    }

    public Skill getUsedSkill4() {
      return fUsedSkill4;
    }

    public Skill getUsedSkill5() {
      return fUsedSkill5;
    }
    
  }
  
  private PreparedStatement fStatement;
  
  public DbActingPlayersForGameStateQuery(FantasyFootballServer pServer) {
    super(pServer);
  }
  
  public DbStatementId getId() {
    return DbStatementId.ACTING_PLAYERS_FOR_GAME_STATE_QUERY;
  }
  
  public void prepare(Connection pConnection) {
    try {
      StringBuilder sql = new StringBuilder();
      sql.append("SELECT * FROM ").append(IDbTableActingPlayers.TABLE_NAME).append(" WHERE ").append(IDbTableActingPlayers.COLUMN_GAME_STATE_ID).append("=?");
      fStatement = pConnection.prepareStatement(sql.toString());
    } catch (SQLException sqlE) {
      throw new FantasyFootballException(sqlE);
    }
  }
  
  public void execute(GameState pGameState) {
    try {
      Game game = pGameState.getGame();
      ActingPlayer actingPlayer = game.getActingPlayer();
      fStatement.setLong(1, pGameState.getId());
      ResultSet resultSet = fStatement.executeQuery();
      while (resultSet.next()) {
        QueryResult queryResult = new QueryResult(resultSet);
        actingPlayer.setPlayer(game.getPlayerById(queryResult.getPlayerId()));
        actingPlayer.setStrength(queryResult.getStrength());
        actingPlayer.setCurrentMove(queryResult.getCurrentMove());
        actingPlayer.setGoingForIt(queryResult.isGoingForIt());
        actingPlayer.setDodging(queryResult.isDodging());
        actingPlayer.setLeaping(queryResult.isLeaping());
        actingPlayer.setHasBlocked(queryResult.isHasBlocked());
        actingPlayer.setHasFouled(queryResult.isHasFouled());
        actingPlayer.setHasPassed(queryResult.isHasPassed());
        actingPlayer.setHasMoved(queryResult.isHasMoved());
        actingPlayer.setPlayerAction(queryResult.getPlayerAction());
        actingPlayer.setStandingUp(queryResult.isStandingUp());
        actingPlayer.setSufferingBloodLust(queryResult.isSufferingBloodLust());
        actingPlayer.setSufferingAnimosity(queryResult.isSufferingAnimosity());
        actingPlayer.markSkillUsed(queryResult.getUsedSkill1());
        actingPlayer.markSkillUsed(queryResult.getUsedSkill2());
        actingPlayer.markSkillUsed(queryResult.getUsedSkill3());
        actingPlayer.markSkillUsed(queryResult.getUsedSkill4());
        actingPlayer.markSkillUsed(queryResult.getUsedSkill5());
      }
      resultSet.close();
    } catch (SQLException pSqlE) {
      throw new FantasyFootballException(pSqlE);
    }
  }
    
}
