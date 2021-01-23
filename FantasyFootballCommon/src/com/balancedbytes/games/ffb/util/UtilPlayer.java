package com.balancedbytes.games.ffb.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.balancedbytes.games.ffb.Card;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.FieldCoordinateBounds;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.TurnMode;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.FieldModel;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.ISkillProperty;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.RosterPlayer;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;
import com.balancedbytes.games.ffb.option.GameOptionId;
import com.balancedbytes.games.ffb.option.UtilGameOption;

/**
 *
 * @author Kalimar
 * @author Kristian Widén
 */
public class UtilPlayer {

	public static Player<?>[] findPlayersOnPitchWithSkill(Game pGame, Team pTeam, Skill pSkill) {
		List<Player<?>> result = new ArrayList<>();
		for (Player<?> player : pTeam.getPlayers()) {
			if (UtilCards.hasSkill(pGame, player, pSkill)
					&& FieldCoordinateBounds.FIELD.isInBounds(pGame.getFieldModel().getPlayerCoordinate(player))) {
				result.add(player);
			}
		}
		return result.toArray(new Player[result.size()]);
	}

	public static Player<?>[] findPlayersOnPitchWithProperty(Game pGame, Team pTeam, ISkillProperty property) {
		List<Player<?>> result = new ArrayList<>();
		for (Player<?> player : pTeam.getPlayers()) {
			if (player.hasSkillWithProperty(property)
					&& FieldCoordinateBounds.FIELD.isInBounds(pGame.getFieldModel().getPlayerCoordinate(player))) {
				result.add(player);
			}
		}
		return result.toArray(new Player[result.size()]);
	}

	public static Player<?>[] findAdjacentOpposingPlayersWithSkill(Game pGame, FieldCoordinate pCenterCoordinate,
			Skill pSkill, boolean pCheckAbleToMove) {
		ActingPlayer actingPlayer = pGame.getActingPlayer();
		Team otherTeam = UtilPlayer.findOtherTeam(pGame, actingPlayer.getPlayer());
		Player<?>[] opponents = UtilPlayer.findAdjacentPlayersWithTacklezones(pGame, otherTeam, pCenterCoordinate, false);
		Set<Player<?>> shadowingPlayers = new HashSet<>();
		for (Player<?> opponent : opponents) {
			PlayerState opponentState = pGame.getFieldModel().getPlayerState(opponent);
			if ((opponentState != null) && opponentState.hasTacklezones() && UtilCards.hasSkill(pGame, opponent, pSkill)
					&& (!pCheckAbleToMove || opponentState.isAbleToMove())) {
				shadowingPlayers.add(opponent);
			}
		}
		Player<?>[] playerArray = shadowingPlayers.toArray(new Player[shadowingPlayers.size()]);
		UtilPlayer.sortByPlayerNr(playerArray);
		return playerArray;
	}

	public static Player<?>[] findAdjacentOpposingPlayersWithProperty(Game pGame, FieldCoordinate pCenterCoordinate,
			ISkillProperty pProperty, boolean pCheckAbleToMove) {
		ActingPlayer actingPlayer = pGame.getActingPlayer();
		Team otherTeam = UtilPlayer.findOtherTeam(pGame, actingPlayer.getPlayer());
		Player<?>[] opponents = UtilPlayer.findAdjacentPlayersWithTacklezones(pGame, otherTeam, pCenterCoordinate, false);
		Set<Player<?>> shadowingPlayers = new HashSet<>();
		for (Player<?> opponent : opponents) {
			PlayerState opponentState = pGame.getFieldModel().getPlayerState(opponent);
			if ((opponentState != null) && opponentState.hasTacklezones()
					&& opponent.hasSkillWithProperty(pProperty)
					&& (!pCheckAbleToMove || opponentState.isAbleToMove())) {
				shadowingPlayers.add(opponent);
			}
		}
		Player<?>[] playerArray = shadowingPlayers.toArray(new Player[shadowingPlayers.size()]);
		UtilPlayer.sortByPlayerNr(playerArray);
		return playerArray;
	}

