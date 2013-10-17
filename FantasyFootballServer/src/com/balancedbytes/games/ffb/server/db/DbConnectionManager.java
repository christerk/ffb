package com.balancedbytes.games.ffb.server.db;

import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.IServerProperty;

/**
 * 
 * @author Kalimar
 */
public class DbConnectionManager {
  
  public static final Charset CHARSET = Charset.forName("UTF-8");
  
  private FantasyFootballServer fServer;
  private Set<Connection> fConnections;
  
  public DbConnectionManager(FantasyFootballServer pServer) {
    fServer = pServer;
    fConnections = new HashSet<Connection>();
  }
    
  public Connection openDbConnection() throws SQLException {
  	Connection connection = DriverManager.getConnection(
      getServer().getProperty(IServerProperty.DB_URL),
      getServer().getProperty(IServerProperty.DB_USER),
      getServer().getProperty(IServerProperty.DB_PASSWORD)
    );
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

}
