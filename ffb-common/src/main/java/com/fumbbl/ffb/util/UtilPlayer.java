package com.fumbbl.ffb.util;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.mechanics.GameMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.TtmMechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.RosterPlayer;
import com.fumbbl.ffb.model.TargetSelectionState;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.property.ISkillProperty;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.UtilGameOption;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Kalimar
 * @author Kristian Widén
 */
public class UtilPlayer {

	public static Player<?>[] findPlayersOnPitchWithProperty(Game pGame, Team pTeam, ISkillProperty property) {
		List<Player<?>> result = new ArrayList<>();
		for (Player<?> player : pTeam.getPlayers()) {
			if (player.hasSkillProperty(property)
				&& FieldCoordinateBounds.FIELD.isInBounds(pGame.getFieldModel().getPlayerCoordinate(player))) {
				result.add(player);
			}
		}
		return result.toArray(new Player[0]);
	}

	public static Player<?>[] findAdjacentOpposingPlayersWithSkill(Game pGame, FieldCoordinate pCenterCoordinate,
																																 Skill pSkill, boolean pCheckAbleToMove) {
		ActingPlayer actingPlayer = pGame.getActingPlayer();
		return findAdjacentOpposingPlayersWithSkill(pGame, actingPlayer.getPlayer(), pCenterCoordinate, pSkill, pCheckAbleToMove);
	}

	public static Player<?>[] findAdjacentOpposingPlayersWithSkill(Game pGame, Player<?> player, FieldCoordinate pCenterCoordinate, Skill pSkill, boolean pCheckAbleToMove) {
		Team otherTeam = UtilPlayer.findOtherTeam(pGame, player);
		Player<?>[] opponents = UtilPlayer.findAdjacentPlayersWithTacklezones(pGame, otherTeam, pCenterCoordinate, false);
		Set<Player<?>> shadowingPlayers = new HashSet<>();
		for (Player<?> opponent : opponents) {
			PlayerState opponentState = pGame.getFieldModel().getPlayerState(opponent);
			if ((opponentState != null) && opponentState.hasTacklezones() && UtilCards.hasSkill(opponent, pSkill)
				&& (!pCheckAbleToMove || opponentState.isAbleToMove())) {
				shadowingPlayers.add(opponent);
			}
		}
		Player<?>[] playerArray = shadowingPlayers.toArray(new Player[0]);
		UtilPlayer.sortByPlayerNr(playerArray);
		return playerArray;
	}

	public static Player<?>[] findAdjacentOpposingPlayersWithProperty(Game pGame, FieldCoordinate pCenterCoordinate,
																																		ISkillProperty pProperty, boolean pCheckAbleToMove) {
		ActingPlayer actingPlayer = pGame.getActingPlayer();
		return findAdjacentOpposingPlayersWithProperty(pGame, actingPlayer.getPlayer(), pCenterCoordinate, pProperty, pCheckAbleToMove);
	}

	public static Player<?>[] findAdjacentOpposingPlayersWithProperty(Game pGame, Player<?> player, FieldCoordinate pCenterCoordinate, ISkillProperty pProperty, boolean pCheckAbleToMove) {
		Team otherTeam = UtilPlayer.findOtherTeam(pGame, player);
		Player<?>[] opponents = UtilPlayer.findAdjacentPlayersWithTacklezones(pGame, otherTeam, pCenterCoordinate, false);
		Set<Player<?>> shadowingPlayers = new HashSet<>();
		for (Player<?> opponent : opponents) {
			PlayerState opponentState = pGame.getFieldModel().getPlayerState(opponent);
			if ((opponentState != null) && opponentState.hasTacklezones()
				&& opponent.hasSkillProperty(pProperty)
				&& (!pCheckAbleToMove || opponentState.isAbleToMove())) {
				shadowingPlayers.add(opponent);
			}
		}
		Player<?>[] playerArray = shadowingPlayers.toArray(new Player[0]);
		UtilPlayer.sortByPlayerNr(playerArray);
		return playerArray;
	}

