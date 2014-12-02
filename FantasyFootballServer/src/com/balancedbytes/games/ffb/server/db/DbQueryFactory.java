package com.balancedbytes.games.ffb.server.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.ServerMode;
import com.balancedbytes.games.ffb.server.db.old.DbActingPlayersForGameStateQuery;
import com.balancedbytes.games.ffb.server.db.old.DbAdminListByStatusQueryOld;
import com.balancedbytes.games.ffb.server.db.old.DbDialogsForGameStateQuery;
import com.balancedbytes.games.ffb.server.db.old.DbFieldModelsForGameStateQuery;
import com.balancedbytes.games.ffb.server.db.old.DbGameListQueryOpenGamesByCoachOld;
import com.balancedbytes.games.ffb.server.db.old.DbGameLogsForGameStateQuery;
import com.balancedbytes.games.ffb.server.db.old.DbGameOptionsForGameStateQuery;
import com.balancedbytes.games.ffb.server.db.old.DbGameStatesQuery;
import com.balancedbytes.games.ffb.server.db.old.DbGameStatesQueryFinishedGames;
import com.balancedbytes.games.ffb.server.db.old.DbInducementsForGameStateQuery;
import com.balancedbytes.games.ffb.server.db.old.DbPlayerIconsForGameStateQuery;
import com.balancedbytes.games.ffb.server.db.old.DbPlayerInjuriesForGameStateQuery;
import com.balancedbytes.games.ffb.server.db.old.DbPlayerResultsForGameStateQuery;
import com.balancedbytes.games.ffb.server.db.old.DbPlayerSkillsForGameStateQuery;
import com.balancedbytes.games.ffb.server.db.old.DbPlayersForGameStateQuery;
import com.balancedbytes.games.ffb.server.db.old.DbStepStackForGameStateQuery;
import com.balancedbytes.games.ffb.server.db.old.DbTeamResultsForGameStateQuery;
import com.balancedbytes.games.ffb.server.db.old.DbTeamsForGameStateQuery;
import com.balancedbytes.games.ffb.server.db.old.DbTurnDataForGameStateQuery;
import com.balancedbytes.games.ffb.server.db.query.DbAdminListByStatusQuery;
import com.balancedbytes.games.ffb.server.db.query.DbGameListQueryOpenGamesByCoach;
import com.balancedbytes.games.ffb.server.db.query.DbGamesInfoInsertQuery;
import com.balancedbytes.games.ffb.server.db.query.DbGamesSerializedQuery;
import com.balancedbytes.games.ffb.server.db.query.DbPasswordForCoachQuery;
import com.balancedbytes.games.ffb.server.db.query.DbPlayerMarkersQuery;
import com.balancedbytes.games.ffb.server.db.query.DbTeamSetupsForTeamQuery;
import com.balancedbytes.games.ffb.server.db.query.DbTeamSetupsQuery;
import com.balancedbytes.games.ffb.server.db.query.DbUserSettingsQuery;

/**
 * 
 * @author Kalimar
 */
public class DbQueryFactory implements IDbStatementFactory {
  
  private DbConnectionManager fDbConnectionManager;
  
  private Connection fDbConnection;
  
  private Map<DbStatementId, DbStatement> fStatementById;
  
  public DbQueryFactory(DbConnectionManager pDbConnectionManager) {
    
  	fDbConnectionManager = pDbConnectionManager;
    fStatementById = new HashMap<DbStatementId, DbStatement>();
    
    register(new DbActingPlayersForGameStateQuery(getServer()));
    register(new DbAdminListByStatusQuery(getServer()));
    register(new DbAdminListByStatusQueryOld(getServer()));  // will be removed later
    register(new DbDialogsForGameStateQuery(getServer()));
    register(new DbFieldModelsForGameStateQuery(getServer()));
    register(new DbGameListQueryOpenGamesByCoach(getServer()));
    register(new DbGameListQueryOpenGamesByCoachOld(getServer()));  // will be removed later
    register(new DbGameLogsForGameStateQuery(getServer()));
    register(new DbGameOptionsForGameStateQuery(getServer()));
    register(new DbGamesSerializedQuery(getServer()));
    register(new DbGamesInfoInsertQuery(getServer()));
    register(new DbGameStatesQuery(getServer()));
    register(new DbGameStatesQueryFinishedGames(getServer()));
    register(new DbInducementsForGameStateQuery(getServer()));
    register(new DbPlayerIconsForGameStateQuery(getServer()));
    register(new DbPlayerInjuriesForGameStateQuery(getServer()));
    register(new DbPlayerResultsForGameStateQuery(getServer()));
    register(new DbPlayersForGameStateQuery(getServer()));
    register(new DbPlayerSkillsForGameStateQuery(getServer()));
    register(new DbStepStackForGameStateQuery(getServer()));
    register(new DbTeamResultsForGameStateQuery(getServer()));
    register(new DbTeamSetupsForTeamQuery(getServer()));
    register(new DbTeamSetupsQuery(getServer()));
    register(new DbTeamsForGameStateQuery(getServer()));
    register(new DbTurnDataForGameStateQuery(getServer()));
    register(new DbUserSettingsQuery(getServer()));
    register(new DbPlayerMarkersQuery(getServer()));
    
    if (ServerMode.STANDALONE == getServer().getMode()) {
      register(new DbPasswordForCoachQuery(getServer()));
    }
    
  }
  
  public DbStatement getStatement(DbStatementId pStatementId) {
    return fStatementById.get(pStatementId);
  }
  
  private void register(DbStatement pStatement) {
    fStatementById.put(pStatement.getId(), pStatement);
  }
  
  public void prepareStatements() throws SQLException {
    fDbConnection = getDbConnectionManager().openDbConnection();
    fDbConnection.setAutoCommit(true);
    Iterator<DbStatement> statementIterator = fStatementById.values().iterator();
    while (statementIterator.hasNext()) {
      DbStatement statement = statementIterator.next();
      statement.prepare(fDbConnection);
    }
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
