package com.balancedbytes.games.ffb.server.db.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.GameStatus;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.TurnMode;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.db.DbStatement;
import com.balancedbytes.games.ffb.server.db.DbStatementId;
import com.balancedbytes.games.ffb.server.db.IDbTableGameStates;

/**
 * 
 * @author Kalimar
 */
public class DbGameStatesQuery extends DbStatement {

  private PreparedStatement fStatement;

  public DbGameStatesQuery(FantasyFootballServer pServer) {
    super(pServer);
  }

  public DbStatementId getId() {
    return DbStatementId.GAME_STATES_QUERY;
  }

  public void prepare(Connection pConnection) {
    try {
      StringBuilder sql = new StringBuilder();
      sql.append("SELECT * FROM ").append(IDbTableGameStates.TABLE_NAME).append(" WHERE id=?");
      fStatement = pConnection.prepareStatement(sql.toString());
    } catch (SQLException sqlE) {
      throw new FantasyFootballException(sqlE);
    }
  }

  public GameState execute(FantasyFootballServer pServer, long pGameStateId) {
    GameState gameState = null;
    try {
      fStatement.setLong(1, pGameStateId);
      ResultSet resultSet = fStatement.executeQuery();
      while (resultSet.next()) {
        int col = 1;
        Game game = new Game();
        gameState = new GameState(pServer);
        gameState.setGame(game);
        game.setId(resultSet.getLong(col++));
        Timestamp scheduled = resultSet.getTimestamp(col++);
        if (scheduled != null) {
          game.setScheduled(new Date(scheduled.getTime()));
        }
        Timestamp started = resultSet.getTimestamp(col++);
        if (started != null) {
          game.setStarted(new Date(started.getTime()));
        }
        Timestamp finished = resultSet.getTimestamp(col++);
        if (finished != null) {
          game.setFinished(new Date(finished.getTime()));
        }
        game.setHalf(resultSet.getByte(col++));
        game.setTurnMode(TurnMode.fromId(resultSet.getByte(col++)));
        game.setHomePlaying(resultSet.getBoolean(col++));
        game.setHomeFirstOffense(resultSet.getBoolean(col++));
        game.setSetupOffense(resultSet.getBoolean(col++));
        game.setWaitingForOpponent(resultSet.getBoolean(col++));
        game.setDefenderId(resultSet.getString(col++));
        game.setDefenderAction(PlayerAction.fromId(resultSet.getByte(col++)));
        int coordinateX = resultSet.getByte(col++);
        int coordinateY = resultSet.getByte(col++);
        if (!resultSet.wasNull()) {
          game.setPassCoordinate(new FieldCoordinate(coordinateX, coordinateY));
        }
        game.setTurnTime(resultSet.getLong(col++));
        game.setTimeoutPossible(resultSet.getBoolean(col++));
        game.setTimeoutEnforced(resultSet.getBoolean(col++));
        game.setConcessionPossible(resultSet.getBoolean(col++));
        game.setTesting(resultSet.getBoolean(col++));
        gameState.setStatus(GameStatus.fromTypeString(resultSet.getString(col++)));
        game.setThrowerId(resultSet.getString(col++));
        game.setThrowerAction(PlayerAction.fromId(resultSet.getByte(col++)));
      }
      resultSet.close();
    } catch (SQLException pSqlE) {
      throw new FantasyFootballException(pSqlE);
    }
    return gameState;
  }

}
