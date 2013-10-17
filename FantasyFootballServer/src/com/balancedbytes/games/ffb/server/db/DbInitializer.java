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
import com.balancedbytes.games.ffb.server.db.old.IDbTableActingPlayers;
import com.balancedbytes.games.ffb.server.db.old.IDbTableDialogs;
import com.balancedbytes.games.ffb.server.db.old.IDbTableFieldModels;
import com.balancedbytes.games.ffb.server.db.old.IDbTableGameLogs;
import com.balancedbytes.games.ffb.server.db.old.IDbTableGameOptions;
import com.balancedbytes.games.ffb.server.db.old.IDbTableGameStates;
import com.balancedbytes.games.ffb.server.db.old.IDbTableInducements;
import com.balancedbytes.games.ffb.server.db.old.IDbTablePlayerIcons;
import com.balancedbytes.games.ffb.server.db.old.IDbTablePlayerInjuries;
import com.balancedbytes.games.ffb.server.db.old.IDbTablePlayerResults;
import com.balancedbytes.games.ffb.server.db.old.IDbTablePlayerSkills;
import com.balancedbytes.games.ffb.server.db.old.IDbTablePlayers;
import com.balancedbytes.games.ffb.server.db.old.IDbTableStepStack;
import com.balancedbytes.games.ffb.server.db.old.IDbTableTeamResults;
import com.balancedbytes.games.ffb.server.db.old.IDbTableTeams;
import com.balancedbytes.games.ffb.server.db.old.IDbTableTurnData;
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
  
  private static final int _DIALOG_MAX_BYTES = 1024;

  private FantasyFootballServer fServer;
  
  public DbInitializer(FantasyFootballServer pServer) {
    fServer = pServer;
  }

  public FantasyFootballServer getServer() {
    return fServer;
  }
      
  public void initDb() throws SQLException {
    
    Connection connection = getServer().getDbConnectionManager().openDbConnection();
    Statement statement = connection.createStatement();
    
    dropTable(statement, IDbTableGameLogs.TABLE_NAME);
    dropTable(statement, IDbTableFieldModels.TABLE_NAME);
    dropTable(statement, IDbTableInducements.TABLE_NAME);
    dropTable(statement, IDbTableTurnData.TABLE_NAME);
    dropTable(statement, IDbTableActingPlayers.TABLE_NAME);
    dropTable(statement, IDbTableDialogs.TABLE_NAME);
    dropTable(statement, IDbTableGameOptions.TABLE_NAME);
    dropTable(statement, IDbTableStepStack.TABLE_NAME);
    
    dropTable(statement, IDbTablePlayerResults.TABLE_NAME);
    dropTable(statement, IDbTablePlayerInjuries.TABLE_NAME);
    dropTable(statement, IDbTablePlayerSkills.TABLE_NAME);
    dropTable(statement, IDbTablePlayerIcons.TABLE_NAME);
    dropTable(statement, IDbTablePlayers.TABLE_NAME);
    dropTable(statement, IDbTableTeamResults.TABLE_NAME);
    dropTable(statement, IDbTableTeams.TABLE_NAME);
    
    dropTable(statement, IDbTableGameStates.TABLE_NAME);

    // new tables (1.0.6) -->
    dropTable(statement, IDbTableTeamSetups.TABLE_NAME);
    dropTable(statement, IDbTableUserSettings.TABLE_NAME);
    dropTable(statement, IDbTableGamesInfo.TABLE_NAME);
    dropTable(statement, IDbTableGamesSerialized.TABLE_NAME);
    // <-- new tables (1.0.6)
    
    if (getServer().getMode().isStandalone()) {
      dropTable(statement, IDbTableCoaches.TABLE_NAME);
      createTableCoaches(statement);
    }

    // new tables (1.0.6) -->
    createTableUserSettings(statement);
    createTableTeamSetups(statement);
    createTableGamesInfo(statement);
    createTableGamesSerialized(statement);
    // <-- new tables (1.0.6)
    
    createTableGameStates(statement);
    createTableFieldModels(statement);
    createTableActingPlayers(statement);
    createTableTurnData(statement);
    createTableInducements(statement);
    createTableDialogs(statement);
    createTableGameLogs(statement);
    createTableGameOptions(statement);
    createTableStepStack(statement);
    
    createTableTeams(statement);
    createTableTeamResults(statement);
    createTablePlayers(statement);
    createTablePlayerIcons(statement);
    createTablePlayerSkills(statement);
    createTablePlayerInjuries(statement);
    createTablePlayerResults(statement);

    if (getServer().getMode().isStandalone()) {
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
    sql.append(IDbTableUserSettings.COLUMN_VALUE).append(" VARCHAR(40),");           // 3
    sql.append("PRIMARY KEY(").append(IDbTableUserSettings.COLUMN_COACH).append(")");
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
    sql.append(IDbTableGamesInfo.COLUMN_ID).append(" BIGINT NOT NULL,");             // 1
    sql.append(IDbTableGamesInfo.COLUMN_SCHEDULED).append(" DATETIME,");             // 2
    sql.append(IDbTableGamesInfo.COLUMN_STARTED).append(" DATETIME,");               // 3
    sql.append(IDbTableGamesInfo.COLUMN_FINISHED).append(" DATETIME,");              // 4
    sql.append(IDbTableGamesInfo.COLUMN_COACH_HOME).append(" VARCHAR(40),");         // 5
    sql.append(IDbTableGamesInfo.COLUMN_TEAM_HOME_ID).append(" VARCHAR(40),");       // 6
    sql.append(IDbTableGamesInfo.COLUMN_TEAM_HOME_NAME).append(" VARCHAR(100),");    // 7
    sql.append(IDbTableGamesInfo.COLUMN_COACH_AWAY).append(" VARCHAR(40),");         // 8
    sql.append(IDbTableGamesInfo.COLUMN_TEAM_AWAY_ID).append(" VARCHAR(40),");       // 9
    sql.append(IDbTableGamesInfo.COLUMN_TEAM_AWAY_NAME).append(" VARCHAR(100),");    // 10
    sql.append(IDbTableGamesInfo.COLUMN_HALF).append(" TINYINT NOT NULL,");          // 11
    sql.append(IDbTableGamesInfo.COLUMN_TURN).append(" TINYINT NOT NULL,");          // 12
    sql.append(IDbTableGamesInfo.COLUMN_HOME_PLAYING).append(" BOOLEAN NOT NULL,");  // 13
    sql.append(IDbTableGamesInfo.COLUMN_STATUS).append(" CHAR(1) NOT NULL,");        // 14
    sql.append(IDbTableGamesInfo.COLUMN_TESTING).append(" BOOLEAN NOT NULL,");       // 15
    sql.append("PRIMARY KEY(").append(IDbTableGamesInfo.COLUMN_ID).append(")");
  	sql.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
    return pStatement.executeUpdate(sql.toString());
  }

  private int createTableGamesSerialized(Statement pStatement) throws SQLException {
    StringBuilder sql = new StringBuilder();
    sql.append("CREATE TABLE ").append(IDbTableGamesSerialized.TABLE_NAME).append(" (");
    sql.append(IDbTableGamesSerialized.COLUMN_ID).append(" BIGINT NOT NULL,");     // 1
  	sql.append(IDbTableGamesSerialized.COLUMN_SERIALIZED).append(" MEDIUMTEXT,");  // 2
    sql.append("PRIMARY KEY(").append(IDbTableGamesSerialized.COLUMN_ID).append(")");
  	sql.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
    return pStatement.executeUpdate(sql.toString());
  }

  private int createTableGameLogs(Statement pStatement) throws SQLException {
    StringBuilder sql = new StringBuilder();
    sql.append("CREATE TABLE ").append(IDbTableGameLogs.TABLE_NAME).append("(");
    sql.append(IDbTableGameLogs.COLUMN_GAME_STATE_ID).append(" BIGINT NOT NULL,");
    sql.append(IDbTableGameLogs.COLUMN_COMMAND_NR).append(" INTEGER NOT NULL,");
    sql.append(IDbTableGameLogs.COLUMN_SEQUENCE_NR).append(" TINYINT NOT NULL,");
    sql.append(IDbTableGameLogs.COLUMN_COMMAND_BYTES).append(" VARBINARY(").append(IDbTableGameLogs.MAX_BYTES).append(") NOT NULL,");
    sql.append("PRIMARY KEY(").append(IDbTableGameLogs.COLUMN_GAME_STATE_ID).append(",").append(IDbTableGameLogs.COLUMN_COMMAND_NR).append(",").append(IDbTableGameLogs.COLUMN_SEQUENCE_NR).append("),");
    sql.append("FOREIGN KEY(").append(IDbTableGameLogs.COLUMN_GAME_STATE_ID).append(") REFERENCES ").append(IDbTableGameStates.TABLE_NAME).append("(").append(IDbTableGameStates.COLUMN_ID).append(")");
  	sql.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
    return pStatement.executeUpdate(sql.toString());
  }

  // TODO: added in 0.8.0
  private int createTableStepStack(Statement pStatement) throws SQLException {
    StringBuilder sql = new StringBuilder();
    sql.append("CREATE TABLE ").append(IDbTableStepStack.TABLE_NAME).append("(");
    sql.append(IDbTableStepStack.COLUMN_GAME_STATE_ID).append(" BIGINT NOT NULL,");
    sql.append(IDbTableStepStack.COLUMN_STACK_INDEX).append(" SMALLINT NOT NULL,");
    sql.append(IDbTableStepStack.COLUMN_STEP_BYTES).append(" VARBINARY(").append(IDbTableStepStack.MAX_BYTES).append("),");
    sql.append("PRIMARY KEY(").append(IDbTableStepStack.COLUMN_GAME_STATE_ID).append(",").append(IDbTableStepStack.COLUMN_STACK_INDEX).append("),");
    sql.append("FOREIGN KEY(").append(IDbTableStepStack.COLUMN_GAME_STATE_ID).append(") REFERENCES ").append(IDbTableGameStates.TABLE_NAME).append("(").append(IDbTableGameStates.COLUMN_ID).append(")");
  	sql.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
    return pStatement.executeUpdate(sql.toString());
  }
  
  private int createTableTeams(Statement pStatement) throws SQLException {
    StringBuilder sql = new StringBuilder();
    sql.append("CREATE TABLE ").append(IDbTableTeams.TABLE_NAME).append("(");
    sql.append(IDbTableTeams.COLUMN_GAME_STATE_ID).append(" BIGINT NOT NULL,");
    sql.append(IDbTableTeams.COLUMN_ID).append(" VARCHAR(40) NOT NULL,");
    sql.append(IDbTableTeams.COLUMN_ROSTER_ID).append(" VARCHAR(40) NOT NULL,");
    sql.append(IDbTableTeams.COLUMN_NAME).append(" VARCHAR(").append(IDbTableTeams.LENGTH_NAME).append(") NOT NULL,");
    sql.append(IDbTableTeams.COLUMN_HOME_TEAM).append(" BOOLEAN NOT NULL,");
    sql.append(IDbTableTeams.COLUMN_RACE).append(" VARCHAR(40),");
    sql.append(IDbTableTeams.COLUMN_COACH).append(" VARCHAR(40) NOT NULL,");
    sql.append(IDbTableTeams.COLUMN_RE_ROLLS).append(" TINYINT NOT NULL,");
    sql.append(IDbTableTeams.COLUMN_APOTHECARIES).append(" TINYINT NOT NULL,");
    sql.append(IDbTableTeams.COLUMN_CHEERLEADERS).append(" TINYINT NOT NULL,");
    sql.append(IDbTableTeams.COLUMN_ASSISTANT_COACHES).append(" TINYINT NOT NULL,");
    sql.append(IDbTableTeams.COLUMN_FAN_FACTOR).append(" TINYINT NOT NULL,");
    sql.append(IDbTableTeams.COLUMN_TEAM_VALUE).append(" INT NOT NULL,");
    sql.append(IDbTableTeams.COLUMN_DIVISION).append(" VARCHAR(3) NOT NULL,");
    sql.append(IDbTableTeams.COLUMN_TREASURY).append(" INTEGER NOT NULL,");
    sql.append(IDbTableTeams.COLUMN_BASE_ICON_PATH).append(" VARCHAR(200),");
    sql.append(IDbTableTeams.COLUMN_LOGO_URL).append(" VARCHAR(200),");
    sql.append("PRIMARY KEY(").append(IDbTableTeams.COLUMN_GAME_STATE_ID).append(",").append(IDbTableTeams.COLUMN_ID).append("),");
    sql.append("FOREIGN KEY(").append(IDbTableTeams.COLUMN_GAME_STATE_ID).append(") REFERENCES ").append(IDbTableGameStates.TABLE_NAME).append("(").append(IDbTableGameStates.COLUMN_ID).append(")");
  	sql.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
    return pStatement.executeUpdate(sql.toString());
  }

  private int createTableTeamResults(Statement pStatement) throws SQLException {
    StringBuilder sql = new StringBuilder();
    sql.append("CREATE TABLE ").append(IDbTableTeamResults.TABLE_NAME).append("(");
    sql.append(IDbTableTeamResults.COLUMN_GAME_STATE_ID).append(" BIGINT NOT NULL,");
    sql.append(IDbTableTeamResults.COLUMN_TEAM_ID).append(" VARCHAR(40) NOT NULL,");
    sql.append(IDbTableTeamResults.COLUMN_SCORE).append(" TINYINT NOT NULL,");
    sql.append(IDbTableTeamResults.COLUMN_CONCEDED).append(" BOOLEAN NOT NULL,");
    sql.append(IDbTableTeamResults.COLUMN_FAME).append(" TINYINT NOT NULL,");
    sql.append(IDbTableTeamResults.COLUMN_SPECTATORS).append(" INTEGER NOT NULL,");
    sql.append(IDbTableTeamResults.COLUMN_WINNINGS).append(" INTEGER NOT NULL,");
    sql.append(IDbTableTeamResults.COLUMN_SPIRALLING_EXPENSES).append(" INTEGER NOT NULL,");
    sql.append(IDbTableTeamResults.COLUMN_FAN_FACTOR_MODIFIER).append(" TINYINT NOT NULL,");
    sql.append(IDbTableTeamResults.COLUMN_SUFFERED_BH).append(" TINYINT NOT NULL,");
    sql.append(IDbTableTeamResults.COLUMN_SUFFERED_SI).append(" TINYINT NOT NULL,");
    sql.append(IDbTableTeamResults.COLUMN_SUFFERED_RIP).append(" TINYINT NOT NULL,");
    sql.append(IDbTableTeamResults.COLUMN_RAISED_DEAD).append(" TINYINT NOT NULL,");
    sql.append(IDbTableTeamResults.COLUMN_PETTY_CASH_TRANSFERRED).append(" INTEGER NOT NULL,");
    sql.append(IDbTableTeamResults.COLUMN_PETTY_CASH_USED).append(" INTEGER NOT NULL,");
    sql.append(IDbTableTeamResults.COLUMN_TEAM_VALUE).append(" INTEGER NOT NULL,");
    sql.append("PRIMARY KEY(").append(IDbTableTeamResults.COLUMN_GAME_STATE_ID).append(",").append(IDbTableTeamResults.COLUMN_TEAM_ID).append("),");
    sql.append("FOREIGN KEY(").append(IDbTableTeamResults.COLUMN_GAME_STATE_ID).append(") REFERENCES ").append(IDbTableGameStates.TABLE_NAME).append("(").append(IDbTableGameStates.COLUMN_ID).append("),");
    sql.append("FOREIGN KEY(").append(IDbTableTeamResults.COLUMN_GAME_STATE_ID).append(",").append(IDbTableTeamResults.COLUMN_TEAM_ID).append(") REFERENCES ").append(IDbTableTeams.TABLE_NAME).append("(game_state_id,id)");
  	sql.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
    return pStatement.executeUpdate(sql.toString());
  }

  private int createTablePlayers(Statement pStatement) throws SQLException {
    StringBuilder sql = new StringBuilder();
    sql.append("CREATE TABLE ").append(IDbTablePlayers.TABLE_NAME).append("(");
    sql.append(IDbTablePlayers.COLUMN_GAME_STATE_ID).append(" BIGINT NOT NULL,");
    sql.append(IDbTablePlayers.COLUMN_ID).append(" VARCHAR(40) NOT NULL,");
    sql.append(IDbTablePlayers.COLUMN_TEAM_ID).append(" VARCHAR(40) NOT NULL,");
    sql.append(IDbTablePlayers.COLUMN_POSITION_ID).append(" VARCHAR(40) NOT NULL,");
    sql.append(IDbTablePlayers.COLUMN_NUMBER).append(" TINYINT NOT NULL,");
    sql.append(IDbTablePlayers.COLUMN_NAME).append(" VARCHAR(").append(IDbTablePlayers.LENGTH_NAME).append(") NOT NULL,");
    sql.append(IDbTablePlayers.COLUMN_GENDER).append(" CHAR(1) NOT NULL,");
    sql.append(IDbTablePlayers.COLUMN_TYPE).append(" TINYINT NOT NULL,");
    sql.append(IDbTablePlayers.COLUMN_MOVEMENT).append(" TINYINT NOT NULL,");
    sql.append(IDbTablePlayers.COLUMN_STRENGTH).append(" TINYINT NOT NULL,");
    sql.append(IDbTablePlayers.COLUMN_AGILITY).append(" TINYINT NOT NULL,");
    sql.append(IDbTablePlayers.COLUMN_ARMOR).append(" TINYINT NOT NULL,");
    sql.append(IDbTablePlayers.COLUMN_CURRENT_INJURY).append("current_injury TINYINT NOT NULL,");
    sql.append("PRIMARY KEY(").append(IDbTablePlayers.COLUMN_GAME_STATE_ID).append(",").append(IDbTablePlayers.COLUMN_ID).append("),");
    sql.append("FOREIGN KEY(").append(IDbTablePlayers.COLUMN_GAME_STATE_ID).append(") REFERENCES ").append(IDbTableGameStates.TABLE_NAME).append("(").append(IDbTableGameStates.COLUMN_ID).append("),");
    sql.append("FOREIGN KEY(").append(IDbTablePlayers.COLUMN_GAME_STATE_ID).append(",").append(IDbTablePlayers.COLUMN_TEAM_ID).append(") REFERENCES ").append(IDbTableTeams.TABLE_NAME).append("(game_state_id,id)");
  	sql.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
    return pStatement.executeUpdate(sql.toString());
  }

  private int createTablePlayerIcons(Statement pStatement) throws SQLException {
    StringBuilder sql = new StringBuilder();
    sql.append("CREATE TABLE ").append(IDbTablePlayerIcons.TABLE_NAME).append("(");
    sql.append(IDbTablePlayerIcons.COLUMN_GAME_STATE_ID).append(" BIGINT NOT NULL,");   // 1
    sql.append(IDbTablePlayerIcons.COLUMN_PLAYER_ID).append(" VARCHAR(40) NOT NULL,");  // 2
    sql.append(IDbTablePlayerIcons.COLUMN_ICON_TYPE).append(" CHAR(2) NOT NULL,");      // 3
    sql.append(IDbTablePlayerIcons.COLUMN_ICON_URL).append(" VARCHAR(200) NOT NULL,");  // 4
    sql.append("FOREIGN KEY(").append(IDbTablePlayerIcons.COLUMN_GAME_STATE_ID).append(") REFERENCES ").append(IDbTableGameStates.TABLE_NAME).append("(").append(IDbTableGameStates.COLUMN_ID).append("),");
    sql.append("FOREIGN KEY(").append(IDbTablePlayerIcons.COLUMN_GAME_STATE_ID).append(",").append(IDbTablePlayerIcons.COLUMN_PLAYER_ID).append(") REFERENCES ").append(IDbTablePlayers.TABLE_NAME).append(" (").append(IDbTablePlayers.COLUMN_GAME_STATE_ID).append(",").append(IDbTablePlayers.COLUMN_ID).append(")");
  	sql.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
    return pStatement.executeUpdate(sql.toString());
  }

  private int createTablePlayerSkills(Statement pStatement) throws SQLException {
    StringBuilder sql = new StringBuilder();
    sql.append("CREATE TABLE ").append(IDbTablePlayerSkills.TABLE_NAME).append("(");
    sql.append(IDbTablePlayerSkills.COLUMN_GAME_STATE_ID).append(" BIGINT NOT NULL,");   // 1
    sql.append(IDbTablePlayerSkills.COLUMN_PLAYER_ID).append(" VARCHAR(40) NOT NULL,");  // 2
    sql.append(IDbTablePlayerSkills.COLUMN_SKILL).append(" VARCHAR(40) NOT NULL,");      // 3
    sql.append("FOREIGN KEY(").append(IDbTablePlayerSkills.COLUMN_GAME_STATE_ID).append(") REFERENCES ").append(IDbTableGameStates.TABLE_NAME).append("(").append(IDbTableGameStates.COLUMN_ID).append("),");
    sql.append("FOREIGN KEY(").append(IDbTablePlayerSkills.COLUMN_GAME_STATE_ID).append(",").append(IDbTablePlayerSkills.COLUMN_PLAYER_ID).append(") REFERENCES ").append(IDbTablePlayers.TABLE_NAME).append(" (").append(IDbTablePlayers.COLUMN_GAME_STATE_ID).append(",").append(IDbTablePlayers.COLUMN_ID).append(")");
  	sql.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
    return pStatement.executeUpdate(sql.toString());
  }

  private int createTablePlayerInjuries(Statement pStatement) throws SQLException {
    StringBuilder sql = new StringBuilder();
    sql.append("CREATE TABLE ").append(IDbTablePlayerInjuries.TABLE_NAME).append("(");
    sql.append(IDbTablePlayerInjuries.COLUMN_GAME_STATE_ID).append(" BIGINT NOT NULL,");   // 1
    sql.append(IDbTablePlayerInjuries.COLUMN_PLAYER_ID).append(" VARCHAR(40) NOT NULL,");  // 2
    sql.append(IDbTablePlayerInjuries.COLUMN_INJURY).append(" VARCHAR(40) NOT NULL,");     // 3
    sql.append("FOREIGN KEY(").append(IDbTablePlayerInjuries.COLUMN_GAME_STATE_ID).append(") REFERENCES ").append(IDbTableGameStates.TABLE_NAME).append("(").append(IDbTableGameStates.COLUMN_ID).append("),");
    sql.append("FOREIGN KEY(").append(IDbTablePlayerInjuries.COLUMN_GAME_STATE_ID).append(",").append(IDbTablePlayerInjuries.COLUMN_PLAYER_ID).append(") REFERENCES ").append(IDbTablePlayers.TABLE_NAME).append(" (").append(IDbTablePlayers.COLUMN_GAME_STATE_ID).append(",").append(IDbTablePlayers.COLUMN_ID).append(")");
  	sql.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
    return pStatement.executeUpdate(sql.toString());
  }
  
  private int createTablePlayerResults(Statement pStatement) throws SQLException {
    StringBuilder sql = new StringBuilder();
    sql.append("CREATE TABLE ").append(IDbTablePlayerResults.TABLE_NAME).append("(");
    sql.append(IDbTablePlayerResults.COLUMN_GAME_STATE_ID).append(" BIGINT NOT NULL,");
    sql.append(IDbTablePlayerResults.COLUMN_PLAYER_ID).append(" VARCHAR(40) NOT NULL,");
    sql.append(IDbTablePlayerResults.COLUMN_COMPLETIONS).append(" TINYINT NOT NULL,");
    sql.append(IDbTablePlayerResults.COLUMN_TOUCHDOWNS).append(" TINYINT NOT NULL,");
    sql.append(IDbTablePlayerResults.COLUMN_INTERCEPTIONS).append(" TINYINT NOT NULL,");
    sql.append(IDbTablePlayerResults.COLUMN_CASUALTIES).append(" TINYINT NOT NULL,");
    sql.append(IDbTablePlayerResults.COLUMN_PLAYER_AWARDS).append(" TINYINT NOT NULL,");
    sql.append(IDbTablePlayerResults.COLUMN_PASSING).append(" TINYINT NOT NULL,");
    sql.append(IDbTablePlayerResults.COLUMN_RUSHING).append(" TINYINT NOT NULL,");
    sql.append(IDbTablePlayerResults.COLUMN_BLOCKS).append(" TINYINT NOT NULL,");
    sql.append(IDbTablePlayerResults.COLUMN_FOULS).append(" TINYINT NOT NULL,");
    sql.append(IDbTablePlayerResults.COLUMN_OLD_SPPS).append(" SMALLINT NOT NULL,");
    sql.append(IDbTablePlayerResults.COLUMN_SERIOUS_INJURY).append(" TINYINT NOT NULL,");
    sql.append(IDbTablePlayerResults.COLUMN_SERIOUS_INJURY_DECAY).append(" TINYINT NOT NULL,");
    sql.append(IDbTablePlayerResults.COLUMN_SEND_TO_BOX_REASON).append(" TINYINT NOT NULL,");
    sql.append(IDbTablePlayerResults.COLUMN_SEND_TO_BOX_TURN).append(" TINYINT NOT NULL,");
    sql.append(IDbTablePlayerResults.COLUMN_SEND_TO_BOX_HALF).append(" TINYINT NOT NULL,");
    sql.append(IDbTablePlayerResults.COLUMN_SEND_TO_BOX_BY_PLAYER_ID).append(" VARCHAR(40),");
    sql.append(IDbTablePlayerResults.COLUMN_TURNS_PLAYED).append(" TINYINT NOT NULL,");
    sql.append(IDbTablePlayerResults.COLUMN_HAS_USED_SECRET_WEAPON).append(" BOOLEAN NOT NULL,");
    sql.append(IDbTablePlayerResults.COLUMN_DEFECTING).append(" BOOLEAN NOT NULL,");
    sql.append("PRIMARY KEY(").append(IDbTablePlayerResults.COLUMN_GAME_STATE_ID).append(",").append(IDbTablePlayerResults.COLUMN_PLAYER_ID).append("),");
    sql.append("FOREIGN KEY(").append(IDbTablePlayerResults.COLUMN_GAME_STATE_ID).append(") REFERENCES ").append(IDbTableGameStates.TABLE_NAME).append("(").append(IDbTableGameStates.COLUMN_ID).append("),");
    sql.append("FOREIGN KEY(").append(IDbTablePlayerResults.COLUMN_GAME_STATE_ID).append(",").append(IDbTablePlayerResults.COLUMN_PLAYER_ID).append(") REFERENCES ").append(IDbTablePlayers.TABLE_NAME).append("(game_state_id,id)");
  	sql.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
    return pStatement.executeUpdate(sql.toString());
  }
  
  private int createTableGameStates(Statement pStatement) throws SQLException {
    StringBuilder sql = new StringBuilder();
    sql.append("CREATE TABLE ").append(IDbTableGameStates.TABLE_NAME).append(" (");
    sql.append(IDbTableGameStates.COLUMN_ID).append(" BIGINT NOT NULL,");                     // 1
    sql.append(IDbTableGameStates.COLUMN_SCHEDULED).append(" DATETIME,");                     // 2
    sql.append(IDbTableGameStates.COLUMN_STARTED).append(" DATETIME,");                       // 3
    sql.append(IDbTableGameStates.COLUMN_FINISHED).append(" DATETIME,");                      // 4
    sql.append(IDbTableGameStates.COLUMN_HALF).append(" TINYINT NOT NULL,");                  // 5
    sql.append(IDbTableGameStates.COLUMN_TURN_MODE).append(" TINYINT NOT NULL,");             // 6
    sql.append(IDbTableGameStates.COLUMN_HOME_PLAYING).append(" BOOLEAN NOT NULL,");          // 7
    sql.append(IDbTableGameStates.COLUMN_HOME_FIRST_OFFENSE).append(" BOOLEAN NOT NULL,");    // 8
    sql.append(IDbTableGameStates.COLUMN_SETUP_OFFENSE).append(" BOOLEAN NOT NULL,");         // 9
    sql.append(IDbTableGameStates.COLUMN_WAITING_FOR_OPPONENT).append(" BOOLEAN NOT NULL,");  // 10
    sql.append(IDbTableGameStates.COLUMN_DEFENDER_ID).append(" VARCHAR(40),");                // 11
    sql.append(IDbTableGameStates.COLUMN_DEFENDER_ACTION).append(" TINYINT,");                // 12
    sql.append(IDbTableGameStates.COLUMN_PASS_COORDINATE_X).append(" TINYINT,");              // 13
    sql.append(IDbTableGameStates.COLUMN_PASS_COORDINATE_Y).append(" TINYINT,");              // 14
    sql.append(IDbTableGameStates.COLUMN_TURN_TIME).append(" BIGINT,");                       // 15
    sql.append(IDbTableGameStates.COLUMN_TIMEOUT_POSSIBLE).append(" BOOLEAN,");               // 16
    sql.append(IDbTableGameStates.COLUMN_TIMEOUT_ENFORCED).append(" BOOLEAN,");               // 17
    sql.append(IDbTableGameStates.COLUMN_CONCESSION_POSSIBLE).append(" BOOLEAN,");            // 18
    sql.append(IDbTableGameStates.COLUMN_TESTING).append(" BOOLEAN,");                        // 19
    sql.append(IDbTableGameStates.COLUMN_STATUS).append(" CHAR(1) NOT NULL,");                // 20
    sql.append(IDbTableGameStates.COLUMN_THROWER_ID).append(" VARCHAR(40),");                 // 21
    sql.append(IDbTableGameStates.COLUMN_THROWER_ACTION).append(" TINYINT,");                 // 22
    sql.append("PRIMARY KEY(").append(IDbTableGameStates.COLUMN_ID).append(")");
  	sql.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
    return pStatement.executeUpdate(sql.toString());
  }

  private int createTableGameOptions(Statement pStatement) throws SQLException {
    StringBuilder sql = new StringBuilder();
    sql.append("CREATE TABLE ").append(IDbTableGameOptions.TABLE_NAME).append(" (");
    sql.append(IDbTableGameOptions.COLUMN_GAME_STATE_ID).append(" BIGINT NOT NULL,");     // 1
    sql.append(IDbTableGameOptions.COLUMN_OPTION_NAME).append(" VARCHAR(40) NOT NULL,");  // 2
    sql.append(IDbTableGameOptions.COLUMN_OPTION_VALUE).append(" INTEGER NOT NULL,");     // 3
    sql.append("FOREIGN KEY(").append(IDbTableGameOptions.COLUMN_GAME_STATE_ID).append(") REFERENCES ").append(IDbTableGameStates.TABLE_NAME).append("(").append(IDbTableGameStates.COLUMN_ID).append(")");
  	sql.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
    return pStatement.executeUpdate(sql.toString());
  }

  private int createTableDialogs(Statement pStatement) throws SQLException {
    StringBuilder sql = new StringBuilder();
    sql.append("CREATE TABLE ").append(IDbTableDialogs.TABLE_NAME).append(" (");
    sql.append(IDbTableDialogs.COLUMN_GAME_STATE_ID).append(" BIGINT NOT NULL,");             // 1
    sql.append(IDbTableDialogs.COLUMN_DIALOG_ID).append(" TINYINT,");                         // 2
    sql.append(IDbTableDialogs.COLUMN_SEQUENCE_NR).append(" TINYINT,");                       // 3
    sql.append(IDbTableDialogs.COLUMN_PARAMETER_BYTES).append(" VARBINARY(").append(_DIALOG_MAX_BYTES).append(") NOT NULL,");  // 4
    sql.append("PRIMARY KEY(").append(IDbTableDialogs.COLUMN_GAME_STATE_ID).append(",").append(IDbTableDialogs.COLUMN_DIALOG_ID).append(",").append(IDbTableDialogs.COLUMN_SEQUENCE_NR).append("),");
    sql.append("FOREIGN KEY(").append(IDbTableDialogs.COLUMN_GAME_STATE_ID).append(") REFERENCES ").append(IDbTableGameStates.TABLE_NAME).append("(").append(IDbTableGameStates.COLUMN_ID).append(")");
  	sql.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
    return pStatement.executeUpdate(sql.toString());
  }
  
  private int createTableTurnData(Statement pStatement) throws SQLException {
    StringBuilder sql = new StringBuilder();
    sql.append("CREATE TABLE ").append(IDbTableTurnData.TABLE_NAME).append(" (");
    sql.append(IDbTableTurnData.COLUMN_GAME_STATE_ID).append(" BIGINT NOT NULL,");              // 1
    sql.append(IDbTableTurnData.COLUMN_HOME_DATA).append(" BOOLEAN NOT NULL,");                 // 2
    sql.append(IDbTableTurnData.COLUMN_TURN_NR).append(" TINYINT NOT NULL,");                   // 3
    sql.append(IDbTableTurnData.COLUMN_FIRST_TURN_AFTER_KICKOFF).append(" BOOLEAN NOT NULL,");  // 4
    sql.append(IDbTableTurnData.COLUMN_RE_ROLLS).append(" TINYINT NOT NULL,");                  // 5
    sql.append(IDbTableTurnData.COLUMN_APOTHECARIES).append(" TINYINT NOT NULL,");              // 6
    sql.append(IDbTableTurnData.COLUMN_RE_ROLL_USED).append(" BOOLEAN NOT NULL,");              // 7
    sql.append(IDbTableTurnData.COLUMN_BLITZ_USED).append(" BOOLEAN NOT NULL,");                // 8
    sql.append(IDbTableTurnData.COLUMN_FOUL_USED).append(" BOOLEAN NOT NULL,");                 // 9
    sql.append(IDbTableTurnData.COLUMN_HAND_OVER_USED).append(" BOOLEAN NOT NULL,");            // 10
    sql.append(IDbTableTurnData.COLUMN_PASS_USED).append(" BOOLEAN NOT NULL,");                 // 11
    sql.append(IDbTableTurnData.COLUMN_LEADER_STATE).append(" TINYINT NOT NULL,");              // 12
    sql.append(IDbTableTurnData.COLUMN_TURN_STARTED).append(" BOOLEAN NOT NULL,");              // 13
    sql.append("FOREIGN KEY(").append(IDbTableTurnData.COLUMN_GAME_STATE_ID).append(") REFERENCES ").append(IDbTableGameStates.TABLE_NAME).append("(").append(IDbTableGameStates.COLUMN_ID).append(")");
  	sql.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
    return pStatement.executeUpdate(sql.toString());
  }
  
  private int createTableInducements(Statement pStatement) throws SQLException {
    StringBuilder sql = new StringBuilder();
    sql.append("CREATE TABLE ").append(IDbTableInducements.TABLE_NAME).append("(");
    sql.append(IDbTableInducements.COLUMN_GAME_STATE_ID).append(" BIGINT NOT NULL,");  // 1
    sql.append(IDbTableInducements.COLUMN_HOME_DATA).append(" BOOLEAN NOT NULL,");     // 2
    sql.append(IDbTableInducements.COLUMN_TYPE).append(" TINYINT NOT NULL,");          // 3
    sql.append(IDbTableInducements.COLUMN_VALUE).append(" SMALLINT NOT NULL,");        // 4
    sql.append(IDbTableInducements.COLUMN_USES).append(" TINYINT NOT NULL,");          // 5
    sql.append("FOREIGN KEY(").append(IDbTableInducements.COLUMN_GAME_STATE_ID).append(") REFERENCES ").append(IDbTableGameStates.TABLE_NAME).append("(").append(IDbTableGameStates.COLUMN_ID).append(")");
  	sql.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
    return pStatement.executeUpdate(sql.toString());
  }

  private int createTableActingPlayers(Statement pStatement) throws SQLException {
    StringBuilder sql = new StringBuilder();
    sql.append("CREATE TABLE ").append(IDbTableActingPlayers.TABLE_NAME).append(" (");
    sql.append(IDbTableActingPlayers.COLUMN_GAME_STATE_ID).append(" BIGINT NOT NULL,");         // 1
    sql.append(IDbTableActingPlayers.COLUMN_PLAYER_ID).append(" VARCHAR(40),");                 // 2
    sql.append(IDbTableActingPlayers.COLUMN_STRENGTH).append(" TINYINT NOT NULL,");             // 3
    sql.append(IDbTableActingPlayers.COLUMN_CURRENT_MOVE).append(" TINYINT NOT NULL,");         // 4
    sql.append(IDbTableActingPlayers.COLUMN_GOING_FOR_IT).append(" BOOLEAN NOT NULL,");         // 5
    sql.append(IDbTableActingPlayers.COLUMN_DODGING).append(" BOOLEAN NOT NULL,");              // 6
    sql.append(IDbTableActingPlayers.COLUMN_LEAPING).append(" BOOLEAN NOT NULL,");              // 7
    sql.append(IDbTableActingPlayers.COLUMN_HAS_BLOCKED).append(" BOOLEAN NOT NULL,");          // 8
    sql.append(IDbTableActingPlayers.COLUMN_HAS_FOULED).append(" BOOLEAN NOT NULL,");           // 9
    sql.append(IDbTableActingPlayers.COLUMN_HAS_PASSED).append(" BOOLEAN NOT NULL,");           // 10
    sql.append(IDbTableActingPlayers.COLUMN_HAS_MOVED).append(" BOOLEAN NOT NULL,");            // 11
    sql.append(IDbTableActingPlayers.COLUMN_PLAYER_ACTION).append(" TINYINT NOT NULL,");        // 12
    sql.append(IDbTableActingPlayers.COLUMN_STANDING_UP).append(" BOOLEAN NOT NULL,");          // 13
    sql.append(IDbTableActingPlayers.COLUMN_SUFFERING_BLOODLUST).append(" BOOLEAN NOT NULL,");  // 14
    sql.append(IDbTableActingPlayers.COLUMN_SUFFERING_ANIMOSITY).append(" BOOLEAN NOT NULL,");  // 15
    sql.append(IDbTableActingPlayers.COLUMN_USED_SKILL_1).append(" TINYINT NOT NULL,");         // 16
    sql.append(IDbTableActingPlayers.COLUMN_USED_SKILL_2).append(" TINYINT NOT NULL,");         // 17
    sql.append(IDbTableActingPlayers.COLUMN_USED_SKILL_3).append(" TINYINT NOT NULL,");         // 18
    sql.append(IDbTableActingPlayers.COLUMN_USED_SKILL_4).append(" TINYINT NOT NULL,");         // 19
    sql.append(IDbTableActingPlayers.COLUMN_USED_SKILL_5).append(" TINYINT NOT NULL,");         // 20
    sql.append("FOREIGN KEY(").append(IDbTableActingPlayers.COLUMN_GAME_STATE_ID).append(") REFERENCES ").append(IDbTableGameStates.TABLE_NAME).append("(").append(IDbTableGameStates.COLUMN_ID).append(")");
  	sql.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
    return pStatement.executeUpdate(sql.toString());
  }

  private int createTableFieldModels(Statement pStatement) throws SQLException {
    StringBuilder sql = new StringBuilder();
    sql.append("CREATE TABLE ").append(IDbTableFieldModels.TABLE_NAME).append(" (");
    sql.append(IDbTableFieldModels.COLUMN_GAME_STATE_ID).append(" BIGINT NOT NULL,");  // 1
    sql.append(IDbTableFieldModels.COLUMN_TYPE).append(" CHAR(2) NOT NULL,");          // 2
    sql.append(IDbTableFieldModels.COLUMN_ITEM).append(" TINYINT NOT NULL,");          // 3
    sql.append(IDbTableFieldModels.COLUMN_COORDINATE_X).append(" TINYINT,");           // 4
    sql.append(IDbTableFieldModels.COLUMN_COORDINATE_Y).append(" TINYINT,");           // 5
    sql.append(IDbTableFieldModels.COLUMN_NUMBER_1).append(" TINYINT NOT NULL,");      // 6
    sql.append(IDbTableFieldModels.COLUMN_NUMBER_2).append(" TINYINT NOT NULL,");      // 7
    sql.append(IDbTableFieldModels.COLUMN_FLAG_1).append(" BOOLEAN NOT NULL,");        // 8
    sql.append(IDbTableFieldModels.COLUMN_FLAG_2).append(" BOOLEAN NOT NULL,");        // 9
    sql.append(IDbTableFieldModels.COLUMN_ID_1).append(" INT NOT NULL,");              // 10
    sql.append(IDbTableFieldModels.COLUMN_TEXT_1).append(" VARCHAR(").append(IDbTableFieldModels.MAX_TEXT_LENGTH).append(") NULL,");  // 11
    sql.append(IDbTableFieldModels.COLUMN_TEXT_2).append(" VARCHAR(").append(IDbTableFieldModels.MAX_TEXT_LENGTH).append(") NULL,");  // 12
    sql.append("FOREIGN KEY(").append(IDbTableFieldModels.COLUMN_GAME_STATE_ID).append(") REFERENCES ").append(IDbTableGameStates.TABLE_NAME).append("(id)");
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
