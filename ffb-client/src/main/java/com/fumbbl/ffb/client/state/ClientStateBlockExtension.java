package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.Influences;
import com.fumbbl.ffb.client.util.UtilClientActionKeys;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ClientStateBlockExtension {
  
  public boolean actionKeyPressed(ClientStateAwt<?> clientState, ActionKey pActionKey) {
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
      case PLAYER_ACTION_BLACK_INK:
        clientState.menuItemSelected(player, IPlayerPopupMenuKeys.KEY_BLACK_INK);
        return true;
      case PLAYER_ACITON_THEN_I_STARTED_BLASTIN:
        clientState.menuItemSelected(player, IPlayerPopupMenuKeys.KEY_THEN_I_STARTED_BLASTIN);
        return true;
      case PLAYER_ACTION_BREATHE_FIRE:
        clientState.menuItemSelected(player, IPlayerPopupMenuKeys.KEY_BREATHE_FIRE);
        return true;
      default:
        FieldCoordinate playerPosition = game.getFieldModel().getPlayerCoordinate(player);
        FieldCoordinate moveCoordinate = UtilClientActionKeys.findMoveCoordinate(playerPosition,
          pActionKey);
        Player<?> defender = game.getFieldModel().getPlayer(moveCoordinate);
        clientState.clickOnPlayer(defender);
        return true;
    }
    return false;
  }


  protected Map<Influences, Map<ClientAction, MenuItemConfig>> influencedItemConfigs() {
    Map<Influences, Map<ClientAction, MenuItemConfig>> influences = new HashMap<>();
    Map<ClientAction, MenuItemConfig> putrid = new HashMap<>();
    influences.put(Influences.VOMIT_DUE_TO_PUTRID_REGURGITATION, putrid);
    putrid.put(ClientAction.PROJECTILE_VOMIT, new MenuItemConfig("Putrid Regurgitation", IIconProperty.ACTION_VOMIT, IPlayerPopupMenuKeys.KEY_PROJECTILE_VOMIT));
    return influences;
  }

  protected LinkedHashMap<ClientAction, MenuItemConfig> itemConfigs() {
    LinkedHashMap<ClientAction, MenuItemConfig> itemConfigs = new LinkedHashMap<>();

    itemConfigs.put(ClientAction.STAB, new MenuItemConfig("Stab Opponent", IIconProperty.ACTION_STAB, IPlayerPopupMenuKeys.KEY_STAB));
    itemConfigs.put(ClientAction.CHAINSAW, new MenuItemConfig("Chainsaw", IIconProperty.ACTION_CHAINSAW, IPlayerPopupMenuKeys.KEY_CHAINSAW));
    itemConfigs.put(ClientAction.PROJECTILE_VOMIT, new MenuItemConfig("Projectile Vomit", IIconProperty.ACTION_VOMIT, IPlayerPopupMenuKeys.KEY_PROJECTILE_VOMIT));
    itemConfigs.put(ClientAction.BREATHE_FIRE, new MenuItemConfig("Breathe Fire", IIconProperty.ACTION_BREATHE_FIRE, IPlayerPopupMenuKeys.KEY_BREATHE_FIRE));
    itemConfigs.put(ClientAction.BLOCK, new MenuItemConfig("Block Opponent", IIconProperty.ACTION_BLOCK, IPlayerPopupMenuKeys.KEY_BLOCK));

    return itemConfigs;

  }
}
