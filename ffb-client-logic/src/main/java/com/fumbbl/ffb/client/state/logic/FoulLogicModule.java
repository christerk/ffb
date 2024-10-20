package com.fumbbl.ffb.client.state.logic;

import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.net.ClientCommunication;
import com.fumbbl.ffb.client.state.IPlayerPopupMenuKeys;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.HashSet;
import java.util.Set;

public class FoulLogicModule extends MoveLogicModule {
  public FoulLogicModule(FantasyFootballClient client) {
    super(client);
  }

  @Override
  public InteractionResult playerInteraction(Player<?> player) {
    Game game = client.getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    if (player == actingPlayer.getPlayer()) {
      if (actingPlayer.isSufferingBloodLust()) {
        return new InteractionResult(InteractionResult.Kind.SHOW_BLOODLUST_ACTIONS);
      } else {
        return new InteractionResult(InteractionResult.Kind.SUPER);
      }
    } else {
      if (UtilPlayer.isNextMoveGoingForIt(game) && !actingPlayer.isGoingForIt()) {
        return new InteractionResult(InteractionResult.Kind.SHOW_ACTIONS);
      } else {
        return playerSelected(player);
      }
    }
  }

  @Override
  public InteractionResult playerPeek(Player<?> player) {
    Game game = client.getGame();
    if (UtilPlayer.isFoulable(game, player)) {
      return new InteractionResult(InteractionResult.Kind.PERFORM);
    }
    return new InteractionResult(InteractionResult.Kind.IGNORE);
  }

  public InteractionResult playerSelected(Player<?> defender) {
    Game game = client.getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    boolean doFoul = UtilPlayer.isFoulable(game, defender);
    if (doFoul) {
      if (actingPlayer.getPlayer().hasSkillProperty(NamedProperties.providesFoulingAlternative)) {
        return new InteractionResult(InteractionResult.Kind.SHOW_ACTION_ALTERNATIVES);
      } else {
        foul(defender, false);
        return new InteractionResult(InteractionResult.Kind.HANDLED);
      }
    }
    return new InteractionResult(InteractionResult.Kind.IGNORE);
  }

  public void foul(Player<?> defender, boolean usingChainsaw) {
    Game game = client.getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    client.getCommunication().sendFoul(actingPlayer.getPlayerId(), defender, usingChainsaw);
  }

  @Override
  public Set<ClientAction> availableActions() {
    return new HashSet<ClientAction>() {{
      add(ClientAction.END_MOVE);
      add(ClientAction.JUMP);
      add(ClientAction.MOVE);
      add(ClientAction.TREACHEROUS);
      add(ClientAction.WISDOM);
      add(ClientAction.RAIDING_PARTY);
      add(ClientAction.LOOK_INTO_MY_EYES);
      add(ClientAction.BALEFUL_HEX);
      add(ClientAction.CATCH_OF_THE_DAY);
      add(ClientAction.BOUNDING_LEAP);
      add(ClientAction.FOUL);
      add(ClientAction.CHAINSAW);
      add(ClientAction.BLACK_INK);
      add(ClientAction.THEN_I_STARTED_BLASTIN);
    }};
  }

  @Override
  protected void performAvailableAction(Player<?> player, ClientAction action) {

    if (player != null) {
      Game game = client.getGame();
      ActingPlayer actingPlayer = game.getActingPlayer();
      ClientCommunication communication = client.getCommunication();
      switch (action) {
        case END_MOVE:
          communication.sendActingPlayer(null, null, false);
          break;
        case JUMP:
          if (isJumpAvailableAsNextMove(game, actingPlayer, false)) {
            communication.sendActingPlayer(player, actingPlayer.getPlayerAction(),
              !actingPlayer.isJumping());
          }
          break;
        case MOVE:
          if (actingPlayer.isSufferingBloodLust()) {
            communication.sendActingPlayer(player, PlayerAction.MOVE, actingPlayer.isJumping());
          }
          break;
        case FOUL:
          foul(player, false);
          break;
        case CHAINSAW:
          foul(player, true);
          break;
        case TREACHEROUS:
          if (isTreacherousAvailable(actingPlayer)) {
            Skill skill = player.getSkillWithProperty(NamedProperties.canStabTeamMateForBall);
            communication.sendUseSkill(skill, true, player.getId());
          }
          break;
        case WISDOM:
          if (isWisdomAvailable(actingPlayer)) {
            communication.sendUseWisdom();
          }
          break;
        case RAIDING_PARTY:
          if (isRaidingPartyAvailable(actingPlayer)) {
            Skill raidingSkill = player.getSkillWithProperty(NamedProperties.canMoveOpenTeamMate);
            communication.sendUseSkill(raidingSkill, true, player.getId());
          }
          break;
        case LOOK_INTO_MY_EYES:
          if (isLookIntoMyEyesAvailable(player)) {
            UtilCards.getUnusedSkillWithProperty(player, NamedProperties.canStealBallFromOpponent)
              .ifPresent(lookSkill -> communication.sendUseSkill(lookSkill, true, player.getId()));
          }
          break;
        case BALEFUL_HEX:
          if (isBalefulHexAvailable(actingPlayer)) {
            Skill balefulSkill = player.getSkillWithProperty(NamedProperties.canMakeOpponentMissTurn);
            communication.sendUseSkill(balefulSkill, true, player.getId());
          }
          break;
        case BLACK_INK:
          if (isBlackInkAvailable(actingPlayer)) {
            Skill blackInkSkill = player.getSkillWithProperty(NamedProperties.canGazeAutomatically);
            communication.sendUseSkill(blackInkSkill, true, player.getId());
          }
          break;
        case CATCH_OF_THE_DAY:
          if (isCatchOfTheDayAvailable(actingPlayer)) {
            Skill skill = player.getSkillWithProperty(NamedProperties.canGetBallOnGround);
            communication.sendUseSkill(skill, true, player.getId());
          }
          break;
        case BOUNDING_LEAP:
          isBoundingLeapAvailable(game, actingPlayer).ifPresent(skill ->
            communication.sendUseSkill(skill, true, actingPlayer.getPlayerId()));
          break;
        case THEN_I_STARTED_BLASTIN:
          if (isThenIStartedBlastinAvailable(actingPlayer)) {
            Skill skill = player.getSkillWithProperty(NamedProperties.canBlastRemotePlayer);
            communication.sendUseSkill(skill, true, player.getId());
          }
          break;
        default:
          break;
      }
    }
  }

}
