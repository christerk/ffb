package com.fumbbl.ffb.server.db.query;

import com.fumbbl.ffb.FantasyFootballException;
import com.fumbbl.ffb.marking.PlayerMarker;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.db.DbStatement;
import com.fumbbl.ffb.server.db.DbStatementId;
import com.fumbbl.ffb.server.db.IDbTablePlayerMarkers;
import com.fumbbl.ffb.util.StringTool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * @author Kalimar
 */
public class DbPlayerMarkersQuery extends DbStatement {

	private PreparedStatement fStatement;

	public DbPlayerMarkersQuery(FantasyFootballServer pServer) {
		super(pServer);
	}

	public DbStatementId getId() {
		return DbStatementId.PLAYER_MARKERS_QUERY;
	}

	public void prepare(Connection pConnection) {
		try {
			String sql = "SELECT " + IDbTablePlayerMarkers.COLUMN_PLAYER_ID + "," +
				IDbTablePlayerMarkers.COLUMN_TEXT + " FROM " + IDbTablePlayerMarkers.TABLE_NAME +
				" WHERE (" + IDbTablePlayerMarkers.COLUMN_TEAM_ID + "=?)";
			fStatement = pConnection.prepareStatement(sql);
		} catch (SQLException sqlE) {
			throw new FantasyFootballException(sqlE);
		}
	}

	public void execute(GameState pGameState, boolean homeTeam) {
		if (pGameState == null) {
			return;
		}
		queryMarkers(pGameState, homeTeam);
	}

	private void queryMarkers(GameState pGameState, boolean pHomeTeam) {
		Game game = pGameState.getGame();
		Team team = pHomeTeam ? game.getTeamHome() : game.getTeamAway();
		if ((team == null) || !StringTool.isProvided(team.getId())) {
			return;
		}
		try {
			fStatement.setString(1, team.getId());
			try (ResultSet resultSet = fStatement.executeQuery()) {
				Arrays.stream(game.getFieldModel().getPlayerMarkers()).forEach(marker -> {
					if (pHomeTeam) {
						marker.setHomeText("");
					} else {
						marker.setAwayText("");
					}
					game.getFieldModel().add(marker);
				});
				while (resultSet.next()) {
					String playerId = resultSet.getString(1);
					String text = resultSet.getString(2);
					Player<?> player = game.getPlayerById(playerId);
					if ((player != null) && StringTool.isProvided(text)) {
						PlayerMarker playerMarker = game.getFieldModel().getPlayerMarker(player.getId());
						if (playerMarker == null) {
							playerMarker = new PlayerMarker(player.getId());
							game.getFieldModel().add(playerMarker);
						}
						if (pHomeTeam) {
							playerMarker.setHomeText(text);
						} else {
							playerMarker.setAwayText(text);
						}
					}
				}
			}
		} catch (SQLException sqlE) {
			throw new FantasyFootballException(sqlE);
		}
	}

}
