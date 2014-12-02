package com.balancedbytes.games.ffb.server.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.db.delete.DbGamesInfoDelete;
import com.balancedbytes.games.ffb.server.db.delete.DbGamesSerializedDelete;
import com.balancedbytes.games.ffb.server.db.delete.DbPlayerMarkersDelete;
import com.balancedbytes.games.ffb.server.db.delete.DbTeamSetupsDelete;
import com.balancedbytes.games.ffb.server.db.delete.DbUserSettingsDelete;
import com.balancedbytes.games.ffb.server.db.insert.DbGamesInfoInsert;
import com.balancedbytes.games.ffb.server.db.insert.DbGamesSerializedInsert;
import com.balancedbytes.games.ffb.server.db.insert.DbPlayerMarkersInsert;
import com.balancedbytes.games.ffb.server.db.insert.DbTeamSetupsInsert;
import com.balancedbytes.games.ffb.server.db.insert.DbUserSettingsInsert;
import com.balancedbytes.games.ffb.server.db.old.DbActingPlayersDelete;
import com.balancedbytes.games.ffb.server.db.old.DbDialogsDelete;
import com.balancedbytes.games.ffb.server.db.old.DbFieldModelsDelete;
import com.balancedbytes.games.ffb.server.db.old.DbGameLogsDelete;
import com.balancedbytes.games.ffb.server.db.old.DbGameOptionsDelete;
import com.balancedbytes.games.ffb.server.db.old.DbGameStatesDelete;
import com.balancedbytes.games.ffb.server.db.old.DbInducementsDelete;
import com.balancedbytes.games.ffb.server.db.old.DbPlayerDelete;
import com.balancedbytes.games.ffb.server.db.old.DbPlayerIconsDelete;
import com.balancedbytes.games.ffb.server.db.old.DbPlayerInjuriesDelete;
import com.balancedbytes.games.ffb.server.db.old.DbPlayerResultsDelete;
import com.balancedbytes.games.ffb.server.db.old.DbPlayerSkillsDelete;
import com.balancedbytes.games.ffb.server.db.old.DbPlayersDelete;
import com.balancedbytes.games.ffb.server.db.old.DbStepStackDelete;
import com.balancedbytes.games.ffb.server.db.old.DbTeamResultsDelete;
import com.balancedbytes.games.ffb.server.db.old.DbTeamsDelete;
import com.balancedbytes.games.ffb.server.db.old.DbTurnDataDelete;
import com.balancedbytes.games.ffb.server.db.update.DbGamesInfoUpdate;
import com.balancedbytes.games.ffb.server.db.update.DbGamesSerializedUpdate;

/**
 * 
 * @author Kalimar
 */
public class DbUpdateFactory implements IDbStatementFactory {

  private DbConnectionManager fDbConnectionManager;
  private Connection fDbConnection;
  private Map<DbStatementId, DbUpdateStatement> fStatementById;

  public DbUpdateFactory(DbConnectionManager pDbConnectionManager) {

  	fDbConnectionManager = pDbConnectionManager;
    fStatementById = new HashMap<DbStatementId, DbUpdateStatement>();

    register(new DbGamesInfoInsert(getServer()));
    register(new DbGamesSerializedInsert(getServer()));
    register(new DbTeamSetupsInsert(getServer()));
    register(new DbUserSettingsInsert(getServer()));
    register(new DbUserSettingsDelete(getServer()));
    register(new DbPlayerMarkersInsert(getServer()));    
    register(new DbPlayerMarkersDelete(getServer()));
 
    register(new DbGamesInfoUpdate(getServer()));
    register(new DbGamesSerializedUpdate(getServer()));

    register(new DbActingPlayersDelete(getServer()));
    register(new DbDialogsDelete(getServer()));
    register(new DbFieldModelsDelete(getServer()));
    register(new DbGameLogsDelete(getServer()));
    register(new DbGameOptionsDelete(getServer()));
    register(new DbGamesInfoDelete(getServer()));
    register(new DbGamesSerializedDelete(getServer()));
    register(new DbGameStatesDelete(getServer()));
    register(new DbInducementsDelete(getServer()));
    register(new DbPlayerDelete(getServer()));
    register(new DbPlayerIconsDelete(getServer()));
    register(new DbPlayerInjuriesDelete(getServer()));
    register(new DbPlayerResultsDelete(getServer()));
    register(new DbPlayersDelete(getServer()));
    register(new DbPlayerSkillsDelete(getServer()));
    register(new DbStepStackDelete(getServer()));
    register(new DbTeamResultsDelete(getServer()));
    register(new DbTeamsDelete(getServer()));
    register(new DbTeamSetupsDelete(getServer()));
    register(new DbTurnDataDelete(getServer()));
  }

  public DbStatement getStatement(DbStatementId pStatementId) {
    return fStatementById.get(pStatementId);
  }

  private void register(DbUpdateStatement pStatement) {
    fStatementById.put(pStatement.getId(), pStatement);
  }

  public void prepareStatements() throws SQLException {
    fDbConnection = getDbConnectionManager().openDbConnection();
    fDbConnection.setAutoCommit(false);
    Iterator<DbUpdateStatement> statementIterator = fStatementById.values().iterator();
    while (statementIterator.hasNext()) {
      DbStatement statement = statementIterator.next();
      statement.prepare(fDbConnection);
    }
  }

  public void commit() throws SQLException {
    fDbConnection.commit();
  }
  
  public void rollback() throws SQLException {
  	fDbConnection.rollback();
  }

  public void closeDbConnection() throws SQLException {
    getDbConnectionManager().closeDbConnection(fDbConnection);
  }

  public DbConnectionManager getDbConnectionManager() {
    return fDbConnectionManager;
  }
  
  public FantasyFootballServer getServer() {
    return getDbConnectionManager().getServer();
  }
  
}
