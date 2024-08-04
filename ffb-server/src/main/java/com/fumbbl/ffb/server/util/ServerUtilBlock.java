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
import com.fumbbl.ffb.server.mechanic.RollMechanic;
import com.fumbbl.ffb.util.UtilPlayer;

public class ServerUtilBlock {

	public static void updateDiceDecorations(Game pGame) {
		updateDiceDecorations(pGame, false);
	}

	public static void updateDiceDecorations(Game pGame, boolean decorateForFrenzyBlitz) {
		ActingPlayer actingPlayer = pGame.getActingPlayer();

		PlayerAction playerAction = actingPlayer.getPlayerAction();
		boolean isBlitz = PlayerAction.BLITZ_MOVE == playerAction || (PlayerAction.BLITZ == playerAction && decorateForFrenzyBlitz);
		boolean isCarnage = PlayerAction.MAXIMUM_CARNAGE == playerAction;
		boolean isPutrid = playerAction != null && playerAction.isPutrid();
		boolean isBlock = PlayerAction.BLOCK == playerAction;
		boolean isMultiBlock = (PlayerAction.MULTIPLE_BLOCK == playerAction);
		boolean blocksDuringMove = actingPlayer.getPlayer().hasSkillProperty(NamedProperties.blocksDuringMove);
		boolean canBlockSameTeamPlayer = actingPlayer.getPlayer().hasSkillProperty(NamedProperties.canBlockSameTeamPlayer);
		boolean kicksDowned = playerAction != null && playerAction.isKickingDowned();

		if ((actingPlayer.getPlayer() != null)
			&& (blocksDuringMove
			|| ((!actingPlayer.hasBlocked() || decorateForFrenzyBlitz || pGame.getTurnMode().forceDiceDecorationUpdate())
			&& (isBlitz || isBlock || isMultiBlock || kicksDowned))
			|| isCarnage || isPutrid)
		) {
			pGame.getFieldModel().clearDiceDecorations();
			FieldCoordinate coordinateAttacker = pGame.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
			Team otherTeam = UtilPlayer.findOtherTeam(pGame, actingPlayer.getPlayer());
			Player<?>[] adjacentPlayers = kicksDowned ? UtilPlayer.findAdjacentPronePlayers(pGame, otherTeam, coordinateAttacker) : UtilPlayer.findAdjacentBlockablePlayers(pGame, otherTeam, coordinateAttacker);
			addDiceDecorations(pGame, adjacentPlayers);
			if (canBlockSameTeamPlayer) {
				addDiceDecorations(pGame,
					UtilPlayer.findAdjacentBlockablePlayers(pGame, actingPlayer.getPlayer().getTeam(), coordinateAttacker));
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

	private static void addDiceDecorations(Game pGame, Player<?>[] pPlayers) {
		ActingPlayer actingPlayer = pGame.getActingPlayer();
		if (pPlayers.length > 0) {
			boolean usingMultiBlock = (actingPlayer.getPlayerAction() == PlayerAction.MULTIPLE_BLOCK);
			TargetSelectionState targetSelectionState = pGame.getFieldModel().getTargetSelectionState();
			boolean performsBlitz = targetSelectionState != null && targetSelectionState.isSelected();
			for (Player<?> pPlayer : pPlayers) {
				int nrOfDice = 0;
				BlockKind blockKind = null;
				if (actingPlayer.getPlayerAction().isPutridBlock()) {
					blockKind = BlockKind.VOMIT;
				} else if (actingPlayer.getPlayerAction().isKickingDowned()) {
					blockKind = BlockKind.CHAINSAW;
				} else {
					boolean isBystanderDuringBlitz = performsBlitz && !pPlayer.getId().equals(targetSelectionState.getSelectedPlayerId());
					if (isBystanderDuringBlitz || pPlayer.getId().equals(pGame.getLastDefenderId())) {
						continue;
					}
					if (!actingPlayer.getPlayer().hasSkillProperty(NamedProperties.needsNoDiceDecorations)) {
						boolean addBlockDie = targetSelectionState != null && targetSelectionState.getUsedSkills().stream()
							.anyMatch(skill -> skill.hasSkillProperty(NamedProperties.canAddBlockDie));

						nrOfDice = findNrOfBlockDice(pGame, actingPlayer.getPlayer(), pPlayer,
							usingMultiBlock, false, false, addBlockDie).getLeft();
					}
				}
				FieldCoordinate coordinateOpponent = pGame.getFieldModel().getPlayerCoordinate(pPlayer);
				pGame.getFieldModel().add(new DiceDecoration(coordinateOpponent, nrOfDice, blockKind));
			}
		}
	}

	public static int getAttackerStrength(Game game, Player<?> attacker, Player<?> defender, boolean isMultiBlock) {
		int strength = attacker.getStrengthWithModifiers();

		if (isMultiBlock) {
			RollMechanic mechanic = (RollMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.ROLL.name());
			strength += mechanic.multiBlockAttackerModifier();
		}

		ActingPlayer actingPlayer = game.getActingPlayer();
		if ((actingPlayer.getPlayerAction() == PlayerAction.BLITZ || actingPlayer.getPlayerAction() == PlayerAction.BLITZ_MOVE)
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

	public static int findNrOfBlockDice(Game game, Player<?> attacker, Player<?> defender,
																			boolean usingMultiBlock, boolean successfulDauntless) {

		return findNrOfBlockDice(game, attacker, defender, usingMultiBlock, successfulDauntless, false, false).getLeft();
	}

	public static Pair<Integer, Boolean> findNrOfBlockDice(Game game, Player<?> attacker, Player<?> defender,
																												 boolean usingMultiBlock, boolean successfulDauntless,
																												 boolean doubleTargetStrength, boolean addBlockDie) {
		int nrOfDice = 0;
		boolean addedDie = false;
		if ((attacker != null) && (defender != null)) {
			nrOfDice = 1;
			RollMechanic mechanic = (RollMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.ROLL.name());

			int blockStrengthAttacker = getAttackerStrength(game, attacker, defender, usingMultiBlock);
			int defenderStrength = defender.getStrengthWithModifiers();
			if (usingMultiBlock) {
				defenderStrength += mechanic.multiBlockDefenderModifier();
			}

			if (successfulDauntless) {
				blockStrengthAttacker = Math.max(blockStrengthAttacker, doubleTargetStrength ? 2 * defenderStrength : defenderStrength);
			}

			blockStrengthAttacker = ServerUtilPlayer.findBlockStrength(game, attacker, blockStrengthAttacker, defender, usingMultiBlock);

			int blockStrengthDefender = ServerUtilPlayer.findBlockStrength(game, defender, defenderStrength, attacker, usingMultiBlock);
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