	public static Player<?>[] findAdjacentPronePlayers(Game pGame, Team pTeam, FieldCoordinate pCoordinate) {
		List<Player<?>> adjacentPlayers = new ArrayList<>();
		FieldModel fieldModel = pGame.getFieldModel();
		FieldCoordinate[] adjacentCoordinates = fieldModel.findAdjacentCoordinates(pCoordinate, FieldCoordinateBounds.FIELD,
			1, false);
		for (FieldCoordinate adjacentCoordinate : adjacentCoordinates) {
			Player<?> player = fieldModel.getPlayer(adjacentCoordinate);
			if ((player != null) && (player.getTeam() == pTeam)) {
				PlayerState playerState = fieldModel.getPlayerState(player);
				if ((playerState.getBase() == PlayerState.PRONE) || (playerState.getBase() == PlayerState.STUNNED)) {
					adjacentPlayers.add(player);
				}
			}
		}
		return adjacentPlayers.toArray(new Player[0]);
	}

	public static Player<?>[] findAdjacentBlockablePlayers(Game pGame, Team pTeam, FieldCoordinate pCoordinate) {
		List<Player<?>> adjacentPlayers = new ArrayList<>();
		FieldModel fieldModel = pGame.getFieldModel();
		FieldCoordinate[] adjacentCoordinates = fieldModel.findAdjacentCoordinates(pCoordinate, FieldCoordinateBounds.FIELD,
			1, false);
		for (FieldCoordinate adjacentCoordinate : adjacentCoordinates) {
			Player<?> player = fieldModel.getPlayer(adjacentCoordinate);
			if ((player != null) && (player.getTeam() == pTeam)) {
				PlayerState playerState = fieldModel.getPlayerState(player);
				if (playerState.canBeBlocked()) {
					adjacentPlayers.add(player);
				}
			}
		}
		return adjacentPlayers.toArray(new Player[0]);
	}

	public static Player<?>[] findAdjacentStandingOrPronePlayers(Game pGame, Team pTeam, FieldCoordinate pCoordinate) {
		List<Player<?>> adjacentPlayers = new ArrayList<>();
		FieldModel fieldModel = pGame.getFieldModel();
		FieldCoordinate[] adjacentCoordinates = fieldModel.findAdjacentCoordinates(pCoordinate, FieldCoordinateBounds.FIELD,
			1, false);
		for (FieldCoordinate adjacentCoordinate : adjacentCoordinates) {
			Player<?> player = fieldModel.getPlayer(adjacentCoordinate);
			if ((player != null) && (player.getTeam() == pTeam)) {
				PlayerState playerState = fieldModel.getPlayerState(player);
				if (!playerState.isStunned()) {
					adjacentPlayers.add(player);
				}
			}
		}
		return adjacentPlayers.toArray(new Player[0]);
	}

	public static Player<?>[] findAdjacentPlayers(Game pGame, Team pTeam, FieldCoordinate pCoordinate) {
		List<Player<?>> adjacentPlayers = new ArrayList<>();
		FieldModel fieldModel = pGame.getFieldModel();
		FieldCoordinate[] adjacentCoordinates = fieldModel.findAdjacentCoordinates(pCoordinate, FieldCoordinateBounds.FIELD,
			1, false);
		for (FieldCoordinate adjacentCoordinate : adjacentCoordinates) {
			Player<?> player = fieldModel.getPlayer(adjacentCoordinate);
			if ((player != null) && (player.getTeam() == pTeam)) {
				adjacentPlayers.add(player);
			}
		}
		return adjacentPlayers.toArray(new Player[0]);
	}

	public static Player<?>[] findAdjacentPlayersWithTacklezones(Game pGame, Team pTeam, FieldCoordinate pCoordinate,
																															 boolean pWithStartCoordinate) {
		List<Player<?>> adjacentPlayers = new ArrayList<>();
		FieldModel fieldModel = pGame.getFieldModel();
		FieldCoordinate[] adjacentCoordinates = fieldModel.findAdjacentCoordinates(pCoordinate, FieldCoordinateBounds.FIELD,
			1, pWithStartCoordinate);
		for (FieldCoordinate adjacentCoordinate : adjacentCoordinates) {
			Player<?> player = fieldModel.getPlayer(adjacentCoordinate);
			if ((player != null) && (player.getTeam() == pTeam)) {
				PlayerState playerState = fieldModel.getPlayerState(player);
				if (playerState.hasTacklezones()) {
					adjacentPlayers.add(player);
				}
			}
		}
		return adjacentPlayers.toArray(new Player[0]);
	}

