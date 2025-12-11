package com.fumbbl.ffb.server.util;

import com.fumbbl.ffb.DiceDecoration;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.Pair;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.BlockKind;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.TargetSelectionState;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.mechanic.RollMechanic;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.Set;

public class ServerUtilBlock {

	public static void updateDiceDecorations(GameState gameState) {
		updateDiceDecorations(gameState, false);
	}

	public static void updateDiceDecorations(GameState gameState, boolean decorateForFrenzyBlitz) {
		Game game = gameState.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();

		PlayerAction playerAction = actingPlayer.getPlayerAction();
		boolean isBlitz =
			PlayerAction.BLITZ_MOVE == playerAction || (PlayerAction.BLITZ == playerAction && decorateForFrenzyBlitz);
		boolean isCarnage = PlayerAction.MAXIMUM_CARNAGE == playerAction;
		boolean isPutrid = playerAction != null && playerAction.isPutrid();
		boolean isBlock = playerAction != null && playerAction.isBlockAction();
		boolean isMultiBlock = (PlayerAction.MULTIPLE_BLOCK == playerAction);
		boolean blocksDuringMove = actingPlayer.getPlayer().hasSkillProperty(NamedProperties.blocksDuringMove);
		boolean canBlockSameTeamPlayer = actingPlayer.getPlayer().hasSkillProperty(NamedProperties.canBlockSameTeamPlayer);
		boolean kicksDowned = playerAction != null && playerAction.isKickingDowned();
		boolean viciousVines = PlayerAction.VICIOUS_VINES == playerAction;

		if ((actingPlayer.getPlayer() != null)
			&& (blocksDuringMove
			|| ((!actingPlayer.hasBlocked() || decorateForFrenzyBlitz || game.getTurnMode().forceDiceDecorationUpdate())
			&& (isBlitz || isBlock || isMultiBlock || kicksDowned))
			|| isCarnage || isPutrid)
		) {
			game.getFieldModel().clearDiceDecorations();
			FieldCoordinate coordinateAttacker = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
			Team otherTeam = UtilPlayer.findOtherTeam(game, actingPlayer.getPlayer());
			Player<?>[] targetPlayers;
			if (kicksDowned) {
				targetPlayers = UtilPlayer.findAdjacentPronePlayers(game, otherTeam, coordinateAttacker);
			} else if (viciousVines) {
				targetPlayers = UtilPlayer.findNonAdjacentBlockablePlayersTwoSquaresAway(game, otherTeam, coordinateAttacker);
			} else {
				targetPlayers = UtilPlayer.findAdjacentBlockablePlayers(game, otherTeam, coordinateAttacker);
			}
			addDiceDecorations(gameState, targetPlayers);
			if (canBlockSameTeamPlayer) {
				addDiceDecorations(gameState,
					UtilPlayer.findAdjacentBlockablePlayers(game, actingPlayer.getPlayer().getTeam(), coordinateAttacker));
			}
		}
	}

	public static void removePlayerBlockStates(Game pGame, PlayerState oldDefenderState) {
		for (Player<?> player : pGame.getPlayers()) {
			PlayerState playerState = pGame.getFieldModel().getPlayerState(player);
			if (playerState.getBase() == PlayerState.BLOCKED) {

				int newBase = PlayerState.STANDING;

				if (oldDefenderState != null && oldDefenderState.isProneOrStunned() && player == pGame.getDefender()) {
					newBase = oldDefenderState.getBase();
				}
				pGame.getFieldModel().setPlayerState(player, playerState.changeBase(newBase));
			}
		}
	}

	private static void addDiceDecorations(GameState gameState, Player<?>[] pPlayers) {
		Game game = gameState.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (pPlayers.length > 0) {
			boolean usingMultiBlock = (actingPlayer.getPlayerAction() == PlayerAction.MULTIPLE_BLOCK);
			TargetSelectionState targetSelectionState = game.getFieldModel().getTargetSelectionState();
			boolean performsBlitz = targetSelectionState != null && targetSelectionState.isSelected();
			for (Player<?> pPlayer : pPlayers) {
				int nrOfDice = 0;
				BlockKind blockKind = null;
				if (actingPlayer.getPlayerAction().isPutridBlock()) {
					blockKind = BlockKind.VOMIT;
				} else if (actingPlayer.getPlayerAction().isKickingDowned()) {
					blockKind = BlockKind.CHAINSAW;
				} else {
					boolean isBystanderDuringBlitz =
						performsBlitz && !pPlayer.getId().equals(targetSelectionState.getSelectedPlayerId());
					if (isBystanderDuringBlitz || pPlayer.getId().equals(game.getLastDefenderId())) {
						continue;
					}
					if (!actingPlayer.getPlayer().hasSkillProperty(NamedProperties.needsNoDiceDecorations)) {
						boolean addBlockDie = targetSelectionState != null && targetSelectionState.getUsedSkills().stream()
							.anyMatch(skill -> skill.hasSkillProperty(NamedProperties.canAddBlockDie));

						nrOfDice = findNrOfBlockDice(gameState, actingPlayer.getPlayer(), pPlayer,
							usingMultiBlock, false, false, addBlockDie).getLeft();
					}
				}
				FieldCoordinate coordinateOpponent = game.getFieldModel().getPlayerCoordinate(pPlayer);
				game.getFieldModel().add(new DiceDecoration(coordinateOpponent, nrOfDice, blockKind));
			}
		}
	}