	public static Player<?>[] findAdjacentPronePlayers(Game pGame, Team pTeam, FieldCoordinate pCoordinate) {
		List<Player<?>> adjacentPlayers = new ArrayList<>();
		FieldModel fieldModel = pGame.getFieldModel();
		FieldCoordinate[] adjacentCoordinates = fieldModel.findAdjacentCoordinates(pCoordinate, FieldCoordinateBounds.FIELD,
				1, false);
		for (int i = 0; i < adjacentCoordinates.length; i++) {
			Player<?> player = fieldModel.getPlayer(adjacentCoordinates[i]);
			if ((player != null) && (player.getTeam() == pTeam)) {
				PlayerState playerState = fieldModel.getPlayerState(player);
				if ((playerState.getBase() == PlayerState.PRONE) || (playerState.getBase() == PlayerState.STUNNED)) {
					adjacentPlayers.add(player);
				}
			}
		}
		return adjacentPlayers.toArray(new Player[adjacentPlayers.size()]);
	}

	public static Player<?>[] findAdjacentBlockablePlayers(Game pGame, Team pTeam, FieldCoordinate pCoordinate) {
		List<Player<?>> adjacentPlayers = new ArrayList<>();
		FieldModel fieldModel = pGame.getFieldModel();
		FieldCoordinate[] adjacentCoordinates = fieldModel.findAdjacentCoordinates(pCoordinate, FieldCoordinateBounds.FIELD,
				1, false);
		for (int i = 0; i < adjacentCoordinates.length; i++) {
			Player<?> player = fieldModel.getPlayer(adjacentCoordinates[i]);
			if ((player != null) && (player.getTeam() == pTeam)) {
				PlayerState playerState = fieldModel.getPlayerState(player);
				if (playerState.canBeBlocked()) {
					adjacentPlayers.add(player);
				}
			}
		}
		return adjacentPlayers.toArray(new Player[adjacentPlayers.size()]);
	}

	public static Player<?>[] findAdjacentStandingOrPronePlayers(Game pGame, Team pTeam, FieldCoordinate pCoordinate) {
		List<Player<?>> adjacentPlayers = new ArrayList<>();
		FieldModel fieldModel = pGame.getFieldModel();
		FieldCoordinate[] adjacentCoordinates = fieldModel.findAdjacentCoordinates(pCoordinate, FieldCoordinateBounds.FIELD,
				1, false);
		for (int i = 0; i < adjacentCoordinates.length; i++) {
			Player<?> player = fieldModel.getPlayer(adjacentCoordinates[i]);
			if ((player != null) && (player.getTeam() == pTeam)) {
				PlayerState playerState = fieldModel.getPlayerState(player);
				if (!playerState.isStunned()) {
					adjacentPlayers.add(player);
				}
			}
		}
		return adjacentPlayers.toArray(new Player[adjacentPlayers.size()]);
	}

	public static Player<?>[] findAdjacentPlayersWithTacklezones(Game pGame, Team pTeam, FieldCoordinate pCoordinate,
			boolean pWithStartCoordinate) {
		List<Player<?>> adjacentPlayers = new ArrayList<>();
		FieldModel fieldModel = pGame.getFieldModel();
		FieldCoordinate[] adjacentCoordinates = fieldModel.findAdjacentCoordinates(pCoordinate, FieldCoordinateBounds.FIELD,
				1, pWithStartCoordinate);
		for (int i = 0; i < adjacentCoordinates.length; i++) {
			Player<?> player = fieldModel.getPlayer(adjacentCoordinates[i]);
			if ((player != null) && (player.getTeam() == pTeam)) {
				PlayerState playerState = fieldModel.getPlayerState(player);
				if (playerState.hasTacklezones()) {
					adjacentPlayers.add(player);
				}
			}
		}
		return adjacentPlayers.toArray(new Player[adjacentPlayers.size()]);
	}

	public static Player<?>[] findAdjacentPlayersToFeedOn(Game pGame, Team pTeam, FieldCoordinate pCoordinate) {
		List<Player<?>> adjacentPlayers = new ArrayList<>();
		FieldModel fieldModel = pGame.getFieldModel();
		FieldCoordinate[] adjacentCoordinates = fieldModel.findAdjacentCoordinates(pCoordinate, FieldCoordinateBounds.FIELD,
				1, false);
		for (int i = 0; i < adjacentCoordinates.length; i++) {
			Player<?> player = fieldModel.getPlayer(adjacentCoordinates[i]);
			if ((player != null) && (player.getTeam() == pTeam) && (player instanceof RosterPlayer)
					&& ((RosterPlayer) player).getPosition().isThrall()) {
				adjacentPlayers.add(player);
			}
		}
		return adjacentPlayers.toArray(new Player[adjacentPlayers.size()]);
	}

