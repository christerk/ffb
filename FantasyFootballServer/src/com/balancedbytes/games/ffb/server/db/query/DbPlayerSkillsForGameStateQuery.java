package com.balancedbytes.games.ffb.server.db.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.SkillFactory;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.db.DbStatement;
import com.balancedbytes.games.ffb.server.db.DbStatementId;
import com.balancedbytes.games.ffb.server.db.IDbTablePlayerSkills;

/**
 * 
 * @author Kalimar
 */
public class DbPlayerSkillsForGameStateQuery extends DbStatement {
  
  private PreparedStatement fStatement;
  
  public DbPlayerSkillsForGameStateQuery(FantasyFootballServer pServer) {
    super(pServer);
  }
  
  public DbStatementId getId() {
    return DbStatementId.PLAYER_SKILLS_FOR_GAME_STATE_QUERY;
  }

  public void prepare(Connection pConnection) {
    try {
      StringBuilder sql = new StringBuilder();
      sql.append("SELECT * FROM ").append(IDbTablePlayerSkills.TABLE_NAME).append(" WHERE game_state_id=?");
      fStatement = pConnection.prepareStatement(sql.toString());
    } catch (SQLException sqlE) {
      throw new FantasyFootballException(sqlE);
    }
  }
  
  public void execute(GameState pGameState) {
    if (pGameState != null) {
      try {
        Game game = pGameState.getGame();
        SkillFactory skillFactory = new SkillFactory();
        fStatement.setLong(1, pGameState.getId());
        ResultSet resultSet = fStatement.executeQuery();
        while (resultSet.next()) {
          int col = 1;
          col++;  // gameStateId
          Player player = game.getPlayerById(resultSet.getString(col++));
          if (player != null) {
            Skill skill = skillFactory.forName(resultSet.getString(col++));
            if (skill != null) {
              player.addSkill(skill);
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
