package com.balancedbytes.games.ffb.server.db.old;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.balancedbytes.games.ffb.FantasyFootballException;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.db.DbStatement;
import com.balancedbytes.games.ffb.server.db.DbStatementId;

/**
 * 
 * @author Kalimar
 */
public class DbTeamsForGameStateQuery extends DbStatement {
  
  private PreparedStatement fStatement;
  
  public DbTeamsForGameStateQuery(FantasyFootballServer pServer) {
    super(pServer);
  }
  
  public DbStatementId getId() {
    return DbStatementId.TEAMS_FOR_GAME_STATE_QUERY;
  }

  public void prepare(Connection pConnection) {
    try {
      StringBuilder sql = new StringBuilder();
      sql.append("SELECT * FROM ").append(IDbTableTeams.TABLE_NAME).append(" WHERE game_state_id=?");
      fStatement = pConnection.prepareStatement(sql.toString());
    } catch (SQLException sqlE) {
      throw new FantasyFootballException(sqlE);
    }
  }
  
  public void execute(GameState pGameState) {
    if (pGameState != null) {
      try {
        Game game = pGameState.getGame();
        fStatement.setLong(1, pGameState.getId());
        ResultSet resultSet = fStatement.executeQuery();
        while (resultSet.next()) {
          int col = 1;
          col++;  // gameStateId
          Team team = new Team();
          team.setId(resultSet.getString(col++));
          team.setRosterId(resultSet.getString(col++));
          team.setName(resultSet.getString(col++));
          boolean homeTeam = resultSet.getBoolean(col++);
          team.setRace(resultSet.getString(col++));
          team.setCoach(resultSet.getString(col++));
          team.setReRolls(resultSet.getByte(col++));
          team.setApothecaries(resultSet.getByte(col++));
          team.setCheerleaders(resultSet.getByte(col++));
          team.setAssistantCoaches(resultSet.getByte(col++));
          team.setFanFactor(resultSet.getByte(col++));
          team.setTeamValue(resultSet.getInt(col++));
          team.setDivision(resultSet.getString(col++));
          team.setTreasury(resultSet.getInt(col++));
          team.setBaseIconPath(resultSet.getString(col++));
          if (homeTeam) {
            game.setTeamHome(team);
          } else {
            game.setTeamAway(team);
          }
        }
        resultSet.close();
      } catch (SQLException sqlE) {
        throw new FantasyFootballException(sqlE);
      }
    }
  }

}
