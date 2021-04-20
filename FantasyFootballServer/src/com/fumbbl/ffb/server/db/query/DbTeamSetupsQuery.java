package com.fumbbl.ffb.server.db.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.fumbbl.ffb.FantasyFootballException;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.TeamSetup;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.db.DbStatement;
import com.fumbbl.ffb.server.db.DbStatementId;
import com.fumbbl.ffb.server.db.IDbTableTeamSetups;

/**
 * 
 * @author Kalimar
 */
public class DbTeamSetupsQuery extends DbStatement {

	private PreparedStatement fStatement;

	public DbTeamSetupsQuery(FantasyFootballServer pServer) {
		super(pServer);
	}

	public DbStatementId getId() {
		return DbStatementId.TEAM_SETUPS_QUERY;
	}

	public void prepare(Connection pConnection) {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT * FROM ").append(IDbTableTeamSetups.TABLE_NAME).append(" WHERE (team_id = ? AND name = ?)");
			fStatement = pConnection.prepareStatement(sql.toString());
		} catch (SQLException sqlE) {
			throw new FantasyFootballException(sqlE);
		}
	}

	public TeamSetup execute(String pTeamId, String pSetupName) {
		TeamSetup teamSetup = new TeamSetup();
		teamSetup.setTeamId(pTeamId);
		teamSetup.setName(pSetupName);
		try {
			fStatement.setString(1, teamSetup.getTeamId());
			fStatement.setString(2, teamSetup.getName());
			try (ResultSet resultSet = fStatement.executeQuery()) {
				while (resultSet.next()) {
					for (int i = 0; i < 11; i++) {
						int playerNr = resultSet.getByte(3 * i + 3);
						int coordinateX = resultSet.getByte((3 * i) + 4);
						int coordinateY = resultSet.getByte((3 * i) + 5);
						if (playerNr > 0) {
							FieldCoordinate playerCoordinate = new FieldCoordinate(coordinateX, coordinateY);
							teamSetup.addCoordinate(playerCoordinate, playerNr);
						}
					}
				}
			}
		} catch (SQLException sqlE) {
			throw new FantasyFootballException(sqlE);
		}
		return teamSetup;
	}

}
