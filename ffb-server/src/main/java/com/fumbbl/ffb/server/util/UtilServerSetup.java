package com.fumbbl.ffb.server.util;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.TeamSetup;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.db.DbStatementId;
import com.fumbbl.ffb.server.db.DbTransaction;
import com.fumbbl.ffb.server.db.IDbTableTeamSetups;
import com.fumbbl.ffb.server.db.delete.DbTeamSetupsDeleteParameter;
import com.fumbbl.ffb.server.db.insert.DbTeamSetupsInsertParameter;
import com.fumbbl.ffb.server.db.query.DbTeamSetupsForTeamQuery;
import com.fumbbl.ffb.server.db.query.DbTeamSetupsQuery;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilBox;
import org.eclipse.jetty.websocket.api.Session;

/**
 * @author Kalimar
 */
public class UtilServerSetup {

	public static void loadTeamSetup(GameState gameState, String setupName) {

		if (gameState != null) {

			FantasyFootballServer server = gameState.getServer();
			Game game = gameState.getGame();
			Team team = game.isHomePlaying() ? game.getTeamHome() : game.getTeamAway();

			if (StringTool.isProvided(setupName)) {
				DbTeamSetupsQuery teamSetupQuery = (DbTeamSetupsQuery) server.getDbQueryFactory()
					.getStatement(DbStatementId.TEAM_SETUPS_QUERY);
				TeamSetup teamSetup = teamSetupQuery.execute(team.getId(), setupName);
				if (teamSetup != null) {
					teamSetup.applyTo(game);
					UtilBox.refreshBoxes(game);
				}

			} else {
				// queue the list query behind pending setup updates so it does not miss a recent save or delete
				DbTransaction dbTransaction = new DbTransaction();
				dbTransaction.setOnCompletion(() -> sendTeamSetupList(server, game.getId(), game.isHomePlaying(), team));
				server.getDbUpdater().add(dbTransaction);
			}

		}

	}

	public static void saveTeamSetup(GameState gameState, String pSetupName, int[] pPlayerNumbers,
	                                 FieldCoordinate[] pPlayerCoordinates) {

		if ((gameState != null) && StringTool.isProvided(pSetupName)) {

			FantasyFootballServer server = gameState.getServer();
			Game game = gameState.getGame();
			Team team = game.isHomePlaying() ? game.getTeamHome() : game.getTeamAway();

			TeamSetup teamSetup = new TeamSetup();
			if (pSetupName.length() <= IDbTableTeamSetups.LENGTH_NAME) {
				teamSetup.setName(pSetupName);
			} else {
				teamSetup.setName(pSetupName.substring(0, IDbTableTeamSetups.LENGTH_NAME));
			}
			teamSetup.setTeamId(team.getId());
			for (int i = 0; i < pPlayerNumbers.length; i++) {
				teamSetup.addCoordinate(pPlayerCoordinates[i], pPlayerNumbers[i]);
			}

			// System.out.println(teamSetup.toXml(0));

			DbTransaction dbTransaction = new DbTransaction();
			dbTransaction.add(new DbTeamSetupsDeleteParameter(teamSetup.getTeamId(), teamSetup.getName()));
			dbTransaction.add(new DbTeamSetupsInsertParameter(teamSetup));
			server.getDbUpdater().add(dbTransaction);

		}

	}

	public static void deleteTeamSetup(GameState gameState, String pSetupName) {

		if (gameState != null) {

			FantasyFootballServer server = gameState.getServer();
			Game game = gameState.getGame();
			Team team = game.isHomePlaying() ? game.getTeamHome() : game.getTeamAway();

			// send the list only after the delete has been executed, otherwise it may still contain the deleted setup
			DbTransaction dbTransaction = new DbTransaction();
			if (StringTool.isProvided(pSetupName)) {
				dbTransaction.add(new DbTeamSetupsDeleteParameter(team.getId(), pSetupName));
			}
			dbTransaction.setOnCompletion(() -> sendTeamSetupList(server, game.getId(), game.isHomePlaying(), team));
			server.getDbUpdater().add(dbTransaction);

		}

	}

	private static void sendTeamSetupList(FantasyFootballServer server, long gameId, boolean homePlaying, Team team) {
		DbTeamSetupsForTeamQuery allSetupNamesQuery = (DbTeamSetupsForTeamQuery) server.getDbQueryFactory()
			.getStatement(DbStatementId.TEAM_SETUPS_QUERY_ALL_FOR_A_TEAM);
		String[] setupNames = allSetupNamesQuery.execute(team);
		Session session = homePlaying ? server.getSessionManager().getSessionOfHomeCoach(gameId)
			: server.getSessionManager().getSessionOfAwayCoach(gameId);
		server.getCommunication().sendTeamSetupList(session, setupNames);
	}

	public static void setupPlayer(GameState gameState, String pPlayerId, FieldCoordinate pCoordinate) {

		if ((gameState != null) && StringTool.isProvided(pPlayerId) && (pCoordinate != null)) {

			Game game = gameState.getGame();
			Player<?> player = game.getPlayerById(pPlayerId);
			if (player == null) {
				return;
			}

			boolean homeTeam = game.getTeamHome().hasPlayer(player);
			if (homeTeam != game.isHomePlaying()) {
				return;
			}

			FieldModel fieldModel = game.getFieldModel();
			FieldCoordinate coordinate = homeTeam ? pCoordinate : pCoordinate.transform();
			FieldCoordinate oldCoordinate = fieldModel.getPlayerCoordinate(player);
			PlayerState playerState = fieldModel.getPlayerState(player);

			if (fieldModel.getPlayer(coordinate) != null) {
				// Client is confused and tried to put a player in a square that already had a
				// player.
				// Force an update of the position and state.
				fieldModel.sendPosition(player);
				Player<?> otherPlayer = fieldModel.getPlayer(coordinate);
				fieldModel.sendPosition(otherPlayer);
			} else {
				if (coordinate.isBoxCoordinate()) {
					fieldModel.setPlayerState(player, playerState.changeBase(PlayerState.RESERVE));
				} else {
					if ((game.getTurnMode() == TurnMode.QUICK_SNAP) && !coordinate.equals(oldCoordinate)) {
						fieldModel.setPlayerState(player, playerState.changeBase(PlayerState.STANDING).changeActive(false));
					} else {
						fieldModel.setPlayerState(player, playerState.changeBase(PlayerState.STANDING).changeActive(true), true);
					}
				}
				fieldModel.setPlayerCoordinate(player, coordinate);
			}
		}

	}

}
