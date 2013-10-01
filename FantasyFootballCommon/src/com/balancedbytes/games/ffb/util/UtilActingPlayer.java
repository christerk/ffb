package com.balancedbytes.games.ffb.util;

import com.balancedbytes.games.ffb.Player;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.FieldModel;
import com.balancedbytes.games.ffb.model.Game;

/**
 * 
 * @author Kalimar
 */
public class UtilActingPlayer {
  
  public static boolean changeActingPlayer(Game pGame, String pActingPlayerId, PlayerAction pPlayerAction, boolean pLeaping) {
    
    boolean changed = false;
    
    FieldModel fieldModel = pGame.getFieldModel();
    ActingPlayer actingPlayer = pGame.getActingPlayer();

    Player oldPlayer = actingPlayer.getPlayer();
    Player newPlayer = pGame.getPlayerById(pActingPlayerId);
    
    if ((oldPlayer != null) && (oldPlayer != newPlayer)) {
      changed = true;
      PlayerState currentState = pGame.getFieldModel().getPlayerState(oldPlayer);
      if (currentState.getBase() == PlayerState.MOVING) {
        if (actingPlayer.hasActed() && (((PlayerAction.THROW_BOMB != actingPlayer.getPlayerAction()) && (PlayerAction.HAIL_MARY_BOMB != actingPlayer.getPlayerAction())) || actingPlayer.isSkillUsed(Skill.BOMBARDIER))) {
          pGame.getFieldModel().setPlayerState(oldPlayer, currentState.changeBase(PlayerState.STANDING).changeActive(false));
      	} else if (actingPlayer.isStandingUp()) {
          pGame.getFieldModel().setPlayerState(oldPlayer, currentState.changeBase(PlayerState.PRONE));
        } else {
          pGame.getFieldModel().setPlayerState(oldPlayer, currentState.changeBase(PlayerState.STANDING));
        }
      }
      pGame.getActingPlayer().setPlayer(null);
    }
        
    if (newPlayer != null) {
      if (newPlayer != oldPlayer) {
        changed = true;
        actingPlayer.setPlayer(newPlayer);
        PlayerState oldState = pGame.getFieldModel().getPlayerState(newPlayer);
        actingPlayer.setStandingUp(oldState.getBase() == PlayerState.PRONE);
        // show acting player as moving
        fieldModel.setPlayerState(newPlayer, oldState.changeBase(PlayerState.MOVING));
      }
      actingPlayer.setPlayerAction(pPlayerAction);
      actingPlayer.setLeaping(pLeaping);
    }
    
    if (changed) {
      fieldModel.clearTrackNumbers();
      fieldModel.clearDiceDecorations();
      fieldModel.clearPushbackSquares();
      fieldModel.clearMoveSquares();
      if (pGame.getActingPlayer() != null) {
        PlayerState playerState = pGame.getFieldModel().getPlayerState(pGame.getActingPlayer().getPlayer());
        if (playerState.hasUsedPro()) {
          pGame.getActingPlayer().markSkillUsed(Skill.PRO);
        }
      }
      Player[] players = pGame.getPlayers();
      for (int i = 0; i < players.length; i++) {
        PlayerState playerState = fieldModel.getPlayerState(players[i]);
        if ((playerState.getBase() == PlayerState.BLOCKED) || ((playerState.getBase() == PlayerState.MOVING) && (players[i] != actingPlayer.getPlayer()) && (players[i] != pGame.getThrower()))) {
          fieldModel.setPlayerState(players[i], playerState.changeBase(PlayerState.STANDING));
        }
      }
    }
    
    return changed;
    
  }
    
}
