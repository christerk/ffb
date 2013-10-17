package com.balancedbytes.games.ffb.server.db.old;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.balancedbytes.games.ffb.Card;
import com.balancedbytes.games.ffb.CardFactory;
import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.Inducement;
import com.balancedbytes.games.ffb.InducementType;
import com.balancedbytes.games.ffb.InducementTypeFactory;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.InducementSet;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.db.DbStatement;
import com.balancedbytes.games.ffb.server.db.DbStatementId;

/**
 * 
 * @author Kalimar
 */
public class DbInducementsForGameStateQuery extends DbStatement {
	
	public static int CARD_USAGE_AVAILABLE = 1;
	public static int CARD_USAGE_ACTIVE = 2;
	public static int CARD_USAGE_DEACTIVATED = 3;
    
  private PreparedStatement fStatement;
  
  public DbInducementsForGameStateQuery(FantasyFootballServer pServer) {
    super(pServer);
  }
  
  public DbStatementId getId() {
    return DbStatementId.INDUCEMENTS_FOR_GAME_STATE_QUERY;
  }
  
  public void prepare(Connection pConnection) {
    try {
      StringBuilder sql = new StringBuilder();
      sql.append("SELECT * FROM ").append(IDbTableInducements.TABLE_NAME).append(" WHERE ").append(IDbTableInducements.COLUMN_GAME_STATE_ID).append("=?");
      fStatement = pConnection.prepareStatement(sql.toString());
    } catch (SQLException sqlE) {
      throw new FantasyFootballException(sqlE);
    }
  }
  
  public void execute(GameState pGameState) {
    try {
      Game game = pGameState.getGame();
      game.getTurnDataHome().getInducementSet().clear();
      game.getTurnDataAway().getInducementSet().clear();
      fStatement.setLong(1, pGameState.getId());
      ResultSet resultSet = fStatement.executeQuery();
      while (resultSet.next()) {
        int col = 1;
        col++;  // gameStateId
        boolean homeData = resultSet.getBoolean(col++);
        InducementType type = new InducementTypeFactory().forId(resultSet.getByte(col++));
        int value = resultSet.getInt(col++);
        byte uses = resultSet.getByte(col++);
        InducementSet inducementSet = homeData ? game.getTurnDataHome().getInducementSet() : game.getTurnDataAway().getInducementSet();
        addInducement(inducementSet, type, value, uses);
      }
      resultSet.close();
    } catch (SQLException pSqlE) {
      throw new FantasyFootballException(pSqlE);
    }
  }
  
  private void addInducement(InducementSet pInducementSet, InducementType pType, int pValue, int pUses) {
  	if (pType != null) {
      Inducement inducement = new Inducement(pType, pValue);
      inducement.setUses(pUses);
      pInducementSet.addInducement(inducement);
  	} else {
  		Card card = new CardFactory().forId(pValue);
			pInducementSet.addAvailableCard(card);
  		if ((pUses == CARD_USAGE_ACTIVE) || (pUses == CARD_USAGE_DEACTIVATED)) {
  			pInducementSet.activateCard(card);
    		if (pUses == CARD_USAGE_DEACTIVATED) {
    			pInducementSet.deactivateCard(card);
    		}
  		}
  	}
  }
    
}
