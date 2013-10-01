package com.balancedbytes.games.ffb.server.db.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.db.DbStatement;
import com.balancedbytes.games.ffb.server.db.DbStatementId;
import com.balancedbytes.games.ffb.server.db.IDbTableUserSettings;

/**
 * 
 * @author Kalimar
 */
public class DbUserSettingsQuery extends DbStatement {
  
  private class QueryResult {

    private String fSettingName;
    private String fSettingValue;
    
    public QueryResult(ResultSet pResultSet) throws SQLException {
      if (pResultSet != null) {
        int col = 1;
        pResultSet.getString(col++);  // coach
        fSettingName = pResultSet.getString(col++);
        fSettingValue = pResultSet.getString(col++);
      }
    }
    
    public String getSettingName() {
      return fSettingName;
    }
    
    public String getSettingValue() {
      return fSettingValue;
    }
    
  }
  
  private PreparedStatement fStatement;
  
  private String fCoach;
  
  private Map<String, String> fSettings;
  
  public DbUserSettingsQuery(FantasyFootballServer pServer) {
    super(pServer);
    fSettings = new HashMap<String, String>();
  }
  
  public DbStatementId getId() {
    return DbStatementId.USER_SETTINGS_QUERY;
  }

  public void prepare(Connection pConnection) {
    try {
      StringBuilder sql = new StringBuilder();
      sql.append("SELECT * FROM ").append(IDbTableUserSettings.TABLE_NAME).append(" WHERE coach=? ORDER BY name");
      fStatement = pConnection.prepareStatement(sql.toString());
    } catch (SQLException sqlE) {
      throw new FantasyFootballException(sqlE);
    }
  }
  
  public void execute(String pCoach) {
    fCoach = pCoach;
    fSettings.clear();
    try {
      fStatement.setString(1, pCoach);
      ResultSet resultSet = fStatement.executeQuery();
      while (resultSet.next()) {
        QueryResult queryResult = new QueryResult(resultSet);
        fSettings.put(queryResult.getSettingName(), queryResult.getSettingValue());
      }
    } catch (SQLException sqlE) {
      throw new FantasyFootballException(sqlE);
    }
  }
  
  public String getCoach() {
    return fCoach;
  }
  
  public String[] getSettingNames() {
    String[] names = fSettings.keySet().toArray(new String[fSettings.size()]);
    Arrays.sort(names);
    return names;
  }
  
  public String getSettingValue(String pSettingName) {
    return fSettings.get(pSettingName);
  }
  
  public String[] getSettingValues() {
    String[] names = getSettingNames();
    String[] values = new String[names.length];
    for (int i = 0; i < names.length; i++) {
      values[i] = getSettingValue(names[i]);
    }
    return values;
  }
  
}
