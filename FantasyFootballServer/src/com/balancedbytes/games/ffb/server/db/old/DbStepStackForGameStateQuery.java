package com.balancedbytes.games.ffb.server.db.old;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.bytearray.ByteArray;
import com.balancedbytes.games.ffb.server.DebugLog;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerLogLevel;
import com.balancedbytes.games.ffb.server.db.DbStatement;
import com.balancedbytes.games.ffb.server.db.DbStatementId;
import com.balancedbytes.games.ffb.server.step.IStep;
import com.balancedbytes.games.ffb.server.step.StepFactory;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepIdFactory;
import com.balancedbytes.games.ffb.util.ArrayTool;

/**
 * 
 * @author Kalimar
 */
public class DbStepStackForGameStateQuery extends DbStatement {
  
  private class QueryResult {

    private short fStackIndex;
    private byte[] fStepBytes;
    
    public QueryResult(ResultSet pResultSet) throws SQLException {
      if (pResultSet != null) {
        int col = 1;
        pResultSet.getLong(col++);  // gameStateId
        fStackIndex = pResultSet.getShort(col++);
        fStepBytes = pResultSet.getBytes(col++);
      }
    }

    public short getStackIndex() {
			return fStackIndex;
		}
    
    public byte[] getStepBytes() {
			return fStepBytes;
		}
    
  }
  
  private PreparedStatement fStatement;
  
  public DbStepStackForGameStateQuery(FantasyFootballServer pServer) {
    super(pServer);
  }
  
  public DbStatementId getId() {
    return DbStatementId.STEP_STACK_FOR_GAME_STATE_QUERY;
  }
  
  public void prepare(Connection pConnection) {
    try {
      StringBuilder sql = new StringBuilder();
      sql.append("SELECT * FROM ").append(IDbTableStepStack.TABLE_NAME)
         .append(" WHERE ").append(IDbTableStepStack.COLUMN_GAME_STATE_ID).append("=?")
         .append(" ORDER BY ").append(IDbTableStepStack.COLUMN_STACK_INDEX).append(" DESC");
      fStatement = pConnection.prepareStatement(sql.toString());
    } catch (SQLException sqlE) {
      throw new FantasyFootballException(sqlE);
    }
  }
  
  public void execute(GameState pGameState) {
    if (pGameState != null) {
      pGameState.getStepStack().clear();
      try {
        fStatement.setLong(1, pGameState.getId());
        ResultSet resultSet = fStatement.executeQuery();
        while (resultSet.next()) {
          QueryResult queryResult = new QueryResult(resultSet);
          if (ArrayTool.isProvided(queryResult.getStepBytes())) {
          	if (queryResult.getStackIndex() > 0) {
          		pGameState.getStepStack().push(fromBytes(pGameState, queryResult.getStepBytes()));
          	} else {
          		pGameState.setCurrentStep(fromBytes(pGameState, queryResult.getStepBytes()));
          	}
          }
        }
        resultSet.close();
      } catch (SQLException pSqlE) {
        throw new FantasyFootballException(pSqlE);
      }
    	// debugging
      DebugLog debugLog = pGameState.getServer().getDebugLog();
      if (pGameState.getCurrentStep() != null) {
      	debugLog.log(IServerLogLevel.DEBUG, "read stack[0] " + pGameState.getCurrentStep().getId() + " (" + pGameState.getCurrentStep().getLabel() + ")");
      } else {
      	debugLog.log(IServerLogLevel.DEBUG, "read stack[0] No current Step");
      }
    	IStep[] steps = pGameState.getStepStack().toArray();
    	for (int j = 0; j < steps.length; j++) {
    		debugLog.log(IServerLogLevel.DEBUG, "read stack[" + (j + 1) + "] " + steps[j].getId() + " (" + steps[j].getLabel() + ")");
    	}
    }
  }
  
  private IStep fromBytes(GameState pGameState, byte[] pStepBytes) {
  	ByteArray byteArray = new ByteArray(pStepBytes);
		StepId stepId = new StepIdFactory().forId(byteArray.getSmallInt(0));
		IStep step = new StepFactory(pGameState).forStepId(stepId);
		step.initFrom(byteArray);
		return step;
  }
    
}
