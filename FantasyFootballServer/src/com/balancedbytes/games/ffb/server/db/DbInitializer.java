package com.balancedbytes.games.ffb.server.db;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.TeamSetup;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.TeamSetupCache;
import com.balancedbytes.games.ffb.util.StringTool;

/**
 * Create all tables in the right order.
 *  
 * @author Kalimar
 */
public class DbInitializer {
  
  private static final String _DIR_TEAM_SETUPS = "setups";
  
  private static final String[] _COACHES = new String[] { "Kalimar", "BattleLore", "LordCrunchy", "LordMisery" };
  private static final String[] _PASSWORDS = new String[] { "f14bcf4b9ce4dd76dcc324a034dcabb6", "77acbde639f9676910987a94227d1192", "fb8f371eb70e3ac3117aa77b6929ee0a", "74baf495a667a34978097fbe81968c0a" };
  
  private DbConnectionManager fDbConnectionManager;
  
  public DbInitializer(DbConnectionManager pDbConnectionManager) {
    fDbConnectionManager = pDbConnectionManager;
  }

  public void initDb() throws SQLException {
    
    Connection connection = fDbConnectionManager.openDbConnection();
    Statement statement = connection.createStatement();
    
    dropTable(statement, IDbTablePlayerMarkers.TABLE_NAME);
    dropTable(statement, IDbTableTeamSetups.TABLE_NAME);
    dropTable(statement, IDbTableUserSettings.TABLE_NAME);
    dropTable(statement, IDbTableGamesInfo.TABLE_NAME);
    dropTable(statement, IDbTableGamesSerialized.TABLE_NAME);
    
    FantasyFootballServer server = fDbConnectionManager.getServer();
    
    if (server.getMode().isStandalone()) {
      dropTable(statement, IDbTableCoaches.TABLE_NAME);
      createTableCoaches(statement);
    }

    createTablePlayerMarkers(statement);
    createTableUserSettings(statement);
    createTableTeamSetups(statement);
    createTableGamesInfo(statement);
    createTableGamesSerialized(statement);
    
    if (server.getMode().isStandalone()) {
      initTableCoaches(statement);
      initTableTeamSetups(statement);
    }

    connection.commit();
    connection.close();
    
  }
  
  private int dropTable(Statement pStatement, String pTableName) throws SQLException {
    StringBuilder sql = new StringBuilder();
    // sql.append("DROP TABLE ").append(pTableName).append(" IF EXISTS;");
    sql.append("DROP TABLE IF EXISTS ").append(pTableName).append(";");
    return pStatement.executeUpdate(sql.toString());
  }
  
  private int createTableTeamSetups(Statement pStatement) throws SQLException {
    StringBuilder sql = new StringBuilder();
    sql.append("CREATE TABLE ").append(IDbTableTeamSetups.TABLE_NAME).append("(");
    sql.append(IDbTableTeamSetups.COLUMN_TEAM_ID).append(" VARCHAR(40) NOT NULL,");                // 1
    sql.append(IDbTableTeamSetups.COLUMN_NAME).append(" VARCHAR(40) NOT NULL,");                   // 2
    for (int i = 1; i <= 11; i++) {
      if (i > 1) {
        sql.append(",");
      }
      sql.append(StringTool.bind(IDbTableTeamSetups.COLUMN_PLAYER_NR, i)).append(" TINYINT,");     // 3 + (i * 3)
      sql.append(StringTool.bind(IDbTableTeamSetups.COLUMN_COORDINATE_X, i)).append(" TINYINT,");  // 4 + (i * 3)
      sql.append(StringTool.bind(IDbTableTeamSetups.COLUMN_COORDINATE_Y, i)).append(" TINYINT");   // 5 + (i * 3)
    }
  	sql.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
    return pStatement.executeUpdate(sql.toString());
  }
  
  private int createTableUserSettings(Statement pStatement) throws SQLException {
    StringBuilder sql = new StringBuilder();
    sql.append("CREATE TABLE ").append(IDbTableUserSettings.TABLE_NAME).append("(");
    sql.append(IDbTableUserSettings.COLUMN_COACH).append(" VARCHAR(40) NOT NULL,");  // 1
    sql.append(IDbTableUserSettings.COLUMN_NAME).append(" VARCHAR(40) NOT NULL,");   // 2
    sql.append(IDbTableUserSettings.COLUMN_VALUE).append(" VARCHAR(40)");           // 3
  	sql.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
    return pStatement.executeUpdate(sql.toString());
  }
  
  private int createTableCoaches(Statement pStatement) throws SQLException {
    StringBuilder sql = new StringBuilder();
    sql.append("CREATE TABLE ").append(IDbTableCoaches.TABLE_NAME).append(" (");
    sql.append(IDbTableCoaches.COLUMN_NAME).append(" VARCHAR(40) NOT NULL,");      // 1
    sql.append(IDbTableCoaches.COLUMN_PASSWORD).append(" VARCHAR(32) NOT NULL,");  // 2
    sql.append("PRIMARY KEY(").append(IDbTableCoaches.COLUMN_NAME).append(")");
  	sql.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
    return pStatement.executeUpdate(sql.toString());
  }
  
