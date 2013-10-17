package com.balancedbytes.games.ffb.server.db.update;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.zip.GZIPOutputStream;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.json.Base64;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.db.DbStatementId;
import com.balancedbytes.games.ffb.server.db.DbUpdateStatement;
import com.balancedbytes.games.ffb.server.db.DefaultDbUpdateParameter;

/**
 * 
 * @author Kalimar
 */
public class DbGamesSerializedUpdateParameter extends DefaultDbUpdateParameter {

  private long fId;
  private String fSerialized;
  
  public DbGamesSerializedUpdateParameter(GameState pGameState) {
  	if (pGameState != null) {
    	fId = pGameState.getId();
      fSerialized = serialize(pGameState);
    }
  }
  
  public long getId() {
    return fId;
  }

  public String getSerialized() {
	  return fSerialized;
  }
  
  private String serialize(GameState pGameState) {
    if (pGameState == null) {
      return null;
    }
    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    try {
      OutputStreamWriter gzipOut = new OutputStreamWriter(new GZIPOutputStream(byteOut));
      gzipOut.write(pGameState.toJsonValue().toString());
      gzipOut.close();
    } catch (IOException pIoException) {
      throw new FantasyFootballException(pIoException);
    }
    return Base64.encodeToString(byteOut.toByteArray(), false);
  }

  public DbUpdateStatement getDbUpdateStatement(FantasyFootballServer pServer) {
    return (DbUpdateStatement) pServer.getDbUpdateFactory().getStatement(DbStatementId.GAMES_SERIALIZED_UPDATE);
  }  

}
