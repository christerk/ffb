package com.balancedbytes.games.ffb.server.db.old;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.IDialogParameter;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.dialog.DialogId;
import com.balancedbytes.games.ffb.dialog.DialogIdFactory;
import com.balancedbytes.games.ffb.dialog.DialogParameterFactory;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.db.DbStatement;
import com.balancedbytes.games.ffb.server.db.DbStatementId;

/**
 * 
 * @author Kalimar
 */
public class DbDialogsForGameStateQuery extends DbStatement {
  
  private class QueryResult {

    private DialogId fDialogId;
    private byte[] fParameterBytes;
    
    public QueryResult(ResultSet pResultSet) throws SQLException {
      if (pResultSet != null) {
        int col = 1;
        pResultSet.getLong(col++);  // gameStateId
        fDialogId = new DialogIdFactory().forId(pResultSet.getByte(col++));
        pResultSet.getByte(col++);  // sequenceNr
        fParameterBytes = pResultSet.getBytes(col++);
      }
    }
    
    public DialogId getDialogId() {
      return fDialogId;
    }
    
    public byte[] getParameterBytes() {
      return fParameterBytes;
    }
    
  }
  
  private PreparedStatement fStatement;
  
  public DbDialogsForGameStateQuery(FantasyFootballServer pServer) {
    super(pServer);
  }
  
  public DbStatementId getId() {
    return DbStatementId.DIALOGS_FOR_GAME_STATE_QUERY;
  }
  
  public void prepare(Connection pConnection) {
    try {
      StringBuilder sql = new StringBuilder();
      sql.append("SELECT * FROM ").append(IDbTableGameStates.TABLE_NAME)
         .append(" WHERE ").append(IDbTableDialogs.COLUMN_GAME_STATE_ID).append("=?")
         .append(" ORDER BY ").append(IDbTableDialogs.COLUMN_DIALOG_ID).append(",").append(IDbTableDialogs.COLUMN_SEQUENCE_NR).append(" DESC");
      fStatement = pConnection.prepareStatement(sql.toString());
    } catch (SQLException sqlE) {
      throw new FantasyFootballException(sqlE);
    }
  }

  public void execute(GameState pGameState) {
    if (pGameState != null) {
      DialogId dialogId = null;
      ByteList parameterByteList = new ByteList();
      try {
        fStatement.setLong(1, pGameState.getId());
        ResultSet resultSet = fStatement.executeQuery();
        while (resultSet.next()) {
          QueryResult queryResult = new QueryResult(resultSet);
          dialogId = queryResult.getDialogId();
          byte[] parameterBytes = queryResult.getParameterBytes();
          for (int i = 0; i < parameterBytes.length; i++) {
            parameterByteList.addByte(parameterBytes[i]);
          }
        }
        resultSet.close();
      } catch (SQLException pSqlE) {
        throw new FantasyFootballException(pSqlE);
      }
      IDialogParameter dialogParameter = new DialogParameterFactory().createDialogParameter(dialogId);
      if (dialogParameter != null) {
        dialogParameter.initFrom(new ByteArray(parameterByteList.toBytes()));
      }
      pGameState.getGame().setDialogParameter(dialogParameter);
    }
  }
    
}
