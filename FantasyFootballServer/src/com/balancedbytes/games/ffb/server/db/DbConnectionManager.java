package com.balancedbytes.games.ffb.server.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import com.balancedbytes.games.ffb.server.FantasyFootballServer;

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
  
  public DbConnectionManager(FantasyFootballServer pServer) {
    fServer = pServer;
    fConnections = new HashSet<Connection>();
  }
    
  public Connection openDbConnection() throws SQLException {
  	Connection connection = DriverManager.getConnection(fDbUrl, fDbUser, fDbPassword);
    connection.setAutoCommit(false);
    fConnections.add(connection);
    return connection;
  }
  
  public void closeDbConnection(Connection pConnection) throws SQLException {
  	if (pConnection != null) {
  		if (!pConnection.getAutoCommit()) {
  			pConnection.commit();
  		}
      pConnection.close();
      fConnections.remove(pConnection);
  	}
  }
  
  public FantasyFootballServer getServer() {
    return fServer;
  }
  
  public void doKeepAlivePing() throws SQLException {
    for (Connection connection : fConnections) {
      Statement statement = connection.createStatement();
      statement.executeQuery("SELECT 1;");
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

}