	public static Player<?>[] filterThrower(Game pGame, Player<?>[] pPlayers) {
		List<Player<?>> playerList = new ArrayList<>();
		if (ArrayTool.isProvided(pPlayers)) {
			for (Player<?> player : pPlayers) {
				if (player != pGame.getThrower()) {
					playerList.add(player);
				}
			}
		}
		return playerList.toArray(new Player[playerList.size()]);
	}

	public static Player<?>[] filterAttackerAndDefender(Game pGame, Player<?>[] pPlayers) {
		List<Player<?>> playerList = new ArrayList<>();
		if (ArrayTool.isProvided(pPlayers)) {
			for (Player<?> player : pPlayers) {
				if ((player != pGame.getActingPlayer().getPlayer()) && (player != pGame.getDefender())) {
					playerList.add(player);
				}
			}
		}
		return playerList.toArray(new Player[playerList.size()]);
	}

	public static int findFoulAssists(Game pGame, Player<?> pAttacker, Player<?> pDefender) {
		int foulAssists = 0;
		FieldCoordinate coordinateDefender = pGame.getFieldModel().getPlayerCoordinate(pDefender);
		for (Player<?> offensiveAssist : findAdjacentPlayersWithTacklezones(pGame, pAttacker.getTeam(), coordinateDefender,
				false)) {
			if (offensiveAssist != pAttacker) {
				FieldCoordinate coordinateAssist = pGame.getFieldModel().getPlayerCoordinate(offensiveAssist);
				if ((findAdjacentPlayersWithTacklezones(pGame, pDefender.getTeam(), coordinateAssist, false).length < 1)
						|| (UtilGameOption.isOptionEnabled(pGame, GameOptionId.SNEAKY_GIT_AS_FOUL_GUARD)
								&& offensiveAssist.hasSkillWithProperty(NamedProperties.canAlwaysAssistFouls))) {
					foulAssists++;
				}
			}
		}
		FieldCoordinate coordinateAttacker = pGame.getFieldModel().getPlayerCoordinate(pAttacker);
		for (Player<?> defensiveAssist : findAdjacentPlayersWithTacklezones(pGame, pDefender.getTeam(), coordinateAttacker,
				false)) {
			if (defensiveAssist != pDefender) {
				FieldCoordinate coordinateAssist = pGame.getFieldModel().getPlayerCoordinate(defensiveAssist);
				if (findAdjacentPlayersWithTacklezones(pGame, pAttacker.getTeam(), coordinateAssist, false).length < 2) {
					foulAssists--;
				}
			}
		}
		return foulAssists;
	}

	public static int findStandUpAssists(Game pGame, Player<?> timmmberPlayer) {
		int assists = 0;
		Team opposingTeam = findOtherTeam(pGame, timmmberPlayer);
		FieldCoordinate coordinatePlayer = pGame.getFieldModel().getPlayerCoordinate(timmmberPlayer);
		for (Player<?> assist : findAdjacentPlayersWithTacklezones(pGame, timmmberPlayer.getTeam(), coordinatePlayer,
				false)) {
			FieldCoordinate assistCoordinate = pGame.getFieldModel().getPlayerCoordinate(assist);
			Player<?>[] opponents = findAdjacentPlayersWithTacklezones(pGame, opposingTeam, assistCoordinate, false);
			if (opponents.length == 0) {
				assists++;
			}
		}
		return assists;
	}

	public static Team findOtherTeam(Game pGame, Player<?> pPlayer) {
		Team ownTeam = pPlayer.getTeam();
		if (pGame.getTeamHome() == ownTeam) {
			return pGame.getTeamAway();
		} else {
			return pGame.getTeamHome();
		}
	}

	public static int findTacklezones(Game pGame, Player<?> pPlayer) {
		Team otherTeam = findOtherTeam(pGame, pPlayer);
		FieldCoordinate playerCoordinate = pGame.getFieldModel().getPlayerCoordinate(pPlayer);
		return findAdjacentPlayersWithTacklezones(pGame, otherTeam, playerCoordinate, false).length;
	}

