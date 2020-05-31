package com.balancedbytes.games.ffb.server.db.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.db.DbStatement;
import com.balancedbytes.games.ffb.server.db.DbStatementId;
import com.balancedbytes.games.ffb.server.db.IDbTableTeamSetups;

/**
 * 
 * @author Kalimar
 */
public class DbTeamSetupsForTeamQuery extends DbStatement {
  
  private PreparedStatement fStatement;
  
  public DbTeamSetupsForTeamQuery(FantasyFootballServer pServer) {
    super(pServer);
  }
  
  public DbStatementId getId() {
    return DbStatementId.TEAM_SETUPS_QUERY_ALL_FOR_A_TEAM;
  }

  public void prepare(Connection pConnection) {
    try {
      StringBuilder sql = new StringBuilder();
      sql.append("SELECT DISTINCT name FROM ").append(IDbTableTeamSetups.TABLE_NAME).append(" WHERE team_id = ?");
      fStatement = pConnection.prepareStatement(sql.toString());
    } catch (SQLException sqlE) {
      throw new FantasyFootballException(sqlE);
    }
  }
  
  public String[] execute(Team pTeam) {
    String[] names = null;
    if (pTeam != null) {
      try {
        fStatement.setString(1, pTeam.getId());
        List<String> nameList = new ArrayList<String>();
        try (ResultSet resultSet = fStatement.executeQuery()) {
          while (resultSet.next()) {
            nameList.add(resultSet.getString(1));
          }
          names = nameList.toArray(new String[nameList.size()]);
        }
      } catch (SQLException sqlE) {
        throw new FantasyFootballException(sqlE);
      }
    }
    return names;
  }
  
}
