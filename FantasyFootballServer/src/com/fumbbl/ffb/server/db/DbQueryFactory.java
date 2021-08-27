package com.fumbbl.ffb.server.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.ServerMode;
import com.fumbbl.ffb.server.db.query.DbAdminListByIdQuery;
import com.fumbbl.ffb.server.db.query.DbAdminListByStatusQuery;
import com.fumbbl.ffb.server.db.query.DbGameListQueryOpenGamesByCoach;
import com.fumbbl.ffb.server.db.query.DbGamesInfoInsertQuery;
import com.fumbbl.ffb.server.db.query.DbGamesSerializedQuery;
import com.fumbbl.ffb.server.db.query.DbPasswordForCoachQuery;
import com.fumbbl.ffb.server.db.query.DbPlayerMarkersQuery;
import com.fumbbl.ffb.server.db.query.DbTeamSetupsForTeamQuery;
import com.fumbbl.ffb.server.db.query.DbTeamSetupsQuery;
import com.fumbbl.ffb.server.db.query.DbUserSettingsQuery;

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
		fStatementById = new HashMap<>();

		register(new DbAdminListByIdQuery(getServer()));
		register(new DbAdminListByStatusQuery(getServer()));
		register(new DbGameListQueryOpenGamesByCoach(getServer()));
		register(new DbGamesSerializedQuery(getServer()));
		register(new DbGamesInfoInsertQuery(getServer()));
		register(new DbTeamSetupsForTeamQuery(getServer()));
		register(new DbTeamSetupsQuery(getServer()));
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