	public static void refreshPlayersForTurnStart(Game pGame) {
		FieldModel fieldModel = pGame.getFieldModel();
		Player<?>[] players = pGame.getPlayers();
		for (int i = 0; i < players.length; i++) {
			PlayerState newPlayerState = null;
			PlayerState oldPlayerState = fieldModel.getPlayerState(players[i]);
			switch (oldPlayerState.getBase()) {
			case PlayerState.BLOCKED:
			case PlayerState.MOVING:
			case PlayerState.FALLING:
				newPlayerState = oldPlayerState.changeBase(PlayerState.STANDING).changeActive(true);
				break;
			case PlayerState.PRONE:
			case PlayerState.STANDING:
				newPlayerState = oldPlayerState.changeActive(true);
				break;
			case PlayerState.STUNNED:
				if ((pGame.isHomePlaying() && pGame.getTeamHome().hasPlayer(players[i]))
						|| (!pGame.isHomePlaying() && pGame.getTeamAway().hasPlayer(players[i]))) {
					newPlayerState = oldPlayerState.changeBase(PlayerState.PRONE).changeActive(false);
				}
				break;
			}
			if ((newPlayerState != null) && newPlayerState.hasUsedPro()) {
				newPlayerState = newPlayerState.changeUsedPro(false);
			} else {
				if (oldPlayerState.hasUsedPro()) {
					newPlayerState = oldPlayerState.changeUsedPro(false);
				}
			}
			if (newPlayerState != null) {
				fieldModel.setPlayerState(players[i], newPlayerState);
			}
		}
	}

	public static boolean canHandOver(Game pGame, Player<?> pThrower) {
		FieldModel fieldModel = pGame.getFieldModel();
		FieldCoordinate throwerCoordinate = fieldModel.getPlayerCoordinate(pThrower);
		Team throwerTeam = pGame.getTeamHome().hasPlayer(pThrower) ? pGame.getTeamHome() : pGame.getTeamAway();
		return (throwerCoordinate.equals(fieldModel.getBallCoordinate()) && !fieldModel.isBallMoving()
		// && !pGame.getTurnData().isHandOverUsed()
				&& (UtilPlayer.findAdjacentPlayersWithTacklezones(pGame, throwerTeam, throwerCoordinate, false).length > 0));
	}

	public static boolean canGaze(Game pGame, Player<?> pPlayer) {
		FieldCoordinate playerCoordinate = pGame.getFieldModel().getPlayerCoordinate(pPlayer);
		Team otherTeam = UtilPlayer.findOtherTeam(pGame, pPlayer);
		PlayerState playerState = pGame.getFieldModel().getPlayerState(pPlayer);

		if (!pPlayer.hasSkillWithProperty(NamedProperties.inflictsConfusion)) {
			return false;
		} else if (!playerState.isActive()) {
			return false;
		} else {
			return (UtilPlayer.findAdjacentPlayersWithTacklezones(pGame, otherTeam, playerCoordinate, false).length > 0);
		}
	}

	public static boolean canFoul(Game pGame, Player<?> pPlayer) {
		FieldModel fieldModel = pGame.getFieldModel();
		FieldCoordinate playerCoordinate = fieldModel.getPlayerCoordinate(pPlayer);
		Team otherTeam = UtilPlayer.findOtherTeam(pGame, pPlayer);
		return (
		// !pGame.getTurnData().isFoulUsed()&&
		(UtilPlayer.findAdjacentPronePlayers(pGame, otherTeam, playerCoordinate).length > 0));
	}

	public static boolean isBallAvailable(Game pGame, Player<?> pPlayer) {
		return ((pPlayer != null) && (pGame != null) && pGame.getFieldModel().isBallInPlay()
				&& (pGame.getFieldModel().isBallMoving()
						|| pGame.getFieldModel().getBallCoordinate().equals(pGame.getFieldModel().getPlayerCoordinate(pPlayer))));
	}

	public static boolean hasBall(Game pGame, Player<?> pPlayer) {
		return ((pPlayer != null) && (pGame != null) && pGame.getFieldModel().isBallInPlay()
				&& !pGame.getFieldModel().isBallMoving()
				&& pGame.getFieldModel().getBallCoordinate().equals(pGame.getFieldModel().getPlayerCoordinate(pPlayer)));
	}

	public static Player<?>[] findThrowableTeamMates(Game pGame, Player<?> pThrower) {
		List<Player<?>> throwablePlayers = new ArrayList<>();
		FieldModel fieldModel = pGame.getFieldModel();
		FieldCoordinate throwerCoordinate = fieldModel.getPlayerCoordinate(pThrower);
		Player<?>[] adjacentPlayers = findAdjacentPlayersWithTacklezones(pGame, pThrower.getTeam(), throwerCoordinate,
				false);
		for (Player<?> adjacentPlayer : adjacentPlayers) {

			if (adjacentPlayer.hasSkillWithProperty(NamedProperties.canBeThrown)) {
				throwablePlayers.add(adjacentPlayer);
			}
		}
		return throwablePlayers.toArray(new Player[throwablePlayers.size()]);
	}

