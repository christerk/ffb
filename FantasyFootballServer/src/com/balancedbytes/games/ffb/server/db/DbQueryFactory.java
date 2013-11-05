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
import com.balancedbytes.games.ffb.server.db.query.DbGamesSerializedQuery;
import com.balancedbytes.games.ffb.server.db.query.DbGamesSerializedQueryMaxId;
import com.balancedbytes.games.ffb.server.db.query.DbPasswordForCoachQuery;
import com.balancedbytes.games.ffb.server.db.query.DbTeamSetupsForTeamQuery;
import com.balancedbytes.games.ffb.server.db.query.DbTeamSetupsQuery;
import com.balancedbytes.games.ffb.server.db.query.DbUserSettingsQuery;

/**
 * 
 * @author Kalimar
 */
public class DbQueryFactory implements IDbStatementFactory {
  
  private FantasyFootballServer fServer;
  
  private Connection fDbConnection;
  
  private Map<DbStatementId, DbStatement> fStatementById;
  
  public DbQueryFactory(FantasyFootballServer pServer) {
    
  	fServer = pServer;
    fStatementById = new HashMap<DbStatementId, DbStatement>();
    
    register(new DbActingPlayersForGameStateQuery(pServer));
    register(new DbAdminListByStatusQuery(pServer));
    register(new DbAdminListByStatusQueryOld(pServer));  // will be removed later
    register(new DbDialogsForGameStateQuery(pServer));
    register(new DbFieldModelsForGameStateQuery(pServer));
    register(new DbGameListQueryOpenGamesByCoach(pServer));
    register(new DbGameListQueryOpenGamesByCoachOld(pServer));  // will be removed later
    register(new DbGameLogsForGameStateQuery(pServer));
    register(new DbGameOptionsForGameStateQuery(pServer));
    register(new DbGamesSerializedQuery(pServer));
    register(new DbGamesSerializedQueryMaxId(pServer));
    register(new DbGameStatesQuery(pServer));
    register(new DbInducementsForGameStateQuery(pServer));
    register(new DbPlayerIconsForGameStateQuery(pServer));
    register(new DbPlayerInjuriesForGameStateQuery(pServer));
    register(new DbPlayerResultsForGameStateQuery(pServer));
    register(new DbPlayersForGameStateQuery(pServer));
    register(new DbPlayerSkillsForGameStateQuery(pServer));
    register(new DbStepStackForGameStateQuery(pServer));
    register(new DbTeamResultsForGameStateQuery(pServer));
    register(new DbTeamSetupsForTeamQuery(pServer));
    register(new DbTeamSetupsQuery(pServer));
    register(new DbTeamsForGameStateQuery(pServer));
    register(new DbTurnDataForGameStateQuery(pServer));
    register(new DbUserSettingsQuery(pServer));
    
    if (ServerMode.STANDALONE == getServer().getMode()) {
      register(new DbPasswordForCoachQuery(pServer));
    }
    
  }
  
  public DbStatement getStatement(DbStatementId pStatementId) {
    return fStatementById.get(pStatementId);
  }
  
  private void register(DbStatement pStatement) {
    fStatementById.put(pStatement.getId(), pStatement);
  }
  
  public void prepareStatements() throws SQLException {
    fDbConnection = getServer().getDbConnectionManager().openDbConnection();
    fDbConnection.setAutoCommit(true);
    Iterator<DbStatement> statementIterator = fStatementById.values().iterator();
    while (statementIterator.hasNext()) {
      DbStatement statement = statementIterator.next();
      statement.prepare(fDbConnection);
    }
  }
  
  public void closeDbConnection() throws SQLException {
    getServer().getDbConnectionManager().closeDbConnection(fDbConnection);
  }
  
  public FantasyFootballServer getServer() {
    return fServer;
  }

}