	public static int getAttackerStrength(Game game, Player<?> attacker, Player<?> defender, boolean isMultiBlock) {
		int strength = attacker.getStrengthWithModifiers();

		if (isMultiBlock) {
			RollMechanic mechanic =
				(RollMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.ROLL.name());
			strength += mechanic.multiBlockAttackerModifier();
		}

		ActingPlayer actingPlayer = game.getActingPlayer();
		if ((actingPlayer.getPlayerAction() == PlayerAction.BLITZ ||
			actingPlayer.getPlayerAction() == PlayerAction.BLITZ_MOVE)
			&& actingPlayer.hasMoved() && defender.hasSkillProperty(NamedProperties.weakenOpposingBlitzer)) {
			strength--;
		}

		if (actingPlayer.getPlayer().hasSkillProperty(NamedProperties.addStrengthOnBlitz)
			&& ((actingPlayer.getPlayerAction() == PlayerAction.BLITZ)
			|| (actingPlayer.getPlayerAction() == PlayerAction.BLITZ_MOVE))) {
			strength++;
		}

		return Math.max(strength, 1);
	}

	public static int findNrOfBlockDice(GameState gameState, Player<?> attacker, Player<?> defender,
																			boolean usingMultiBlock, boolean successfulDauntless) {

		return findNrOfBlockDice(gameState, attacker, defender, usingMultiBlock, successfulDauntless, false,
			false).getLeft();
	}

	public static Pair<Integer, Boolean> findNrOfBlockDice(GameState gameState, Player<?> attacker, Player<?> defender,
																												 boolean usingMultiBlock, boolean successfulDauntless,
																												 boolean doubleTargetStrength, boolean addBlockDie) {
		Game game = gameState.getGame();
		int nrOfDice = 0;
		boolean addedDie = false;
		if ((attacker != null) && (defender != null)) {
			nrOfDice = 1;
			RollMechanic mechanic =
				(RollMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.ROLL.name());

			int blockStrengthAttacker = getAttackerStrength(game, attacker, defender, usingMultiBlock);
			int defenderStrength = defender.getStrengthWithModifiers();
			if (usingMultiBlock) {
				defenderStrength += mechanic.multiBlockDefenderModifier();
			}

			if (successfulDauntless) {
				blockStrengthAttacker =
					Math.max(blockStrengthAttacker, doubleTargetStrength ? 2 * defenderStrength : defenderStrength);
			}

			blockStrengthAttacker =
				ServerUtilPlayer.findBlockStrength(game, attacker, blockStrengthAttacker, defender, usingMultiBlock);


			Set<String> multiBlockTargets = gameState.getGame().getMultiBlockTargets();
			// add additional assist when:
			// - effect is present
			// - either no multi block
			// - or no multiblock target yet selected (we are only showing decorations)
			// - or only this player is selected (also showing decorations but keep the assist for the "first" target)
			// - or two multiblock targets are selected
			//
			// if two players are selected we are actually blocking, so we can simply check for the existing effect as it
			// is removed after the "first" block
			if (gameState.hasAdditionalAssist(game.getActingTeam().getId()) && (
				!usingMultiBlock || multiBlockTargets.isEmpty() || multiBlockTargets.size() == 2 ||
				multiBlockTargets.size() == 1 && multiBlockTargets.contains(defender.getId()))) {
				blockStrengthAttacker += 1;
			}

			int blockStrengthDefender =
				ServerUtilPlayer.findBlockStrength(game, defender, defenderStrength, attacker, usingMultiBlock);
			if (blockStrengthAttacker > blockStrengthDefender) {
				nrOfDice = 2;
			}
			if (blockStrengthAttacker > (2 * blockStrengthDefender)) {
				nrOfDice = 3;
			}
			if (blockStrengthAttacker < blockStrengthDefender) {
				nrOfDice = -2;
			}
			if ((blockStrengthAttacker * 2) < blockStrengthDefender) {
				nrOfDice = -3;
			}

			if (attacker.getTeam() == defender.getTeam()) {
				// This can happen with Ball & Chain for example.
				nrOfDice = Math.abs(nrOfDice); // the choice is always for the coach of the attacker
			}

			if (addBlockDie && (nrOfDice == 2 || nrOfDice == 1)) {
				addedDie = true;
				nrOfDice++;
			}
		}
		return new Pair<>(nrOfDice, addedDie);
	}
}