	public static Player<?>[] findKickableTeamMates(Game pGame, Player<?> pKicker) {
		List<Player<?>> kickablePlayers = new ArrayList<>();
		FieldModel fieldModel = pGame.getFieldModel();
		FieldCoordinate kickerCoordinate = fieldModel.getPlayerCoordinate(pKicker);
		Player<?>[] adjacentPlayers = findAdjacentPlayersWithTacklezones(pGame, pKicker.getTeam(), kickerCoordinate, false);
		for (Player<?> adjacentPlayer : adjacentPlayers) {
			if (adjacentPlayer.hasSkillWithProperty(NamedProperties.canBeKicked)) {
				kickablePlayers.add(adjacentPlayer);
			}
		}
		return kickablePlayers.toArray(new Player[kickablePlayers.size()]);
	}

	public static boolean canThrowTeamMate(Game pGame, Player<?> pThrower, boolean pCheckPassUsed) {
		return ((pThrower != null) && (!pCheckPassUsed || !pGame.getTurnData().isPassUsed())
				&& pThrower.hasSkillWithProperty(NamedProperties.canThrowTeamMates)
				&& (UtilPlayer.findThrowableTeamMates(pGame, pThrower).length > 0));
	}

	public static boolean canKickTeamMate(Game pGame, Player<?> pKicker, boolean pCheckBlitzUsed) {
		return ((pKicker != null) && (!pCheckBlitzUsed || !pGame.getTurnData().isBlitzUsed())
				&& pKicker.hasSkillWithProperty(NamedProperties.canKickTeamMates)
				&& (UtilPlayer.findKickableTeamMates(pGame, pKicker).length > 0));
	}

	public static boolean isBlockable(Game pGame, Player<?> pPlayer) {
		ActingPlayer actingPlayer = pGame.getActingPlayer();
		if (pPlayer != null) {
			PlayerState defenderState = pGame.getFieldModel().getPlayerState(pPlayer);
			FieldCoordinate defenderCoordinate = pGame.getFieldModel().getPlayerCoordinate(pPlayer);
			FieldCoordinate attackerCoordinate = pGame.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
			return (defenderState.canBeBlocked() && pGame.getTeamAway().hasPlayer(pPlayer) && (defenderCoordinate != null)
					&& defenderCoordinate.isAdjacent(attackerCoordinate)
					&& (pGame.getFieldModel().getDiceDecoration(defenderCoordinate) != null));
		}
		return false;
	}

	public static boolean isFoulable(Game pGame, Player<?> pPlayer) {
		boolean foulable = false;
		ActingPlayer actingPlayer = pGame.getActingPlayer();
		if (pPlayer != null) {
			PlayerState defenderState = pGame.getFieldModel().getPlayerState(pPlayer);
			FieldCoordinate defenderCoordinate = pGame.getFieldModel().getPlayerCoordinate(pPlayer);
			FieldCoordinate attackerCoordinate = pGame.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
			foulable = (((defenderState.getBase() == PlayerState.PRONE) || (defenderState.getBase() == PlayerState.STUNNED))
					&& pGame.getTeamAway().hasPlayer(pPlayer) && (defenderCoordinate != null)
					&& defenderCoordinate.isAdjacent(attackerCoordinate)
					&& !UtilCards.hasCard(pGame, pPlayer, Card.GOOD_OLD_MAGIC_CODPIECE));
		}
		return foulable;
	}

	public static boolean isPickUp(Game pGame) {
		ActingPlayer actingPlayer = pGame.getActingPlayer();
		FieldCoordinate playerCoordinate = pGame.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
		return (pGame.getFieldModel().isBallInPlay() && pGame.getFieldModel().isBallMoving()
				&& playerCoordinate.equals(pGame.getFieldModel().getBallCoordinate()));
	}

	public static void sortByPlayerNr(Player<?>[] pPlayerArray) {
		Arrays.sort(pPlayerArray, new Comparator<Player<?>>() {
			public int compare(Player<?> pPlayer1, Player<?> pPlayer2) {
				return pPlayer1.getNr() - pPlayer2.getNr();
			}
		});
	}