	public static Player<?>[] findAdjacentPlayersToFeedOn(Game pGame, Team pTeam, FieldCoordinate pCoordinate) {
		List<Player<?>> adjacentPlayers = new ArrayList<>();
		FieldModel fieldModel = pGame.getFieldModel();
		FieldCoordinate[] adjacentCoordinates = fieldModel.findAdjacentCoordinates(pCoordinate, FieldCoordinateBounds.FIELD,
			1, false);
		for (FieldCoordinate adjacentCoordinate : adjacentCoordinates) {
			Player<?> player = fieldModel.getPlayer(adjacentCoordinate);
			if ((player != null) && (player.getTeam() == pTeam) && (player instanceof RosterPlayer)
				&& ((RosterPlayer) player).getPosition().isThrall()) {
				adjacentPlayers.add(player);
			}
		}
		return adjacentPlayers.toArray(new Player[0]);
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
		return playerList.toArray(new Player[0]);
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
		return playerList.toArray(new Player[0]);
	}

	public static int findFoulAssists(Game pGame, Player<?> pAttacker, Player<?> pDefender) {
		GameMechanic mechanic = (GameMechanic) pGame.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.GAME.name());

		int foulAssists = 0;
		FieldCoordinate coordinateDefender = pGame.getFieldModel().getPlayerCoordinate(pDefender);
		for (Player<?> offensiveAssist : findAdjacentPlayersWithTacklezones(pGame, pAttacker.getTeam(), coordinateDefender,
			false)) {
			if (offensiveAssist != pAttacker) {
				FieldCoordinate coordinateAssist = pGame.getFieldModel().getPlayerCoordinate(offensiveAssist);
				Player<?>[] adjacentPlayersWithTacklezones = findAdjacentPlayersWithTacklezones(pGame, pDefender.getTeam(), coordinateAssist, false);
				boolean guardIsCanceled = mechanic.allowsCancellingGuard(pGame.getTurnMode())
					&& Arrays.stream(adjacentPlayersWithTacklezones)
					.flatMap(player -> player.getSkillsIncludingTemporaryOnes().stream())
					.anyMatch(skill -> skill.canCancel(NamedProperties.assistsFoulsInTacklezones));
				if ((adjacentPlayersWithTacklezones.length < 1)
					|| (UtilGameOption.isOptionEnabled(pGame, GameOptionId.SNEAKY_GIT_AS_FOUL_GUARD)
					&& offensiveAssist.hasSkillProperty(NamedProperties.canAlwaysAssistFouls))
					|| (offensiveAssist.hasSkillProperty(NamedProperties.assistsFoulsInTacklezones) && !guardIsCanceled)) {
					foulAssists++;
				}
			}
		}
		FieldCoordinate coordinateAttacker = pGame.getFieldModel().getPlayerCoordinate(pAttacker);
		for (Player<?> defensiveAssist : findAdjacentPlayersWithTacklezones(pGame, pDefender.getTeam(), coordinateAttacker,
			false)) {
			if (defensiveAssist != pDefender) {
				FieldCoordinate coordinateAssist = pGame.getFieldModel().getPlayerCoordinate(defensiveAssist);
				if (defensiveAssist.hasSkillProperty(NamedProperties.assistsFoulsInTacklezones) || findAdjacentPlayersWithTacklezones(pGame, pAttacker.getTeam(), coordinateAssist, false).length < 2) {
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
		return findTacklezonePlayers(pGame, pPlayer).length;
	}

	public static Player<?>[] findTacklezonePlayers(Game pGame, Player<?> pPlayer) {
		Team otherTeam = findOtherTeam(pGame, pPlayer);
		FieldCoordinate playerCoordinate = pGame.getFieldModel().getPlayerCoordinate(pPlayer);
		return findAdjacentPlayersWithTacklezones(pGame, otherTeam, playerCoordinate, false);
	}

	public static void refreshPlayersForTurnStart(Game pGame) {
		FieldModel fieldModel = pGame.getFieldModel();
		Player<?>[] players = pGame.getPlayers();
		GameMechanic mechanic = (GameMechanic) pGame.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.GAME.name());

		Set<String> enhancementsToRemove = mechanic.enhancementsToRemoveAtEndOfTurn(pGame.getFactory(FactoryType.Factory.SKILL));
		Set<String> enhancementsToRemoveWhenNotSettingActive = mechanic.enhancementsToRemoveAtEndOfTurnWhenNotSettingActive(pGame.getFactory(FactoryType.Factory.SKILL));

		pGame.clearRolledOver();

		for (Player<?> player : players) {
			boolean playerOnTeamFromLastTurn = player.getTeam() != pGame.getTeamHome() && pGame.isHomePlaying();
			boolean setActive = playerOnTeamFromLastTurn || !player.hasSkillProperty(NamedProperties.hasToMissTurn);

			if (!setActive) {
				enhancementsToRemoveWhenNotSettingActive.forEach(enhancement -> pGame.getFieldModel().removeSkillEnhancements(player, enhancement));
			}

			enhancementsToRemove.forEach(enhancement -> pGame.getFieldModel().removeSkillEnhancements(player, enhancement));
			player.resetUsedSkills(SkillUsageType.ONCE_PER_TURN, pGame);
			player.resetUsedSkills(SkillUsageType.ONCE_PER_TURN_BY_TEAM_MATE, pGame);
			PlayerState newPlayerState = null;
			PlayerState oldPlayerState = fieldModel.getPlayerState(player);
			switch (oldPlayerState.getBase()) {
				case PlayerState.BLOCKED:
				case PlayerState.MOVING:
				case PlayerState.FALLING:
				case PlayerState.HIT_ON_GROUND:
					newPlayerState = oldPlayerState.changeBase(PlayerState.STANDING).changeActive(setActive);
					break;
				case PlayerState.PRONE:
				case PlayerState.STANDING:
					newPlayerState = oldPlayerState.changeActive(setActive);
					break;
				case PlayerState.STUNNED:
					if ((pGame.isHomePlaying() && pGame.getTeamHome().hasPlayer(player))
						|| (!pGame.isHomePlaying() && pGame.getTeamAway().hasPlayer(player))) {
						newPlayerState = oldPlayerState.changeBase(PlayerState.PRONE).changeActive(false);
						pGame.rollOver(player);
					}
					break;
				default:
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
				fieldModel.setPlayerState(player, newPlayerState);
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
		return canGaze(pGame, pPlayer, NamedProperties.inflictsConfusion);
	}

	public static boolean canGaze(Game pGame, Player<?> pPlayer, ISkillProperty property) {
		FieldCoordinate playerCoordinate = pGame.getFieldModel().getPlayerCoordinate(pPlayer);
		Team otherTeam = UtilPlayer.findOtherTeam(pGame, pPlayer);
		PlayerState playerState = pGame.getFieldModel().getPlayerState(pPlayer);
		GameMechanic mechanic = (GameMechanic) pGame.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.GAME.name());

		Skill skill = pPlayer.getSkillWithProperty(property);

		boolean usedSkill = skill == null || pPlayer.isUsed(skill);

		if (usedSkill) {
			return false;
		} else if (!playerState.isActive()) {
			return false;
		} else {
			return mechanic.declareGazeActionAtStart() || (UtilPlayer.findAdjacentPlayersWithTacklezones(pGame, otherTeam, playerCoordinate, false).length > 0);
		}
	}

	public static boolean isNextToGazeTarget(Game game, Player<?> player) {
		FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(player);
		Team otherTeam = UtilPlayer.findOtherTeam(game, player);
		return Arrays.stream(
				UtilPlayer.findAdjacentPlayersWithTacklezones(game, otherTeam, playerCoordinate, false))
			.anyMatch(adjacentPlayer -> isAdjacentGazeTarget(game, adjacentPlayer));
	}

	public static boolean isAdjacentGazeTarget(Game game, Player<?> player) {
		FieldModel fieldModel = game.getFieldModel();
		TargetSelectionState targetSelectionState = fieldModel.getTargetSelectionState();
		if (targetSelectionState != null && targetSelectionState.isSelected()) {
			boolean isTargetedPlayer = player.getId().equalsIgnoreCase(targetSelectionState.getSelectedPlayerId());
			ActingPlayer actingPlayer = game.getActingPlayer();
			boolean isAdjacent = fieldModel.getPlayerCoordinate(actingPlayer.getPlayer()).isAdjacent(fieldModel.getPlayerCoordinate(player));

			return isTargetedPlayer && isAdjacent;
		}
		return false;
	}


	public static boolean canFoul(Game pGame, Player<?> pPlayer) {
		FieldModel fieldModel = pGame.getFieldModel();
		FieldCoordinate playerCoordinate = fieldModel.getPlayerCoordinate(pPlayer);
		Team otherTeam = UtilPlayer.findOtherTeam(pGame, pPlayer);
		return (
			(UtilPlayer.findAdjacentPronePlayers(pGame, otherTeam, playerCoordinate).length > 0));
	}

	public static boolean isBallAvailable(Game pGame, Player<?> pPlayer) {
		return ((pPlayer != null) && (pGame != null) && pGame.getFieldModel().isBallInPlay()
			&& (pGame.getFieldModel().isBallMoving()
			|| (pGame.getFieldModel().getBallCoordinate() != null && pGame.getFieldModel().getBallCoordinate().equals(pGame.getFieldModel().getPlayerCoordinate(pPlayer)))));
	}

	public static boolean hasBall(Game pGame, Player<?> pPlayer) {
		return ((pPlayer != null) && (pGame != null) && pGame.getFieldModel().isBallInPlay()
			&& !pGame.getFieldModel().isBallMoving()
			&& pGame.getFieldModel().getBallCoordinate() != null
			&& pGame.getFieldModel().getBallCoordinate().equals(pGame.getFieldModel().getPlayerCoordinate(pPlayer)));
	}

	public static Player<?>[] findKickableTeamMates(Game pGame, Player<?> pKicker) {
		List<Player<?>> kickablePlayers = new ArrayList<>();
		FieldModel fieldModel = pGame.getFieldModel();
		FieldCoordinate kickerCoordinate = fieldModel.getPlayerCoordinate(pKicker);
		TtmMechanic mechanic = (TtmMechanic) pGame.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.TTM.name());
		Player<?>[] adjacentPlayers = findAdjacentPlayersWithTacklezones(pGame, pKicker.getTeam(), kickerCoordinate, false);
		for (Player<?> adjacentPlayer : adjacentPlayers) {
			if (mechanic.canBeKicked(pGame, adjacentPlayer)) {
				kickablePlayers.add(adjacentPlayer);
			}
		}
		return kickablePlayers.toArray(new Player[0]);
	}

	public static boolean canThrowTeamMate(Game pGame, Player<?> pThrower, boolean pCheckPassUsed) {
		TtmMechanic mechanic = (TtmMechanic) pGame.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.TTM.name());
		return ((pThrower != null) && (!pCheckPassUsed || !pGame.getTurnData().isPassUsed())
			&& mechanic.canThrow(pThrower)
			&& (mechanic.findThrowableTeamMates(pGame, pThrower).length > 0));
	}

