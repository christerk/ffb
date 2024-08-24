package com.fumbbl.ffb.client.state.logic;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.net.ClientCommunication;
import com.fumbbl.ffb.client.state.ClientState;
import com.fumbbl.ffb.client.state.IPlayerPopupMenuKeys;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.util.UtilCards;

public class BlockLogicExtension {



  public static boolean menuItemSelected(FantasyFootballClient client, Player<?> pPlayer, int pMenuKey) {
    boolean handled = false;
    if (pPlayer != null) {
      Game game = client.getGame();
      ActingPlayer actingPlayer = game.getActingPlayer();
      ClientCommunication communication = client.getCommunication();
      switch (pMenuKey) {
        case IPlayerPopupMenuKeys.KEY_BLOCK:
          handled = true;
          block(client, actingPlayer.getPlayerId(), pPlayer, false, false, false);
          break;
        case IPlayerPopupMenuKeys.KEY_STAB:
          handled = true;
          block(client, actingPlayer.getPlayerId(), pPlayer, true, false, false);
          break;
        case IPlayerPopupMenuKeys.KEY_CHAINSAW:
          handled = true;
          block(client, actingPlayer.getPlayerId(), pPlayer, false, true, false);
          break;
        case IPlayerPopupMenuKeys.KEY_PROJECTILE_VOMIT:
          handled = true;
          block(client, actingPlayer.getPlayerId(), pPlayer, false, false, true);
          break;
        case IPlayerPopupMenuKeys.KEY_TREACHEROUS:
          Skill skill = actingPlayer.getPlayer().getSkillWithProperty(NamedProperties.canStabTeamMateForBall);
          communication.sendUseSkill(skill, true, actingPlayer.getPlayerId());
          break;
        case IPlayerPopupMenuKeys.KEY_WISDOM:
          communication.sendUseWisdom();
          break;
        case IPlayerPopupMenuKeys.KEY_RAIDING_PARTY:
          Skill raidingSkill = actingPlayer.getPlayer().getSkillWithProperty(NamedProperties.canMoveOpenTeamMate);
          communication.sendUseSkill(raidingSkill, true, actingPlayer.getPlayerId());
          break;
        case IPlayerPopupMenuKeys.KEY_LOOK_INTO_MY_EYES:
          UtilCards.getUnusedSkillWithProperty(actingPlayer.getPlayer(), NamedProperties.canStealBallFromOpponent)
            .ifPresent(lookSkill -> communication.sendUseSkill(lookSkill, true, actingPlayer.getPlayerId()));
          break;
        case IPlayerPopupMenuKeys.KEY_BALEFUL_HEX:
          Skill balefulSkill = actingPlayer.getPlayer().getSkillWithProperty(NamedProperties.canMakeOpponentMissTurn);
          communication.sendUseSkill(balefulSkill, true, actingPlayer.getPlayerId());
          break;
        case IPlayerPopupMenuKeys.KEY_BLACK_INK:
          Skill blackInk = actingPlayer.getPlayer().getSkillWithProperty(NamedProperties.canGazeAutomatically);
          communication.sendUseSkill(blackInk, true, actingPlayer.getPlayerId());
          break;
        case IPlayerPopupMenuKeys.KEY_GORED_BY_THE_BULL:
          if (isGoredAvailable(game)) {
            UtilCards.getUnusedSkillWithProperty(actingPlayer.getPlayer(), NamedProperties.canAddBlockDie).ifPresent(goredSkill ->
              communication.sendUseSkill(goredSkill, true, actingPlayer.getPlayerId()));
          }
          break;
        default:
          break;
      }
    }
    return handled;
  }

  public static void block(FantasyFootballClient client, String pActingPlayerId, Player<?> pDefender, boolean pUsingStab,
                           boolean usingChainsaw, boolean usingVomit) {
    // TODO is this needed? Was in place in old structure
    //pClientState.getClient().getUserInterface().getFieldComponent().refresh();
   client.getCommunication().sendBlock(pActingPlayerId, pDefender, pUsingStab, usingChainsaw, usingVomit);
  }

}
