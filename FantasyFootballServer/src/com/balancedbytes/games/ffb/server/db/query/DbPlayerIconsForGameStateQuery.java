package com.balancedbytes.games.ffb.server.db.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.Player;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.db.DbPlayerIconType;
import com.balancedbytes.games.ffb.server.db.DbStatement;
import com.balancedbytes.games.ffb.server.db.DbStatementId;
import com.balancedbytes.games.ffb.server.db.IDbTablePlayerIcons;

/**
 * 
 * @author Kalimar
 */
public class DbPlayerIconsForGameStateQuery extends DbStatement {
  
  private PreparedStatement fStatement;
  
  public DbPlayerIconsForGameStateQuery(FantasyFootballServer pServer) {
    super(pServer);
  }
  
  public DbStatementId getId() {
    return DbStatementId.PLAYER_ICONS_FOR_GAME_STATE_QUERY;
  }

  public void prepare(Connection pConnection) {
    try {
      StringBuilder sql = new StringBuilder();
      sql.append("SELECT * FROM ").append(IDbTablePlayerIcons.TABLE_NAME).append(" WHERE game_state_id=?");
      fStatement = pConnection.prepareStatement(sql.toString());
    } catch (SQLException sqlE) {
      throw new FantasyFootballException(sqlE);
    }
  }
  
  public void execute(GameState pGameState) {
    if (pGameState != null) {
      try {
        Game game = pGameState.getGame();
        fStatement.setLong(1, pGameState.getId());
        ResultSet resultSet = fStatement.executeQuery();
        while (resultSet.next()) {
          int col = 1;
          col++;  // gameStateId
          Player player = game.getPlayerById(resultSet.getString(col++));
          DbPlayerIconType iconType = DbPlayerIconType.fromTypeString(resultSet.getString(col++));
          String iconUrl = resultSet.getString(col++);
          if (iconType != null) {
            switch (iconType) {
              case HOME_STANDING:
                player.setIconUrlStandingHome(iconUrl);
                break;
              case HOME_MOVING:
                player.setIconUrlMovingHome(iconUrl);
                break;
              case AWAY_STANDING:
                player.setIconUrlStandingAway(iconUrl);
                break;
              case AWAY_MOVING:
                player.setIconUrlMovingAway(iconUrl);
                break;
              case PORTRAIT:
                player.setIconUrlPortrait(iconUrl);
                break;
              case BASE_PATH:
                player.setBaseIconPath(iconUrl);
                break;
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
