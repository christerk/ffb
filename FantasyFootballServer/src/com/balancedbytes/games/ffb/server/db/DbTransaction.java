package com.balancedbytes.games.ffb.server.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.IServerLogLevel;

/**
 * 
 * @author Kalimar
 */
public class DbTransaction implements IDbUpdateParameter {

  private int fUpdatedRows;
  private List<IDbUpdateParameter> fDbUpdateParameters;

  public DbTransaction() {
    fDbUpdateParameters = new ArrayList<IDbUpdateParameter>();
  }

  public void add(IDbUpdateParameter pDbUpdateParameter) {
    if (pDbUpdateParameter != null) {
      fDbUpdateParameters.add(pDbUpdateParameter);
    }
  }

  public void add(IDbUpdateParameterList pDbUpdateParameterList) {
    if (pDbUpdateParameterList != null) {
      for (IDbUpdateParameter parameter : pDbUpdateParameterList.getParameters()) {
        fDbUpdateParameters.add(parameter);
      }
    }
  }

  public IDbUpdateParameter[] getDbUpdateParameters() {
    return fDbUpdateParameters.toArray(new IDbUpdateParameter[fDbUpdateParameters.size()]);
  }

  public int size() {
    return fDbUpdateParameters.size();
  }

  public void executeUpdate(FantasyFootballServer pServer) {
    fUpdatedRows = 0;
    boolean doCommit = true;
    for (int i = 0; i < fDbUpdateParameters.size(); i++) {
      IDbUpdateParameter dbUpdateParameter = fDbUpdateParameters.get(i);
      try {
        dbUpdateParameter.executeUpdate(pServer);
        fUpdatedRows += dbUpdateParameter.getUpdatedRows();
      } catch (SQLException pSqlException) {
        doCommit = false;
        pServer.getDebugLog().log(pSqlException);
        pServer.getDebugLog().log(IServerLogLevel.ERROR, "*** DbTransaction Content ***");
        for (int j = 0; j <= i; j++) {
          try {
            pServer.getDebugLog().log(IServerLogLevel.ERROR, fDbUpdateParameters.get(j).getDbUpdateStatement(pServer).toString(fDbUpdateParameters.get(j)));
          } catch (SQLException pSqlException2) {
            // just don't log
          }
        }
        break;
      }
    }
    try {
      if (doCommit) {
        pServer.getDbUpdateFactory().commit();
      } else {
        fUpdatedRows = 0;
        pServer.getDbUpdateFactory().rollback();
      }
    } catch (SQLException pCommitException) {
      pServer.getDebugLog().log(pCommitException);
    }
  }

  public int getUpdatedRows() {
    return fUpdatedRows;
  }

  public DbUpdateStatement getDbUpdateStatement(FantasyFootballServer pServer) {
    return null;
  }

  public IDbUpdateParameter[] getParameters() {
    return new DbTransaction[] { this };
  }

}
