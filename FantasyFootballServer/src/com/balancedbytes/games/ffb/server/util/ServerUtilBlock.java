package com.balancedbytes.games.ffb.server.util;

import com.balancedbytes.games.ffb.DiceDecoration;
import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.mechanics.Mechanic;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.model.property.NamedProperties;
import com.balancedbytes.games.ffb.server.mechanic.RollMechanic;
import com.balancedbytes.games.ffb.util.UtilPlayer;

public class ServerUtilBlock {
	public static void updateDiceDecorations(Game pGame) {
		ActingPlayer actingPlayer = pGame.getActingPlayer();

		boolean isBlitz = PlayerAction.BLITZ_MOVE == actingPlayer.getPlayerAction();
		boolean isBlock = PlayerAction.BLOCK == actingPlayer.getPlayerAction();
		boolean isMultiBlock = (PlayerAction.MULTIPLE_BLOCK == actingPlayer.getPlayerAction());
		boolean blocksDuringMove = actingPlayer.getPlayer().hasSkillProperty(NamedProperties.blocksDuringMove);
		boolean canBlockSameTeamPlayer = actingPlayer.getPlayer().hasSkillProperty(NamedProperties.canBlockSameTeamPlayer);

		if ((actingPlayer.getPlayer() != null)
				&& (blocksDuringMove || (!actingPlayer.hasBlocked() && (isBlitz || isBlock || isMultiBlock)))) {
			pGame.getFieldModel().clearDiceDecorations();
			FieldCoordinate coordinateAttacker = pGame.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
			Team otherTeam = UtilPlayer.findOtherTeam(pGame, actingPlayer.getPlayer());
			addDiceDecorations(pGame, UtilPlayer.findAdjacentBlockablePlayers(pGame, otherTeam, coordinateAttacker));
			if (canBlockSameTeamPlayer) {
				addDiceDecorations(pGame,
						UtilPlayer.findAdjacentBlockablePlayers(pGame, actingPlayer.getPlayer().getTeam(), coordinateAttacker));
			}
		}
	}

	public static void removePlayerBlockStates(Game pGame) {
		for (Player<?> player : pGame.getPlayers()) {
			PlayerState playerState = pGame.getFieldModel().getPlayerState(player);
			if (playerState.getBase() == PlayerState.BLOCKED) {
				pGame.getFieldModel().setPlayerState(player, playerState.changeBase(PlayerState.STANDING));
			}
		}
	}

	private static void addDiceDecorations(Game pGame, Player<?>[] pPlayers) {
		ActingPlayer actingPlayer = pGame.getActingPlayer();
		if (pPlayers.length > 0) {
			boolean usingMultiBlock = (actingPlayer.getPlayerAction() == PlayerAction.MULTIPLE_BLOCK);
			for (Player<?> pPlayer : pPlayers) {
				if (!usingMultiBlock || (pPlayer != pGame.getDefender())) {
					int nrOfDice = 0;
					if (!actingPlayer.getPlayer().hasSkillProperty(NamedProperties.needsNoDiceDecorations)) {
						nrOfDice = findNrOfBlockDice(pGame, actingPlayer.getPlayer(), pPlayer,
							usingMultiBlock, false);
					}
					FieldCoordinate coordinateOpponent = pGame.getFieldModel().getPlayerCoordinate(pPlayer);
					pGame.getFieldModel().add(new DiceDecoration(coordinateOpponent, nrOfDice));
				}
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

	public static int findNrOfBlockDice(Game game, Player<?> attacker,  Player<?> defender,
			boolean usingMultiBlock, boolean successfulDauntless) {
		int nrOfDice = 0;
		if ((attacker != null) && (defender != null)) {
			nrOfDice = 1;
			RollMechanic mechanic = (RollMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.ROLL.name());

			int blockStrengthAttacker = getAttackerStrength(game, attacker, defender, usingMultiBlock);
			int defenderStrength = defender.getStrengthWithModifiers();
			if (usingMultiBlock) {
				defenderStrength += mechanic.multiBlockDefenderModifier();
			}

			if (successfulDauntless) {
				blockStrengthAttacker = Math.max(blockStrengthAttacker, defenderStrength);
			}

			blockStrengthAttacker = ServerUtilPlayer.findBlockStrength(game, attacker, blockStrengthAttacker, defender);

			int blockStrengthDefender = ServerUtilPlayer.findBlockStrength(game, defender, defenderStrength, attacker);
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
		}
		return nrOfDice;
	}
}
