package com.fumbbl.ffb.client.state.logic;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.net.ClientCommunication;
import com.fumbbl.ffb.client.state.ClientState;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.HashSet;
import java.util.Set;

public class BlockLogicExtension {



  public Set<ClientAction> genericBlockActions() {
    return new HashSet<ClientAction>() {{
      add(ClientAction.BLOCK);
      add(ClientAction.STAB);
      add(ClientAction.CHAINSAW);
      add(ClientAction.PROJECTILE_VOMIT);
      add(ClientAction.TREACHEROUS);
      add(ClientAction.WISDOM);
      add(ClientAction.RAIDING_PARTY);
      add(ClientAction.LOOK_INTO_MY_EYES);
      add(ClientAction.BALEFUL_HEX);
      add(ClientAction.BLACK_INK);
    }};
  }

  protected void performBlockAction(FantasyFootballClient client, Player<?> player, ClientAction action) {
    ClientCommunication communication = client.getCommunication();
    ActingPlayer actingPlayer = client.getGame().getActingPlayer();
    switch (action) {
      case BLOCK:
        block(client, actingPlayer.getPlayerId(), player, false, false, false);
        break;
      case STAB:
        block(client, actingPlayer.getPlayerId(), player, true, false, false);
        break;
      case CHAINSAW:
        block(client, actingPlayer.getPlayerId(), player, false, true, false);
        break;
      case PROJECTILE_VOMIT:
        block(client, actingPlayer.getPlayerId(), player, false, false, true);
        break;
      case TREACHEROUS:
        Skill skill = actingPlayer.getPlayer().getSkillWithProperty(NamedProperties.canStabTeamMateForBall);
        communication.sendUseSkill(skill, true, actingPlayer.getPlayerId());
        break;
      case WISDOM:
        communication.sendUseWisdom();
        break;
      case RAIDING_PARTY:
        Skill raidingSkill = actingPlayer.getPlayer().getSkillWithProperty(NamedProperties.canMoveOpenTeamMate);
        communication.sendUseSkill(raidingSkill, true, actingPlayer.getPlayerId());
        break;
      case LOOK_INTO_MY_EYES:
        UtilCards.getUnusedSkillWithProperty(actingPlayer.getPlayer(), NamedProperties.canStealBallFromOpponent)
          .ifPresent(lookSkill -> communication.sendUseSkill(lookSkill, true, actingPlayer.getPlayerId()));
         break;
      case BALEFUL_HEX:
        Skill balefulSkill = actingPlayer.getPlayer().getSkillWithProperty(NamedProperties.canMakeOpponentMissTurn);
        communication.sendUseSkill(balefulSkill, true, actingPlayer.getPlayerId());
        break;
      case BLACK_INK:
        Skill blackInk = actingPlayer.getPlayer().getSkillWithProperty(NamedProperties.canGazeAutomatically);
        communication.sendUseSkill(blackInk, true, actingPlayer.getPlayerId());
        break;
      default:
        break;

    }
  }

  public void block(FantasyFootballClient client, String pActingPlayerId, Player<?> pDefender, boolean pUsingStab,
                           boolean usingChainsaw, boolean usingVomit) {
    // TODO is this needed? Was in place in old structure
    //pClientState.getClient().getUserInterface().getFieldComponent().refresh();
    client.getCommunication().sendBlock(pActingPlayerId, pDefender, pUsingStab, usingChainsaw, usingVomit);
  }


  public InteractionResult playerInteraction(ClientState<?,?> pClientState, Player<?> pDefender, boolean pDoBlitz) {
    if (pDefender == null) {
      return new InteractionResult(InteractionResult.Kind.IGNORE);
    }

    Game game = pClientState.getClient().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();

    PlayerState playerState = game.getFieldModel().getPlayerState(actingPlayer.getPlayer());
    // rooted players can not move but still spend movement for the blitz action
    if (isBlockable(game, pDefender) && (!pDoBlitz || playerState.isRooted() || UtilPlayer.isNextMovePossible(game, false))) {
      FieldCoordinate defenderCoordinate = game.getFieldModel().getPlayerCoordinate(pDefender);
       if (game.getFieldModel().getDiceDecoration(defenderCoordinate) != null) {
        block(pClientState.getClient(), actingPlayer.getPlayerId(), pDefender, false, false, false);
        return new InteractionResult(InteractionResult.Kind.HANDLED);
      }
    }
    return new InteractionResult(InteractionResult.Kind.IGNORE);
  }


  public boolean isBlockable(Game game, Player<?> pPlayer) {
    ActingPlayer actingPlayer = game.getActingPlayer();
    FieldCoordinate defenderCoordinate = game.getFieldModel().getPlayerCoordinate(pPlayer);
    FieldCoordinate attackerCoordinate = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
    return isValidBlitzTarget(game, pPlayer) && defenderCoordinate.isAdjacent(attackerCoordinate)
      && (game.getFieldModel().getDiceDecoration(defenderCoordinate) != null);
  }

  public boolean isValidBlitzTarget(Game game, Player<?> pPlayer) {
    if (pPlayer != null) {
      FieldModel fieldModel = game.getFieldModel();
      PlayerState defenderState = fieldModel.getPlayerState(pPlayer);
      FieldCoordinate defenderCoordinate = fieldModel.getPlayerCoordinate(pPlayer);
      return (defenderState.canBeBlocked() && game.getTeamAway().hasPlayer(pPlayer) && (defenderCoordinate != null)
        && (fieldModel.getTargetSelectionState() == null || pPlayer.getId().equals(fieldModel.getTargetSelectionState().getSelectedPlayerId())));
    }
    return false;
  }

}
