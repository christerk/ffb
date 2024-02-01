package com.fumbbl.ffb.server.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.db.delete.DbGamesInfoDelete;
import com.fumbbl.ffb.server.db.delete.DbGamesSerializedDelete;
import com.fumbbl.ffb.server.db.delete.DbPlayerMarkersDelete;
import com.fumbbl.ffb.server.db.delete.DbTeamSetupsDelete;
import com.fumbbl.ffb.server.db.delete.DbUserSettingsDelete;
import com.fumbbl.ffb.server.db.insert.DbGamesSerializedInsert;
import com.fumbbl.ffb.server.db.insert.DbPlayerMarkersInsert;
import com.fumbbl.ffb.server.db.insert.DbTeamSetupsInsert;
import com.fumbbl.ffb.server.db.insert.DbUserSettingsInsert;
import com.fumbbl.ffb.server.db.update.DbGamesInfoUpdate;
import com.fumbbl.ffb.server.db.update.DbGamesSerializedUpdate;

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
		fStatementById = new HashMap<>();

		register(new DbGamesSerializedInsert(getServer()));
		register(new DbTeamSetupsInsert(getServer()));
		register(new DbUserSettingsInsert(getServer()));
		register(new DbUserSettingsDelete(getServer()));
		register(new DbPlayerMarkersInsert(getServer()));
		register(new DbPlayerMarkersDelete(getServer()));
		register(new DbGamesInfoUpdate(getServer()));
		register(new DbGamesSerializedUpdate(getServer()));
		register(new DbGamesInfoDelete(getServer()));
		register(new DbGamesSerializedDelete(getServer()));
		register(new DbTeamSetupsDelete(getServer()));

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
		if (getDbConnectionManager().isStandalone()) {
			fDbConnection.createStatement().executeUpdate("SHUTDOWN");
		}
		getDbConnectionManager().closeDbConnection(fDbConnection);
	}

	public DbConnectionManager getDbConnectionManager() {
		return fDbConnectionManager;
	}

	public FantasyFootballServer getServer() {
		return getDbConnectionManager().getServer();
	}

}
