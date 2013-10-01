package com.balancedbytes.games.ffb.server.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.db.delete.DbActingPlayersDelete;
import com.balancedbytes.games.ffb.server.db.delete.DbDialogsDelete;
import com.balancedbytes.games.ffb.server.db.delete.DbFieldModelsDelete;
import com.balancedbytes.games.ffb.server.db.delete.DbGameLogsDelete;
import com.balancedbytes.games.ffb.server.db.delete.DbGameOptionsDelete;
import com.balancedbytes.games.ffb.server.db.delete.DbGameStatesDelete;
import com.balancedbytes.games.ffb.server.db.delete.DbGamesInfoDelete;
import com.balancedbytes.games.ffb.server.db.delete.DbGamesSerializedDelete;
import com.balancedbytes.games.ffb.server.db.delete.DbInducementsDelete;
import com.balancedbytes.games.ffb.server.db.delete.DbPlayerDelete;
import com.balancedbytes.games.ffb.server.db.delete.DbPlayerIconsDelete;
import com.balancedbytes.games.ffb.server.db.delete.DbPlayerInjuriesDelete;
import com.balancedbytes.games.ffb.server.db.delete.DbPlayerResultsDelete;
import com.balancedbytes.games.ffb.server.db.delete.DbPlayerSkillsDelete;
import com.balancedbytes.games.ffb.server.db.delete.DbPlayersDelete;
import com.balancedbytes.games.ffb.server.db.delete.DbStepStackDelete;
import com.balancedbytes.games.ffb.server.db.delete.DbTeamResultsDelete;
import com.balancedbytes.games.ffb.server.db.delete.DbTeamSetupsDelete;
import com.balancedbytes.games.ffb.server.db.delete.DbTeamsDelete;
import com.balancedbytes.games.ffb.server.db.delete.DbTurnDataDelete;
import com.balancedbytes.games.ffb.server.db.delete.DbUserSettingsDelete;
import com.balancedbytes.games.ffb.server.db.insert.DbGamesInfoInsert;
import com.balancedbytes.games.ffb.server.db.insert.DbGamesSerializedInsert;
import com.balancedbytes.games.ffb.server.db.insert.DbTeamSetupsInsert;
import com.balancedbytes.games.ffb.server.db.insert.DbUserSettingsInsert;
import com.balancedbytes.games.ffb.server.db.update.DbGamesInfoUpdate;
import com.balancedbytes.games.ffb.server.db.update.DbGamesSerializedUpdate;

/**
 * 
 * @author Kalimar
 */
public class DbUpdateFactory implements IDbStatementFactory {

  private FantasyFootballServer fServer;
  private Connection fDbConnection;
  private Map<DbStatementId, DbUpdateStatement> fStatementById;

  public DbUpdateFactory(FantasyFootballServer pServer) {

  	fServer = pServer;
    fStatementById = new HashMap<DbStatementId, DbUpdateStatement>();

    register(new DbGamesInfoInsert(pServer));
    register(new DbGamesSerializedInsert(pServer));
    register(new DbTeamSetupsInsert(pServer));
    register(new DbUserSettingsInsert(pServer));
    
    register(new DbGamesInfoUpdate(pServer));
    register(new DbGamesSerializedUpdate(pServer));

    register(new DbActingPlayersDelete(pServer));
    register(new DbDialogsDelete(pServer));
    register(new DbFieldModelsDelete(pServer));
    register(new DbGameLogsDelete(pServer));
    register(new DbGameOptionsDelete(pServer));
    register(new DbGamesInfoDelete(pServer));
    register(new DbGamesSerializedDelete(pServer));
    register(new DbGameStatesDelete(pServer));
    register(new DbInducementsDelete(pServer));
    register(new DbPlayerDelete(pServer));
    register(new DbPlayerIconsDelete(pServer));
    register(new DbPlayerInjuriesDelete(pServer));
    register(new DbPlayerResultsDelete(pServer));
    register(new DbPlayersDelete(pServer));
    register(new DbPlayerSkillsDelete(pServer));
    register(new DbStepStackDelete(pServer));
    register(new DbTeamResultsDelete(pServer));
    register(new DbTeamsDelete(pServer));
    register(new DbTeamSetupsDelete(pServer));
    register(new DbTurnDataDelete(pServer));
    register(new DbUserSettingsDelete(pServer));
    
  }

  public DbStatement getStatement(DbStatementId pStatementId) {
    return fStatementById.get(pStatementId);
  }

  private void register(DbUpdateStatement pStatement) {
    fStatementById.put(pStatement.getId(), pStatement);
  }

  public void prepareStatements() throws SQLException {
    fDbConnection = getServer().getDbConnectionManager().openDbConnection();
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
    getServer().getDbConnectionManager().closeDbConnection(fDbConnection);
  }

  public FantasyFootballServer getServer() {
    return fServer;
  }
  
}
