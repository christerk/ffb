package com.balancedbytes.games.ffb.server.db.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.SeriousInjury;
import com.balancedbytes.games.ffb.SeriousInjuryFactory;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.db.DbStatement;
import com.balancedbytes.games.ffb.server.db.DbStatementId;
import com.balancedbytes.games.ffb.server.db.IDbTablePlayerInjuries;

/**
 * 
 * @author Kalimar
 */
public class DbPlayerInjuriesForGameStateQuery extends DbStatement {
  
  private PreparedStatement fStatement;
  
  public DbPlayerInjuriesForGameStateQuery(FantasyFootballServer pServer) {
    super(pServer);
  }
  
  public DbStatementId getId() {
    return DbStatementId.PLAYER_INJURIES_FOR_GAME_STATE_QUERY;
  }

  public void prepare(Connection pConnection) {
    try {
      StringBuilder sql = new StringBuilder();
      sql.append("SELECT * FROM ").append(IDbTablePlayerInjuries.TABLE_NAME).append(" WHERE game_state_id=?");
      fStatement = pConnection.prepareStatement(sql.toString());
    } catch (SQLException sqlE) {
      throw new FantasyFootballException(sqlE);
    }
  }
  
  public void execute(GameState pGameState) {
    if (pGameState != null) {
      try {
        SeriousInjuryFactory seriousInjuryFactory = new SeriousInjuryFactory();
        Game game = pGameState.getGame();
        fStatement.setLong(1, pGameState.getId());
        ResultSet resultSet = fStatement.executeQuery();
        while (resultSet.next()) {
          int col = 1;
          col++;  // gameStateId
          Player player = game.getPlayerById(resultSet.getString(col++));
          if (player != null) {
            SeriousInjury injury = seriousInjuryFactory.forName(resultSet.getString(col++));
            if (injury != null) {
              player.addLastingInjury(injury);
            }
          }
        }
        resultSet.close();
      } catch (SQLException sqlE) {
        throw new FantasyFootballException(sqlE);
      }
    }
  }

}
