package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.util.UtilClientActionKeys;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilPlayer;

public class ClientStateExtension {
  
  public boolean actionKeyPressed(ClientStateAwt<?> clientState, ActionKey pActionKey, boolean pDoBlitz) {
    Game game = clientState.getClient().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    Player<?> player = actingPlayer.getPlayer();

    switch (pActionKey) {
      case PLAYER_ACTION_BLOCK:
        clientState.menuItemSelected(player, IPlayerPopupMenuKeys.KEY_BLOCK);
        break;
      case PLAYER_ACTION_STAB:
        clientState.menuItemSelected(player, IPlayerPopupMenuKeys.KEY_STAB);
        break;
      case PLAYER_ACTION_CHAINSAW:
        clientState.menuItemSelected(player, IPlayerPopupMenuKeys.KEY_CHAINSAW);
        break;
      case PLAYER_ACTION_PROJECTILE_VOMIT:
        clientState.menuItemSelected(player, IPlayerPopupMenuKeys.KEY_PROJECTILE_VOMIT);
        break;
      case PLAYER_ACTION_TREACHEROUS:
        clientState.menuItemSelected(player, IPlayerPopupMenuKeys.KEY_TREACHEROUS);
        break;
      case PLAYER_ACTION_WISDOM:
        clientState.menuItemSelected(player, IPlayerPopupMenuKeys.KEY_WISDOM);
        break;
      case PLAYER_ACTION_RAIDING_PARTY:
        clientState.menuItemSelected(player, IPlayerPopupMenuKeys.KEY_RAIDING_PARTY);
        break;
      case PLAYER_ACTION_BALEFUL_HEX:
        clientState.menuItemSelected(player, IPlayerPopupMenuKeys.KEY_BALEFUL_HEX);
        break;
      case PLAYER_ACTION_GORED:
        clientState.menuItemSelected(player, IPlayerPopupMenuKeys.KEY_GORED_BY_THE_BULL);
        return true;
      default:
        FieldCoordinate playerPosition = game.getFieldModel().getPlayerCoordinate(player);
        FieldCoordinate moveCoordinate = UtilClientActionKeys.findMoveCoordinate(playerPosition,
          pActionKey);
        Player<?> defender = game.getFieldModel().getPlayer(moveCoordinate);
        return showPopupOrBlockPlayer(clientState, defender, pDoBlitz);
    }
    return false;
  }

  public static boolean showPopupOrBlockPlayer(ClientStateAwt<?> pClientState, Player<?> pDefender, boolean pDoBlitz) {
    if (pDefender == null) {
      return false;
    }
    boolean handled = false;
    Game game = pClientState.getClient().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();

    PlayerState playerState = game.getFieldModel().getPlayerState(actingPlayer.getPlayer());
    // rooted players can not move but still spend movement for the blitz action
    if (UtilPlayer.isBlockable(game, pDefender) && (!pDoBlitz || playerState.isRooted() || UtilPlayer.isNextMovePossible(game, false))) {
      handled = true;
      FieldCoordinate defenderCoordinate = game.getFieldModel().getPlayerCoordinate(pDefender);
      if (UtilCards.hasUnusedSkillWithProperty(actingPlayer.getPlayer(), NamedProperties.providesBlockAlternative)
        || (isGoredAvailable(game) && pDoBlitz)) {
        createAndShowBlockOptionsPopupMenu(pClientState, actingPlayer.getPlayer(), pDefender, false);
      } else if (game.getFieldModel().getDiceDecoration(defenderCoordinate) != null) {
        block(pClientState, actingPlayer.getPlayerId(), pDefender, false, false, false);
      } else {
        handled = false;
      }
    }
    return handled;
  }
}
