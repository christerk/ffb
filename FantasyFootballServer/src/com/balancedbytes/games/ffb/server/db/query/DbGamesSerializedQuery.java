package com.balancedbytes.games.ffb.server.db.query;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.zip.GZIPInputStream;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameCacheMode;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerLogLevel;
import com.balancedbytes.games.ffb.server.db.DbStatement;
import com.balancedbytes.games.ffb.server.db.DbStatementId;
import com.balancedbytes.games.ffb.server.db.IDbTableGamesSerialized;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.StringTool;

/**
 * 
 * @author Kalimar
 */
public class DbGamesSerializedQuery extends DbStatement {

  private PreparedStatement fStatement;

  public DbGamesSerializedQuery(FantasyFootballServer pServer) {
    super(pServer);
  }

  public DbStatementId getId() {
    return DbStatementId.GAMES_SERIALIZED_QUERY;
  }

  public void prepare(Connection pConnection) {
    try {
      StringBuilder sql = new StringBuilder();
      sql.append("SELECT ").append(IDbTableGamesSerialized.COLUMN_SERIALIZED);
      sql.append(" FROM ").append(IDbTableGamesSerialized.TABLE_NAME).append(" WHERE id=?");
      fStatement = pConnection.prepareStatement(sql.toString());
    } catch (SQLException sqlE) {
      throw new FantasyFootballException(sqlE);
    }
  }

  public GameState execute(FantasyFootballServer pServer, long pGameStateId) {
    GameState gameState = null;
    try {
      fStatement.setLong(1, pGameStateId);
      ResultSet resultSet = fStatement.executeQuery();
      while (resultSet.next()) {
      	Blob blob = resultSet.getBlob(1);
      	byte[] gameStateBytes = readByteArray(blob.getBinaryStream());
      	if (ArrayTool.isProvided(gameStateBytes)) {
      		StringBuilder logMsg = new StringBuilder();
      		logMsg.append("reading serialized game of ").append(StringTool.formatThousands(gameStateBytes.length)).append(" bytes");
      		getServer().getDebugLog().log(IServerLogLevel.TRACE, pGameStateId, logMsg.toString());
          gameState = new GameState(pServer);
          gameState.initFrom(new ByteArray(gameStateBytes));
      		pServer.getGameCache().add(gameState, GameCacheMode.LOAD_GAME);
      	}
      }
      resultSet.close();
    } catch (IOException pIOException) {
      throw new FantasyFootballException(pIOException);
    } catch (SQLException pSqlException) {
      throw new FantasyFootballException(pSqlException);
    }
    return gameState;
  }
  
  private byte[] readByteArray(InputStream pBlobBinaryStream) throws IOException {
  	ByteArrayOutputStream baos = new ByteArrayOutputStream();
  	GZIPInputStream gzipIn = new GZIPInputStream(pBlobBinaryStream);
    byte[] buf = new byte[1024];
    int n = 0;
    while ((n=gzipIn.read(buf))>=0) {
    	baos.write(buf, 0, n);
    }
    gzipIn.close();
    return baos.toByteArray(); 
  }
  
}
