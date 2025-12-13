package com.fumbbl.ffb.server.mechanic.mixed;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.dialog.DialogSetupErrorParameter;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.UtilGameOption;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.ArrayList;
import java.util.List;

@RulesCollection(RulesCollection.Rules.BB2016)
@RulesCollection(RulesCollection.Rules.BB2020)
public class SetupMechanic extends com.fumbbl.ffb.server.mechanic.SetupMechanic {

	@Override
	public boolean checkSetup(GameState pGameState, boolean pHomeTeam) {
		return checkSetup(pGameState, pHomeTeam, 0);
	}

	@Override
	public boolean checkSetup(GameState pGameState, boolean pHomeTeam, int additionalSwarmers) {
		if (pGameState == null) {
			throw new IllegalArgumentException("Parameter gameState must not be null.");
		}
		int swarmersOnField = 0;
		int playersOnField = 0;
		int playersInUpperWideZone = 0;
		int playersInLowerWideZone = 0;
		int playersOnLos = 0;
		int availablePlayers = 0;
		Game game = pGameState.getGame();
		List<String> messageList = new ArrayList<>();
		Team team = pHomeTeam ? game.getTeamHome() : game.getTeamAway();
		for (Player<?> player : team.getPlayers()) {
			PlayerState playerState = game.getFieldModel().getPlayerState(player);
			// Keen Players are available but have to be removed from the count to not trigger setup checks as they do not have to be fielded
			if (playerState.canBeSetUpNextDrive() && !player.hasSkillProperty(NamedProperties.canJoinTeamIfLessThanEleven)) {
				availablePlayers++;
			}
			FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(player);
			if ((pHomeTeam && FieldCoordinateBounds.HALF_HOME.isInBounds(playerCoordinate))
					|| (!pHomeTeam && FieldCoordinateBounds.HALF_AWAY.isInBounds(playerCoordinate))) {
				if (player.hasSkillProperty(NamedProperties.canSneakExtraPlayersOntoPitch)) {
					swarmersOnField++;
				} else {
					playersOnField++;
				}
			}
			if ((pHomeTeam && FieldCoordinateBounds.UPPER_WIDE_ZONE_HOME.isInBounds(playerCoordinate))
					|| (!pHomeTeam && FieldCoordinateBounds.UPPER_WIDE_ZONE_AWAY.isInBounds(playerCoordinate))) {
				playersInUpperWideZone++;
			}
			if ((pHomeTeam && FieldCoordinateBounds.LOWER_WIDE_ZONE_HOME.isInBounds(playerCoordinate))
					|| (!pHomeTeam && FieldCoordinateBounds.LOWER_WIDE_ZONE_AWAY.isInBounds(playerCoordinate))) {
				playersInLowerWideZone++;
			}
			if ((pHomeTeam && FieldCoordinateBounds.LOS_HOME.isInBounds(playerCoordinate))
					|| (!pHomeTeam && FieldCoordinateBounds.LOS_AWAY.isInBounds(playerCoordinate))) {
				playersOnLos++;
			}
		}
		int maxPlayersOnField = UtilGameOption.getIntOption(game, GameOptionId.MAX_PLAYERS_ON_FIELD);
		int allPlayersOnField = playersOnField + swarmersOnField;
		if (allPlayersOnField > maxPlayersOnField + additionalSwarmers || playersOnField > maxPlayersOnField) {
			messageList.add("You placed " + allPlayersOnField + " Players on the field. Maximum are "
					+ (maxPlayersOnField + additionalSwarmers) + " players.");
			if (additionalSwarmers > 0) {
				messageList.add("Maximum " + maxPlayersOnField + " regular Players and maximum " + additionalSwarmers
						+ " Swarming Players.");
			}
		}
		if ((allPlayersOnField < maxPlayersOnField) && (availablePlayers >= maxPlayersOnField)) {
			messageList.add("You placed " + allPlayersOnField + " Players on the field. You have to put " + maxPlayersOnField
					+ " players on the field (except Keen Players).");
		} else {
			if ((allPlayersOnField < maxPlayersOnField) && (allPlayersOnField < availablePlayers)) {
				messageList.add(
						"You placed " + allPlayersOnField + " Players on the field. You have to put all players (except Keen Players) on the field.");
			}
		}
		int maxPlayersInWideZone = UtilGameOption.getIntOption(game, GameOptionId.MAX_PLAYERS_IN_WIDE_ZONE);
		if (playersInLowerWideZone > maxPlayersInWideZone) {
			messageList.add("You placed " + playersInLowerWideZone + " Players in the lower wide zone. Only "
					+ maxPlayersInWideZone + " allowed there.");
		}
		if (playersInUpperWideZone > maxPlayersInWideZone) {
			messageList.add("You placed " + playersInUpperWideZone + " Players in the upper wide zone. Only "
					+ maxPlayersInWideZone + " allowed there.");
		}
		int minPlayersOnLos = UtilGameOption.getIntOption(game, GameOptionId.MIN_PLAYERS_ON_LOS);
		if ((playersOnLos < minPlayersOnLos) && (availablePlayers >= minPlayersOnLos)) {
			messageList.add("You placed " + playersOnLos + " Players on the Line of Scrimmage. You have to put "
					+ minPlayersOnLos + " there.");
		} else {
			if (playersOnLos < minPlayersOnLos && playersOnLos < availablePlayers) {
				messageList.add("You placed " + playersOnLos
						+ " Players on the Line of Scrimmage. You have to put all your Players there.");
			}
		}
		if (!messageList.isEmpty()) {
			UtilServerDialog.showDialog(pGameState,
					new DialogSetupErrorParameter(team.getId(), messageList.toArray(new String[0])), false);
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void pinPlayersInTacklezones(GameState pGameState, Team pTeam) {
		pinPlayersInTacklezones(pGameState, pTeam, false);
	}

	@Override
	public void pinPlayersInTacklezones(GameState pGameState, Team pTeam, boolean pinBallAndChain) {
		Game game = pGameState.getGame();
		Team otherTeam = (pTeam == game.getTeamHome()) ? game.getTeamAway() : game.getTeamHome();
		for (Player<?> player : pTeam.getPlayers()) {
			PlayerState playerState = game.getFieldModel().getPlayerState(player);
			if (playerState.isActive()) {
				FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(player);
				if (UtilPlayer.findAdjacentPlayersWithTacklezones(game, otherTeam, playerCoordinate, false).length > 0
						|| (pinBallAndChain && player.hasSkillProperty(NamedProperties.movesRandomly))) {
					game.getFieldModel().setPlayerState(player, playerState.changeActive(false));
				}
			}
		}
	}

}