	public static boolean canKickTeamMate(Game pGame, Player<?> pKicker, boolean pCheckBlitzUsed) {
		return ((pKicker != null) && (!pCheckBlitzUsed || !pGame.getTurnData().isBlitzUsed())
			&& pKicker.hasSkillProperty(NamedProperties.canKickTeamMates)
			&& (UtilPlayer.findKickableTeamMates(pGame, pKicker).length > 0));
	}

	public static boolean isBlockable(Game pGame, Player<?> pPlayer) {
		ActingPlayer actingPlayer = pGame.getActingPlayer();
		FieldCoordinate defenderCoordinate = pGame.getFieldModel().getPlayerCoordinate(pPlayer);
		FieldCoordinate attackerCoordinate = pGame.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
		return isValidBlitzTarget(pGame, pPlayer) && defenderCoordinate.isAdjacent(attackerCoordinate)
			&& (pGame.getFieldModel().getDiceDecoration(defenderCoordinate) != null);
	}

	public static boolean isValidBlitzTarget(Game pGame, Player<?> pPlayer) {
		if (pPlayer != null) {
			FieldModel fieldModel = pGame.getFieldModel();
			PlayerState defenderState = fieldModel.getPlayerState(pPlayer);
			FieldCoordinate defenderCoordinate = fieldModel.getPlayerCoordinate(pPlayer);
			return (defenderState.canBeBlocked() && pGame.getTeamAway().hasPlayer(pPlayer) && (defenderCoordinate != null)
				&& (fieldModel.getTargetSelectionState() == null || pPlayer.getId().equals(fieldModel.getTargetSelectionState().getSelectedPlayerId())));
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
				&& !pPlayer.hasSkillProperty(NamedProperties.preventBeingFouled));
		}
		return foulable;
	}

	public static boolean isKickable(Game pGame, Player<?> pPlayer) {
		boolean kickable = false;
		ActingPlayer actingPlayer = pGame.getActingPlayer();
		if (pPlayer != null) {
			PlayerState defenderState = pGame.getFieldModel().getPlayerState(pPlayer);
			FieldCoordinate defenderCoordinate = pGame.getFieldModel().getPlayerCoordinate(pPlayer);
			FieldCoordinate attackerCoordinate = pGame.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
			kickable = (defenderState.isProneOrStunned()
				&& pGame.getTeamAway().hasPlayer(pPlayer) && (defenderCoordinate != null)
				&& defenderCoordinate.isAdjacent(attackerCoordinate));
		}
		return kickable;
	}

	public static void sortByPlayerNr(Player<?>[] pPlayerArray) {
		Arrays.sort(pPlayerArray, Comparator.comparingInt(Player::getNr));
	}

	public static boolean isNextMoveGoingForIt(Game pGame) {
		boolean nextMoveGoingForIt = false;
		ActingPlayer actingPlayer = pGame.getActingPlayer();
		Player<?> player = actingPlayer.getPlayer();
		if (player != null) {
			if ((pGame.getTurnMode() == TurnMode.KICKOFF_RETURN) || (pGame.getTurnMode() == TurnMode.PASS_BLOCK)) {
				return false;
			} else if (actingPlayer.isStandingUp() && !actingPlayer.hasActed()
				&& !actingPlayer.getPlayer().hasSkillProperty(NamedProperties.canStandUpForFree)) {
				nextMoveGoingForIt = (3 >= player.getMovementWithModifiers());
			} else {
				if (actingPlayer.isJumping()) {
					nextMoveGoingForIt = ((actingPlayer.getCurrentMove() + 1) >= player.getMovementWithModifiers());
				} else {
					nextMoveGoingForIt = (actingPlayer.getCurrentMove() >= player.getMovementWithModifiers());
				}
			}
		}
		return nextMoveGoingForIt;
	}

	public static boolean isNextMovePossible(Game pGame, boolean jumping) {
		boolean movePossible = false;
		ActingPlayer actingPlayer = pGame.getActingPlayer();
		PlayerState playerState = pGame.getFieldModel().getPlayerState(actingPlayer.getPlayer());
		if ((playerState != null) && playerState.isAbleToMove()) {
			if ((pGame.getTurnMode() == TurnMode.KICKOFF_RETURN) || (pGame.getTurnMode() == TurnMode.PASS_BLOCK)) {
				movePossible = jumping ? (actingPlayer.getCurrentMove() < 2) : (actingPlayer.getCurrentMove() < 3);
			} else {
				int extraMove = 0;
				if (actingPlayer.isGoingForIt()) {
					extraMove = 2;
					if (actingPlayer.getPlayer().hasSkillProperty(NamedProperties.canMakeAnExtraGfi)) {
						extraMove++;
					}
					if (UtilCards.hasUnusedSkillWithProperty(actingPlayer, NamedProperties.canMakeAnExtraGfiOnce)) {
						extraMove++;
					}
					if (jumping) {
						extraMove--;
					}
				}
				movePossible = (actingPlayer
					.getCurrentMove() < (actingPlayer.getPlayer().getMovementWithModifiers() + extraMove));
			}
		}
		return movePossible;
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
		return playersInBoxOrField.toArray(new Player[0]);
	}

}
