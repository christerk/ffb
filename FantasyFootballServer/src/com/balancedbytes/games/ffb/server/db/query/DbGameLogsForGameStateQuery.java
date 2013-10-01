package com.balancedbytes.games.ffb.server.db.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.bytearray.ByteList;
import com.balancedbytes.games.ffb.net.commands.ServerCommand;
import com.balancedbytes.games.ffb.net.commands.ServerCommandReplay;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.db.DbStatement;
import com.balancedbytes.games.ffb.server.db.DbStatementId;
import com.balancedbytes.games.ffb.server.db.IDbTableGameLogs;

/**
 * 
 * @author Georg Seipler
 */
public class DbGameLogsForGameStateQuery extends DbStatement {
  
  private class QueryResult {

    private byte fSequenceNr;
    private byte[] fCommandBytes;
    
    public QueryResult(ResultSet pResultSet) throws SQLException {
      if (pResultSet != null) {
        int col = 1;
        pResultSet.getLong(col++);  // gameStateId
        pResultSet.getShort(col++);  // commandNr
        fSequenceNr = pResultSet.getByte(col++);
        fCommandBytes = pResultSet.getBytes(col++);
      }
    }

    public byte getSequenceNr() {
      return fSequenceNr;
    }
    
    public byte[] getCommandBytes() {
      return fCommandBytes;
    }
    
  }
  
  private PreparedStatement fStatement;
  
  public DbGameLogsForGameStateQuery(FantasyFootballServer pServer) {
    super(pServer);
  }
  
  public DbStatementId getId() {
    return DbStatementId.GAME_LOGS_FOR_GAME_STATE_QUERY;
  }
  
  public void prepare(Connection pConnection) {
    try {
      StringBuilder sql = new StringBuilder();
      sql.append("SELECT * FROM ").append(IDbTableGameLogs.TABLE_NAME)
         .append(" WHERE ").append(IDbTableGameLogs.COLUMN_GAME_STATE_ID).append("=?")
         .append(" ORDER BY ").append(IDbTableGameLogs.COLUMN_COMMAND_NR).append(",").append(IDbTableGameLogs.COLUMN_SEQUENCE_NR).append(" DESC");
      fStatement = pConnection.prepareStatement(sql.toString());
    } catch (SQLException sqlE) {
      throw new FantasyFootballException(sqlE);
    }
  }
  
  public void execute(GameState pGameState) {
    int nrOfCommands = 0;
    ByteList replayByteList = new ByteList();
    try {
      fStatement.setLong(1, pGameState.getId());
      ResultSet resultSet = fStatement.executeQuery();
      List<QueryResult> queryResults = new ArrayList<QueryResult>();
      while (resultSet.next()) {
        QueryResult queryResult = new QueryResult(resultSet);
        queryResults.add(queryResult);
        if (queryResult.getSequenceNr() == 0) {
          nrOfCommands++;
          for (QueryResult commandPart : queryResults) {
            byte[] commandBytes = commandPart.getCommandBytes();
            for (int i = 0; i < commandBytes.length; i++) {
              replayByteList.addByte(commandBytes[i]);
            }
          }
          queryResults.clear();
        }
      }
      resultSet.close();
    } catch (SQLException pSqlE) {
      throw new FantasyFootballException(pSqlE);
    }
    int maxCommandNr = 0;
    ServerCommandReplay replayCommand = new ServerCommandReplay();
    replayCommand.initFrom(new ByteArray(replayByteList.toBytes()), nrOfCommands);
    for (ServerCommand loggedCommand : replayCommand.getReplayCommands()) {
    	pGameState.getGameLog().add(loggedCommand);
    	maxCommandNr = loggedCommand.getCommandNr(); 
    }
    pGameState.getGameLog().setLastCommitedCommandNr(maxCommandNr);
  }
    
}
