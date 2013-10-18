package com.balancedbytes.games.ffb.server.db.update;

import java.io.IOException;

import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.db.DbStatementId;
import com.balancedbytes.games.ffb.server.db.DbUpdateStatement;
import com.balancedbytes.games.ffb.server.db.DefaultDbUpdateParameter;
import com.eclipsesource.json.JsonObject;

/**
 * 
 * @author Kalimar
 */
public class DbGamesSerializedUpdateParameter extends DefaultDbUpdateParameter {

  private long fId;
  private JsonObject fJsonObject;
  
  public DbGamesSerializedUpdateParameter(GameState pGameState) {
  	if (pGameState != null) {
      fId = pGameState.getId();
      fJsonObject = pGameState.toJsonValue();
    }
  }
  
  public long getId() {
    return fId;
  }
  
  public int length() {
    return fJsonObject.toString().length();
  }
  
  public byte[] deflate() throws IOException {
    return UtilJson.deflate(fJsonObject);
  }

  public DbUpdateStatement getDbUpdateStatement(FantasyFootballServer pServer) {
    return (DbUpdateStatement) pServer.getDbUpdateFactory().getStatement(DbStatementId.GAMES_SERIALIZED_UPDATE);
  }  

}
