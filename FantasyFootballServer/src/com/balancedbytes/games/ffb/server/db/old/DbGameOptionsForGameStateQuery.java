package com.balancedbytes.games.ffb.server.db.old;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.model.GameOptions;
import com.balancedbytes.games.ffb.old.GameOptionFactoryOld;
import com.balancedbytes.games.ffb.old.GameOptionOld;
import com.balancedbytes.games.ffb.option.GameOptionFactory;
import com.balancedbytes.games.ffb.option.GameOptionId;
import com.balancedbytes.games.ffb.option.GameOptionIdFactory;
import com.balancedbytes.games.ffb.option.IGameOption;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.db.DbStatement;
import com.balancedbytes.games.ffb.server.db.DbStatementId;

/**
 * 
 * @author Kalimar
 */
public class DbGameOptionsForGameStateQuery extends DbStatement {
  
  private PreparedStatement fStatement;
  
  public DbGameOptionsForGameStateQuery(FantasyFootballServer pServer) {
    super(pServer);
  }
  
  public DbStatementId getId() {
    return DbStatementId.GAME_OPTIONS_FOR_GAME_STATE_QUERY;
  }
  
  public void prepare(Connection pConnection) {
    try {
      StringBuilder sql = new StringBuilder();
      sql.append("SELECT * FROM ").append(IDbTableGameOptions.TABLE_NAME)
         .append(" WHERE ").append(IDbTableGameOptions.COLUMN_GAME_STATE_ID).append("=?");
      fStatement = pConnection.prepareStatement(sql.toString());
    } catch (SQLException sqlE) {
      throw new FantasyFootballException(sqlE);
    }
  }

  public void execute(GameState pGameState) {
    if (pGameState != null) {
      GameOptionFactoryOld gameOptionFactory = new GameOptionFactoryOld();
      GameOptions options = pGameState.getGame().getOptions();
      GameOptionIdFactory idFactory = new GameOptionIdFactory();
      GameOptionFactory optionFactory = new GameOptionFactory();
      try {
        fStatement.setLong(1, pGameState.getId());
        ResultSet resultSet = fStatement.executeQuery();
        while (resultSet.next()) {
          int col = 1;
          resultSet.getLong(col++); // gameStateId
          GameOptionOld optionName = gameOptionFactory.forName(resultSet.getString(col++));
          if (optionName != null) {
            int optionValue = resultSet.getInt(col++);
            GameOptionId optionId = idFactory.forName(optionName.getName());
            IGameOption gameOption = optionFactory.createGameOption(optionId);
            if (gameOption != null) {
              gameOption.setValue(Integer.toString(optionValue));
              options.addOption(gameOption);
            }
          }
        }
        resultSet.close();
      } catch (SQLException pSqlE) {
        throw new FantasyFootballException(pSqlE);
      }
    }
  }
    
}
