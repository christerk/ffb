package com.balancedbytes.games.ffb.server.util;

import com.balancedbytes.games.ffb.DiceDecoration;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.model.property.NamedProperties;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilPlayer;

public class ServerUtilBlock {
	public static boolean updateDiceDecorations(Game pGame) {
		boolean diceDecorationsDrawn = false;
		ActingPlayer actingPlayer = pGame.getActingPlayer();

		boolean isBlitz = PlayerAction.BLITZ_MOVE == actingPlayer.getPlayerAction();
		boolean isBlock = PlayerAction.BLOCK == actingPlayer.getPlayerAction();
		boolean isMultiBlock = (PlayerAction.MULTIPLE_BLOCK == actingPlayer.getPlayerAction());
		boolean canBlockMoreThanOnce = actingPlayer.getPlayer().hasSkillWithProperty(NamedProperties.canBlockMoreThanOnce);
		boolean canBlockSameTeamPlayer = actingPlayer.getPlayer().hasSkillWithProperty(NamedProperties.canBlockSameTeamPlayer);

		if ((actingPlayer.getPlayer() != null)
				&& (canBlockMoreThanOnce || (!actingPlayer.hasBlocked() && (isBlitz || isBlock || isMultiBlock)))) {
			pGame.getFieldModel().clearDiceDecorations();
			FieldCoordinate coordinateAttacker = pGame.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
			Team otherTeam = UtilPlayer.findOtherTeam(pGame, actingPlayer.getPlayer());
			addDiceDecorations(pGame, UtilPlayer.findAdjacentBlockablePlayers(pGame, otherTeam, coordinateAttacker));
			if (canBlockSameTeamPlayer) {
				addDiceDecorations(pGame,
						UtilPlayer.findAdjacentBlockablePlayers(pGame, actingPlayer.getPlayer().getTeam(), coordinateAttacker));
			}
		}
		return diceDecorationsDrawn;
	}

	public static void removePlayerBlockStates(Game pGame) {
		for (Player<?> player : pGame.getPlayers()) {
			PlayerState playerState = pGame.getFieldModel().getPlayerState(player);
			if (playerState.getBase() == PlayerState.BLOCKED) {
				pGame.getFieldModel().setPlayerState(player, playerState.changeBase(PlayerState.STANDING));
			}
		}
	}

	private static boolean addDiceDecorations(Game pGame, Player<?>[] pPlayers) {
		ActingPlayer actingPlayer = pGame.getActingPlayer();
		if (pPlayers.length > 0) {
			int attackerStrength = actingPlayer.getStrength();
			if (actingPlayer.getPlayer().hasSkillWithProperty(NamedProperties.addStrengthOnBlitz)
					&& ((actingPlayer.getPlayerAction() == PlayerAction.BLITZ)
							|| (actingPlayer.getPlayerAction() == PlayerAction.BLITZ_MOVE))) {
				attackerStrength++;
			}
			boolean usingMultiBlock = (actingPlayer.getPlayerAction() == PlayerAction.MULTIPLE_BLOCK);
			for (int i = 0; i < pPlayers.length; i++) {
				if (!usingMultiBlock || (pPlayers[i] != pGame.getDefender())) {
					int nrOfDice = 0;
					if (!actingPlayer.getPlayer().hasSkillWithProperty(NamedProperties.useSpecialBlockRules)) {
						nrOfDice = findNrOfBlockDice(pGame, actingPlayer.getPlayer(), attackerStrength, pPlayers[i],
								usingMultiBlock);
					}
					FieldCoordinate coordinateOpponent = pGame.getFieldModel().getPlayerCoordinate(pPlayers[i]);
					pGame.getFieldModel().add(new DiceDecoration(coordinateOpponent, nrOfDice));
				}
			}
			return true;
		}
		return false;
	}

	public static int findNrOfBlockDice(Game pGame, Player<?> pAttacker, int pAttackerStrength, Player<?> pDefender,
			boolean pUsingMultiBlock) {
		int nrOfDice = 0;
		if ((pAttacker != null) && (pDefender != null)) {
			nrOfDice = 1;
			int blockStrengthAttacker = ServerUtilPlayer.findBlockStrength(pGame, pAttacker, pAttackerStrength, pDefender);
			ActingPlayer actingPlayer = pGame.getActingPlayer();
			if ((pAttacker == actingPlayer.getPlayer())
					&& ((actingPlayer.getPlayerAction() == PlayerAction.BLITZ)
							|| (actingPlayer.getPlayerAction() == PlayerAction.BLITZ_MOVE))
					&& actingPlayer.hasMoved() && pDefender.hasSkillProperty(NamedProperties.weakenOpposingBlitzer)) {
				blockStrengthAttacker -= 1;
			}
			int defenderStrength = pUsingMultiBlock ? UtilCards.getPlayerStrength(pGame, pDefender) + 2
					: UtilCards.getPlayerStrength(pGame, pDefender);
			int blockStrengthDefender = ServerUtilPlayer.findBlockStrength(pGame, pDefender, defenderStrength, pAttacker);
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

			if (pAttacker.getTeam() == pDefender.getTeam()) {
				// This can happen with Ball & Chain for example.
				nrOfDice = Math.abs(nrOfDice); // the choice is always for the coach of the attacker
			}
		}
		return nrOfDice;
	}
}