  private int createTableGamesInfo(Statement pStatement) throws SQLException {
    StringBuilder sql = new StringBuilder();
    sql.append("CREATE TABLE ").append(IDbTableGamesInfo.TABLE_NAME).append(" (");
    sql.append(IDbTableGamesInfo.COLUMN_ID).append(" BIGINT NOT NULL AUTO_INCREMENT,");  // 1
    sql.append(IDbTableGamesInfo.COLUMN_SCHEDULED).append(" DATETIME,");                 // 2
    sql.append(IDbTableGamesInfo.COLUMN_STARTED).append(" DATETIME,");                   // 3
    sql.append(IDbTableGamesInfo.COLUMN_FINISHED).append(" DATETIME,");                  // 4
    sql.append(IDbTableGamesInfo.COLUMN_COACH_HOME).append(" VARCHAR(40),");             // 5
    sql.append(IDbTableGamesInfo.COLUMN_TEAM_HOME_ID).append(" VARCHAR(40),");           // 6
    sql.append(IDbTableGamesInfo.COLUMN_TEAM_HOME_NAME).append(" VARCHAR(100),");        // 7
    sql.append(IDbTableGamesInfo.COLUMN_COACH_AWAY).append(" VARCHAR(40),");             // 8
    sql.append(IDbTableGamesInfo.COLUMN_TEAM_AWAY_ID).append(" VARCHAR(40),");           // 9
    sql.append(IDbTableGamesInfo.COLUMN_TEAM_AWAY_NAME).append(" VARCHAR(100),");        // 10
    sql.append(IDbTableGamesInfo.COLUMN_HALF).append(" TINYINT NOT NULL,");              // 11
    sql.append(IDbTableGamesInfo.COLUMN_TURN).append(" TINYINT NOT NULL,");              // 12
    sql.append(IDbTableGamesInfo.COLUMN_HOME_PLAYING).append(" BOOLEAN NOT NULL,");      // 13
    sql.append(IDbTableGamesInfo.COLUMN_STATUS).append(" CHAR(1) NOT NULL,");            // 14
    sql.append(IDbTableGamesInfo.COLUMN_TESTING).append(" BOOLEAN NOT NULL,");           // 15
    sql.append("PRIMARY KEY(").append(IDbTableGamesInfo.COLUMN_ID).append(")");
  	sql.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
    return pStatement.executeUpdate(sql.toString());
  }

  private int createTableGamesSerialized(Statement pStatement) throws SQLException {
    StringBuilder sql = new StringBuilder();
    sql.append("CREATE TABLE ").append(IDbTableGamesSerialized.TABLE_NAME).append(" (");
    sql.append(IDbTableGamesSerialized.COLUMN_ID).append(" BIGINT NOT NULL,");     // 1
  	sql.append(IDbTableGamesSerialized.COLUMN_SERIALIZED).append(" MEDIUMBLOB,");  // 2
    sql.append("PRIMARY KEY(").append(IDbTableGamesSerialized.COLUMN_ID).append(")");
  	sql.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
    return pStatement.executeUpdate(sql.toString());
  }
  
  private int createTablePlayerMarkers(Statement pStatement) throws SQLException {
    StringBuilder sql = new StringBuilder();
    sql.append("CREATE TABLE ").append(IDbTablePlayerMarkers.TABLE_NAME).append("(");
    sql.append(IDbTablePlayerMarkers.COLUMN_TEAM_ID).append(" VARCHAR(40) NOT NULL,");    // 1
    sql.append(IDbTablePlayerMarkers.COLUMN_PLAYER_ID).append(" VARCHAR(40) NOT NULL,");  // 2
    sql.append(IDbTablePlayerMarkers.COLUMN_TEXT).append(" VARCHAR(40),");                // 3
    sql.append("PRIMARY KEY(").append(IDbTablePlayerMarkers.COLUMN_TEAM_ID).append(",").append(IDbTablePlayerMarkers.COLUMN_PLAYER_ID).append(")");
    sql.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
    return pStatement.executeUpdate(sql.toString());
  }

  private int initTableCoaches(Statement pStatement) throws SQLException {
    int updatedRows = 0;
    for (int i = 0; i < _COACHES.length; i++) {
      StringBuilder sql = new StringBuilder();
      sql.append("INSERT INTO ").append(IDbTableCoaches.TABLE_NAME).append(" VALUES('").append(_COACHES[i]).append("', '").append(_PASSWORDS[i]).append("');");
      updatedRows += pStatement.executeUpdate(sql.toString());
    }
    return updatedRows;
  }

  private int initTableTeamSetups(Statement pStatement) throws SQLException {
    TeamSetupCache teamSetupCache = new TeamSetupCache();
    try {
      teamSetupCache.init(new File(_DIR_TEAM_SETUPS));
    } catch (IOException ioe) {
      throw new FantasyFootballException(ioe);
    }
    int updatedRows = 0;
    for (TeamSetup teamSetup : teamSetupCache.getTeamSetups()) {
      StringBuilder sql = new StringBuilder();
      sql.append("INSERT INTO ").append(IDbTableTeamSetups.TABLE_NAME).append(" VALUES(");
      sql.append("'").append(teamSetup.getTeamId()).append("'");
      sql.append(",").append("'").append(teamSetup.getName()).append("'");
      int[] playerNumbers = teamSetup.getPlayerNumbers();
      FieldCoordinate[] coordinates = teamSetup.getCoordinates();
      for (int i = 0; i < playerNumbers.length; i++) {
        sql.append(",").append(playerNumbers[i]);
        sql.append(",").append(coordinates[i].getX());
        sql.append(",").append(coordinates[i].getY());
      }
      sql.append(");");
      updatedRows += pStatement.executeUpdate(sql.toString());
    }
    return updatedRows;
  }  
    
}