	public static boolean isNextMoveGoingForIt(Game pGame) {
		boolean nextMoveGoingForIt = false;
		ActingPlayer actingPlayer = pGame.getActingPlayer();
		Player<?> player = actingPlayer.getPlayer();
		if (player != null) {
			if ((pGame.getTurnMode() == TurnMode.KICKOFF_RETURN) || (pGame.getTurnMode() == TurnMode.PASS_BLOCK)) {
				return false;
			} else if (actingPlayer.isStandingUp() && !actingPlayer.hasActed()
					&& !actingPlayer.getPlayer().hasSkillWithProperty(NamedProperties.canStandUpForFree)) {
				nextMoveGoingForIt = (3 >= UtilCards.getPlayerMovement(pGame, player));
			} else {
				if (actingPlayer.isLeaping()) {
					nextMoveGoingForIt = ((actingPlayer.getCurrentMove() + 1) >= UtilCards.getPlayerMovement(pGame, player));
				} else {
					nextMoveGoingForIt = (actingPlayer.getCurrentMove() >= UtilCards.getPlayerMovement(pGame, player));
				}
			}
		}
		return nextMoveGoingForIt;
	}

	public static boolean isNextMoveDodge(Game pGame) {
		boolean nextMoveDodge = false;
		if (pGame.getActingPlayer() != null) {
			Player<?> player = pGame.getActingPlayer().getPlayer();
			if (player != null) {
				Team otherTeam = findOtherTeam(pGame, player);
				FieldCoordinate playerCoordinate = pGame.getFieldModel().getPlayerCoordinate(player);
				nextMoveDodge = (findAdjacentPlayersWithTacklezones(pGame, otherTeam, playerCoordinate, false).length > 0);
			}
		}
		return nextMoveDodge;
	}

	public static boolean isNextMovePossible(Game pGame, boolean pLeaping) {
		boolean movePossible = false;
		ActingPlayer actingPlayer = pGame.getActingPlayer();
		PlayerState playerState = pGame.getFieldModel().getPlayerState(actingPlayer.getPlayer());
		if ((playerState != null) && playerState.isAbleToMove()) {
			if ((pGame.getTurnMode() == TurnMode.KICKOFF_RETURN) || (pGame.getTurnMode() == TurnMode.PASS_BLOCK)) {
				movePossible = pLeaping ? (actingPlayer.getCurrentMove() < 2) : (actingPlayer.getCurrentMove() < 3);
			} else {
				int extraMove = 0;
				if (actingPlayer.isGoingForIt()) {
					boolean canMakeAnExtraGfi = actingPlayer.getPlayer().hasSkillWithProperty(NamedProperties.canMakeAnExtraGfi);
					extraMove = canMakeAnExtraGfi ? 3 : 2;
					if (pLeaping) {
						extraMove--;
					}
				}
				movePossible = (actingPlayer
						.getCurrentMove() < (UtilCards.getPlayerMovement(pGame, actingPlayer.getPlayer()) + extraMove));
			}
		}
		return movePossible;
	}

	public static boolean isPickup(Game pGame) {
		Player<?> player = pGame.getActingPlayer().getPlayer();
		FieldCoordinate playerCoordinate = pGame.getFieldModel().getPlayerCoordinate(player);
		return (pGame.getFieldModel().isBallMoving() && playerCoordinate.equals(pGame.getFieldModel().getBallCoordinate()));
	}

	public static boolean testPlayersAbleToAct(Game pGame, Team pTeam) {
		for (Player<?> player : pTeam.getPlayers()) {
			FieldCoordinate playerCoordinate = pGame.getFieldModel().getPlayerCoordinate(player);
			PlayerState playerState = pGame.getFieldModel().getPlayerState(player);
			if ((playerCoordinate != null) && !playerCoordinate.isBoxCoordinate() && (playerState != null)
					&& playerState.isActive()) {
				return true;
			}
		}
		return false;
	}

	public static Player<?>[] findPlayersInReserveOrField(Game pGame, Team pTeam) {
		List<Player<?>> playersInBoxOrField = new ArrayList<>();
		for (Player<?> player : pTeam.getPlayers()) {
			FieldCoordinate playerCoordinate = pGame.getFieldModel().getPlayerCoordinate(player);
			if ((playerCoordinate != null) && !playerCoordinate.isBoxCoordinate()) {
				playersInBoxOrField.add(player);
			}
			PlayerState playerState = pGame.getFieldModel().getPlayerState(player);
			if ((playerState != null) && (playerState.getBase() == PlayerState.RESERVE)) {
				playersInBoxOrField.add(player);
			}
		}
		return playersInBoxOrField.toArray(new Player[playersInBoxOrField.size()]);
	}

}
