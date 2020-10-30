package com.balancedbytes.games.ffb.server.util;

import com.balancedbytes.games.ffb.Card;
import com.balancedbytes.games.ffb.DiceDecoration;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilPlayer;

public class ServerUtilBlock {
  public static boolean updateDiceDecorations(Game pGame) {
    boolean diceDecorationsDrawn = false;
    ActingPlayer actingPlayer = pGame.getActingPlayer();
    if ((actingPlayer.getPlayer() != null) && (UtilCards.hasSkill(pGame, actingPlayer, Skill.BALL_AND_CHAIN) || (!actingPlayer.hasBlocked() && ((PlayerAction.BLITZ_MOVE == actingPlayer.getPlayerAction()) || (PlayerAction.BLOCK == actingPlayer.getPlayerAction()) || (PlayerAction.MULTIPLE_BLOCK == actingPlayer.getPlayerAction()))))) {
      pGame.getFieldModel().clearDiceDecorations();
      FieldCoordinate coordinateAttacker = pGame.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
      Team otherTeam = UtilPlayer.findOtherTeam(pGame, actingPlayer.getPlayer());
      addDiceDecorations(pGame, UtilPlayer.findAdjacentBlockablePlayers(pGame, otherTeam, coordinateAttacker));
      if (UtilCards.hasSkill(pGame, actingPlayer, Skill.BALL_AND_CHAIN)) {
        addDiceDecorations(pGame, UtilPlayer.findAdjacentBlockablePlayers(pGame, actingPlayer.getPlayer().getTeam(), coordinateAttacker));
      }
    }
    return diceDecorationsDrawn;
  }
  
  public static void removePlayerBlockStates(Game pGame) {
    for (Player player : pGame.getPlayers()) {
      PlayerState playerState = pGame.getFieldModel().getPlayerState(player);
      if (playerState.getBase() == PlayerState.BLOCKED) {
        pGame.getFieldModel().setPlayerState(player, playerState.changeBase(PlayerState.STANDING));
      }
    }
  }  
  
  private static boolean addDiceDecorations(Game pGame, Player[] pPlayers) {
    ActingPlayer actingPlayer = pGame.getActingPlayer();
    if (pPlayers.length > 0) {
      int attackerStrength = actingPlayer.getStrength();
      if (UtilCards.hasSkill(pGame, actingPlayer, Skill.HORNS) && ((actingPlayer.getPlayerAction() == PlayerAction.BLITZ) || (actingPlayer.getPlayerAction() == PlayerAction.BLITZ_MOVE))) {
        attackerStrength++;
      }
      boolean usingMultiBlock = (actingPlayer.getPlayerAction() == PlayerAction.MULTIPLE_BLOCK);
      for (int i = 0; i < pPlayers.length; i++) {
        if (!usingMultiBlock || (pPlayers[i] != pGame.getDefender())) {
          int nrOfDice = 0;
          if (!UtilCards.hasSkill(pGame, actingPlayer, Skill.CHAINSAW)) {
            nrOfDice = findNrOfBlockDice(pGame, actingPlayer.getPlayer(), attackerStrength, pPlayers[i], usingMultiBlock);
          }
          FieldCoordinate coordinateOpponent = pGame.getFieldModel().getPlayerCoordinate(pPlayers[i]);
          pGame.getFieldModel().add(new DiceDecoration(coordinateOpponent, nrOfDice));
        }
      }
      return true;
    }
    return false;
  }
  
  public static int findNrOfBlockDice(Game pGame, Player pAttacker, int pAttackerStrength, Player pDefender, boolean pUsingMultiBlock) {
    int nrOfDice = 0;
    if ((pAttacker != null) && (pDefender != null)) {
      nrOfDice = 1;
      int blockStrengthAttacker = UtilPlayer.findBlockStrength(pGame, pAttacker, pAttackerStrength, pDefender);
      ActingPlayer actingPlayer = pGame.getActingPlayer();
      if ((pAttacker == actingPlayer.getPlayer()) && ((actingPlayer.getPlayerAction() == PlayerAction.BLITZ) || (actingPlayer.getPlayerAction() == PlayerAction.BLITZ_MOVE)) && actingPlayer.hasMoved() && UtilCards.hasCard(pGame, pDefender, Card.INERTIA_DAMPER)) {
        blockStrengthAttacker -= 1;
      }
      int defenderStrength = pUsingMultiBlock ? UtilCards.getPlayerStrength(pGame, pDefender) + 2 : UtilCards.getPlayerStrength(pGame, pDefender);
      int blockStrengthDefender = UtilPlayer.findBlockStrength(pGame, pDefender, defenderStrength, pAttacker);
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
      if (UtilCards.hasSkill(pGame, pAttacker, Skill.BALL_AND_CHAIN) && (pAttacker.getTeam() == pDefender.getTeam())) {
        nrOfDice = Math.abs(nrOfDice);  // the choice is always for the coach of the b&c team in that case
      }
    }
    return nrOfDice;
  }
}
