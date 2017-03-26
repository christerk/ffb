package com.balancedbytes.games.ffb.server.db.insert;

import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.db.DbStatementId;
import com.balancedbytes.games.ffb.server.db.DbUpdateStatement;
import com.balancedbytes.games.ffb.server.db.IDbUpdateWithGameState;
import com.balancedbytes.games.ffb.server.db.update.DbGamesSerializedUpdateParameter;

/**
 * 
 * @author Kalimar
 */
public class DbGamesSerializedInsertParameter extends DbGamesSerializedUpdateParameter implements IDbUpdateWithGameState {

  public DbGamesSerializedInsertParameter(GameState pGameState) {
    super(pGameState);
  }
   
  @Override
  public DbUpdateStatement getDbUpdateStatement(FantasyFootballServer pServer) {
    return (DbUpdateStatement) pServer.getDbUpdateFactory().getStatement(DbStatementId.GAMES_SERIALIZED_INSERT);
  }

}
