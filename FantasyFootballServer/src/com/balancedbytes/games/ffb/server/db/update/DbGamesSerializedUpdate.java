package com.balancedbytes.games.ffb.server.db.update;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.IServerLogLevel;
import com.balancedbytes.games.ffb.server.db.DbStatementId;
import com.balancedbytes.games.ffb.server.db.DbUpdateStatement;
import com.balancedbytes.games.ffb.server.db.IDbTableGamesSerialized;
import com.balancedbytes.games.ffb.server.db.IDbUpdateParameter;
import com.balancedbytes.games.ffb.util.StringTool;

/**
 * 
 * @author Kalimar
 */
public class DbGamesSerializedUpdate extends DbUpdateStatement {

  private PreparedStatement fStatement;

  public DbGamesSerializedUpdate(FantasyFootballServer pServer) {
    super(pServer);
  }

  public DbStatementId getId() {
    return DbStatementId.GAMES_SERIALIZED_UPDATE;
  }

  public void prepare(Connection pConnection) {
    try {
      StringBuilder sql = new StringBuilder();
      sql.append("UPDATE ").append(IDbTableGamesSerialized.TABLE_NAME).append(" SET ");
      sql.append(IDbTableGamesSerialized.COLUMN_SERIALIZED).append("=?"); // 2
      sql.append(" WHERE ").append(IDbTableGamesSerialized.COLUMN_ID).append("=?"); // 1
      fStatement = pConnection.prepareStatement(sql.toString());
    } catch (SQLException sqlE) {
      throw new FantasyFootballException(sqlE);
    }
  }

  public int execute(IDbUpdateParameter pUpdateParameter) throws SQLException {
    return fillDbStatement(pUpdateParameter, true).executeUpdate();
  }

  public String toString(IDbUpdateParameter pUpdateParameter) throws SQLException {
    return fillDbStatement(pUpdateParameter, false).toString();
  }

  private PreparedStatement fillDbStatement(IDbUpdateParameter pUpdateParameter, boolean pFillBlob) throws SQLException {
    DbGamesSerializedUpdateParameter parameter = (DbGamesSerializedUpdateParameter) pUpdateParameter;
    fStatement.clearParameters();
    int col = 1;
    try {
      byte[] blobData = pFillBlob ? parameter.deflate() : new byte[0];
      if (pFillBlob && getServer().getDebugLog().isLogging(IServerLogLevel.TRACE)) {
        int newLength = blobData.length;
        int oldLength = parameter.length();
        StringBuilder logMsg = new StringBuilder();
        logMsg.append("updating compressed serialized game of ").append(StringTool.formatThousands(newLength)).append(" bytes");
        logMsg.append(" (").append(Math.round((double) newLength * 100 / oldLength)).append("%");
        logMsg.append(" of original ").append(StringTool.formatThousands(oldLength)).append(" bytes)");
        getServer().getDebugLog().log(IServerLogLevel.TRACE, parameter.getId(), logMsg.toString());
//        String currentStepName = (parameter.getGameState().getCurrentStep() != null) ? parameter.getGameState().getCurrentStep().getId().getName() : "null";
//        getServer().getDebugLog().log(IServerLogLevel.TRACE, StringTool.bind("saved CurrentStep $1", currentStepName));
      }
      fStatement.setBinaryStream(col++, new ByteArrayInputStream(blobData), blobData.length);
    } catch (IOException pIoException) {
      throw new SQLException("Error on compressing game", pIoException);
    }
    fStatement.setLong(col++, parameter.getId());
    return fStatement;
  }

}
