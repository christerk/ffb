package com.balancedbytes.games.ffb.server.db.update;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import com.balancedbytes.games.ffb.bytearray.ByteList;
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
  private byte[] fSerialized;
  
  public DbGamesSerializedUpdateParameter(GameState pGameState) {
  	if (pGameState != null) {
    	fId = pGameState.getId();
      ByteList byteList = new ByteList();
      pGameState.addTo(byteList);
      fSerialized = byteList.toBytes();
    }
  }
  
  public long getId() {
    return fId;
  }

  public byte[] getSerialized() {
	  return fSerialized;
  }
  
  public byte[] compress() throws IOException {
  	
  	ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
  	GZIPOutputStream gzipOut = new GZIPOutputStream(byteArrayOut);
  	
  	ByteArrayInputStream byteArrayIn = new ByteArrayInputStream(getSerialized());

  	// Transfer bytes from the input stream to the GZIP output stream
    byte[] buf = new byte[1024];
    int len;
    while ((len = byteArrayIn.read(buf)) > 0) {
      gzipOut.write(buf, 0, len);
    }

    byteArrayIn.close();
    gzipOut.close();
    
    return byteArrayOut.toByteArray();

  }  

  public DbUpdateStatement getDbUpdateStatement(FantasyFootballServer pServer) {
    return (DbUpdateStatement) pServer.getDbUpdateFactory().getStatement(DbStatementId.GAMES_SERIALIZED_UPDATE);
  }  

}
