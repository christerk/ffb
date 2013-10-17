package com.balancedbytes.games.ffb.server.db.old;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.IServerProperty;
import com.balancedbytes.games.ffb.server.db.DbStatement;
import com.balancedbytes.games.ffb.server.db.DbStatementId;
import com.balancedbytes.games.ffb.server.db.IDbStatementFactory;
import com.balancedbytes.games.ffb.server.db.delete.DbGamesInfoDelete;
import com.balancedbytes.games.ffb.server.db.delete.DbGamesSerializedDelete;
import com.balancedbytes.games.ffb.server.db.insert.DbGamesInfoInsert;
import com.balancedbytes.games.ffb.server.db.insert.DbGamesSerializedInsert;

/**
 * 
 * @author Georg Seipler
 */
public class DbConversionFactory implements IDbStatementFactory {
  
  private FantasyFootballServer fServer;
  
  private Connection fDbConnection;
  
  private Map<DbStatementId, DbStatement> fStatementById;
  
  public DbConversionFactory(FantasyFootballServer pServer) {
    
    fServer = pServer;
    fStatementById = new HashMap<DbStatementId, DbStatement>();
    
    // old queries
    register(new DbFieldModelsForGameStateQueryOld(fServer));
    register(new DbDialogsForGameStateQueryOld(fServer));
    register(new DbActingPlayersForGameStateQueryOld(fServer));
    register(new DbGameStatesQueryOld(fServer));
    register(new DbTurnDataForGameStateQueryOld(fServer));
    register(new DbTeamsForGameStateQueryOld(fServer));
    register(new DbPlayersForGameStateQueryOld(fServer));
    register(new DbPlayerSkillsForGameStateQueryOld(fServer));
    register(new DbPlayerInjuriesForGameStateQueryOld(fServer));
    register(new DbPlayerIconsForGameStateQueryOld(fServer));
    register(new DbTeamResultsForGameStateQueryOld(fServer));
    register(new DbPlayerResultsForGameStateQueryOld(fServer));
    register(new DbInducementsForGameStateQueryOld(fServer));
    register(new DbGameOptionsForGameStateQueryOld(fServer));
    register(new DbGameStatesQueryFinishedGamesOld(fServer));
    register(new DbGameLogsForGameStateQueryOld(fServer));
    
    // new updates
    register(new DbGamesInfoInsert(pServer));
    register(new DbGamesSerializedInsert(pServer));
    register(new DbGamesInfoDelete(pServer));
    register(new DbGamesSerializedDelete(pServer));
    
  }
  
  public DbStatement getStatement(DbStatementId pStatementId) {
    return fStatementById.get(pStatementId);
  }
  
  private void register(DbStatement pStatement) {
    fStatementById.put(pStatement.getId(), pStatement);
  }
  
  private Connection openDbConnection() throws SQLException {
    Connection connection = DriverManager.getConnection(
      getServer().getProperty(IServerProperty.DB_OLD_URL),
      getServer().getProperty(IServerProperty.DB_OLD_USER),
      getServer().getProperty(IServerProperty.DB_OLD_PASSWORD)
    );
    connection.setAutoCommit(false);
    return connection;
  }
  
  public void prepareStatements() throws SQLException {
    fDbConnection = openDbConnection();
    fDbConnection.setAutoCommit(true);
    Iterator<DbStatement> statementIterator = fStatementById.values().iterator();
    while (statementIterator.hasNext()) {
      DbStatement statement = statementIterator.next();
      statement.prepare(fDbConnection);
    }
  }
  
  public void closeDbConnection() throws SQLException {
    fDbConnection.close();
  }
  
  public FantasyFootballServer getServer() {
    return fServer;
  }
  
  public void commit() throws SQLException {
    fDbConnection.commit();
  }
  
  public void rollback() throws SQLException {
    fDbConnection.rollback();
  }

}
