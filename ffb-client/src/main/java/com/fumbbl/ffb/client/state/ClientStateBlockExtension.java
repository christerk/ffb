package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.IconCache;
import com.fumbbl.ffb.client.ui.swing.JMenuItem;
import com.fumbbl.ffb.client.util.UtilClientActionKeys;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilPlayer;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

  public void createAndShowBlockOptionsPopupMenu(ClientStateAwt<?> pClientState, Player<?> attacker, Player<?> defender, boolean multiBlock, List<JMenuItem> menuItemList) {
    IconCache iconCache = pClientState.getClient().getUserInterface().getIconCache();
    DimensionProvider dimensionProvider = pClientState.dimensionProvider();
    if (attacker.hasSkillProperty(NamedProperties.canPerformArmourRollInsteadOfBlock)) {
      JMenuItem stabAction = new JMenuItem(dimensionProvider, "Stab Opponent",
        new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_STAB)));
      stabAction.setMnemonic(IPlayerPopupMenuKeys.KEY_STAB);
      stabAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_STAB, 0));
      menuItemList.add(stabAction);
    }
    if (attacker.hasSkillProperty(NamedProperties.providesChainsawBlockAlternative) && !multiBlock) {
      JMenuItem chainsawAction = new JMenuItem(dimensionProvider, "Chainsaw",
        new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_CHAINSAW)));
      chainsawAction.setMnemonic(IPlayerPopupMenuKeys.KEY_CHAINSAW);
      chainsawAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_CHAINSAW, 0));
      menuItemList.add(chainsawAction);
    }
    Optional<Skill> vomitSkill = UtilCards.getUnusedSkillWithProperty(attacker, NamedProperties.canPerformArmourRollInsteadOfBlockThatMightFail);
    if (vomitSkill.isPresent()) {
      JMenuItem projectileVomit = new JMenuItem(dimensionProvider, vomitSkill.get().getName(),
        new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_VOMIT)));
      projectileVomit.setMnemonic(IPlayerPopupMenuKeys.KEY_PROJECTILE_VOMIT);
      projectileVomit.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_PROJECTILE_VOMIT, 0));
      menuItemList.add(projectileVomit);
    }

    JMenuItem blockAction = new JMenuItem(dimensionProvider, "Block Opponent",
      new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_BLOCK)));
    blockAction.setMnemonic(IPlayerPopupMenuKeys.KEY_BLOCK);
    blockAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_BLOCK, 0));
    menuItemList.add(blockAction);
    pClientState.createPopupMenu(menuItemList.toArray(new JMenuItem[0]));
    pClientState.showPopupMenuForPlayer(defender);
  }
}
