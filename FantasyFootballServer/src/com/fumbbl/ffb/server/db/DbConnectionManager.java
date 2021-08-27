package com.fumbbl.ffb.server.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import com.fumbbl.ffb.server.FantasyFootballServer;

/**
 *
 * @author Kalimar
 */
public class DbConnectionManager {

	private FantasyFootballServer fServer;
	private Set<Connection> fConnections;
	private String fDbUrl;
	private String fDbUser;
	private String fDbPassword;
	private String fDbType;

	public DbConnectionManager(FantasyFootballServer pServer) {
		fServer = pServer;
		fConnections = new HashSet<>();
	}

	public Connection openDbConnection() throws SQLException {
		Connection connection = DriverManager.getConnection(fDbUrl, fDbUser, fDbPassword);
		connection.setAutoCommit(false);
		fConnections.add(connection);
		return connection;
	}

	public void closeDbConnection(Connection pConnection) throws SQLException {
		try (Connection connection = pConnection) {
			if (connection != null) {
				if (!connection.getAutoCommit()) {
					connection.commit();
				}
				fConnections.remove(connection);
			}
		}
	}

	public FantasyFootballServer getServer() {
		return fServer;
	}

	public void doKeepAlivePing() throws SQLException {
		for (Connection connection : fConnections) {
			try (Statement statement = connection.createStatement()) {
				statement.executeQuery("SELECT 1;");
			}
		}
	}

	public String getDbUrl() {
		return fDbUrl;
	}

	public void setDbUrl(String pDbUrl) {
		fDbUrl = pDbUrl;
	}

	public String getDbUser() {
		return fDbUser;
	}

	public void setDbUser(String pDbUser) {
		fDbUser = pDbUser;
	}

	public String getDbPassword() {
		return fDbPassword;
	}

	public void setDbPassword(String pDbPassword) {
		fDbPassword = pDbPassword;
	}

	public boolean isStandalone() {
		return fServer.getMode().isStandalone();
	}

	public void setDbType(String pDbType) {
		fDbType = pDbType;
	}

	public boolean useMysqlDialect() {
		return "mysql".equalsIgnoreCase(fDbType);
	}
}
