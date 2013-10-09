package com.balancedbytes.games.ffb.server.db.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.PlayerGender;
import com.balancedbytes.games.ffb.PlayerType;
import com.balancedbytes.games.ffb.SeriousInjuryFactory;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.db.DbStatement;
import com.balancedbytes.games.ffb.server.db.DbStatementId;
import com.balancedbytes.games.ffb.server.db.IDbTablePlayers;

/**
 * 
 * @author Kalimar
 */
public class DbPlayersForGameStateQuery extends DbStatement {
  
  private PreparedStatement fStatement;
  
  public DbPlayersForGameStateQuery(FantasyFootballServer pServer) {
    super(pServer);
  }
  
  public DbStatementId getId() {
    return DbStatementId.PLAYERS_FOR_GAME_STATE_QUERY;
  }

  public void prepare(Connection pConnection) {
    try {
      StringBuilder sql = new StringBuilder();
      sql.append("SELECT * FROM ").append(IDbTablePlayers.TABLE_NAME).append(" WHERE game_state_id=?");
      fStatement = pConnection.prepareStatement(sql.toString());
    } catch (SQLException sqlE) {
      throw new FantasyFootballException(sqlE);
    }
  }
  
  public void execute(GameState pGameState) {
    if (pGameState != null) {
      try {
        Game game = pGameState.getGame();
        SeriousInjuryFactory seriousInjuryFactory = new SeriousInjuryFactory();
        fStatement.setLong(1, pGameState.getId());
        ResultSet resultSet = fStatement.executeQuery();
        while (resultSet.next()) {
          int col = 1;
          col++;  // gameStateId
          Player player = new Player();
          player.setId(resultSet.getString(col++));
          Team team = game.getTeamById(resultSet.getString(col++));
          player.setPositionId(resultSet.getString(col++));
          player.setNr(resultSet.getByte(col++));
          player.setName(resultSet.getString(col++));
          player.setGender(PlayerGender.fromTypeString(resultSet.getString(col++)));
          player.setType(PlayerType.fromId(resultSet.getByte(col++)));
          player.setMovement(resultSet.getByte(col++));
          player.setStrength(resultSet.getByte(col++));
          player.setAgility(resultSet.getByte(col++));
          player.setArmour(resultSet.getByte(col++));
          player.setRecoveringInjury(seriousInjuryFactory.forId(resultSet.getByte(col++)));
          team.add(player);
        }
        resultSet.close();
      } catch (SQLException sqlE) {
        throw new FantasyFootballException(sqlE);
      }
    }
  }

}
