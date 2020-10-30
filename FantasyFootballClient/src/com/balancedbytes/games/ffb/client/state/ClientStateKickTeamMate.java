package com.balancedbytes.games.ffb.client.state;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import com.balancedbytes.games.ffb.ClientStateId;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.client.ActionKey;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.IIconProperty;
import com.balancedbytes.games.ffb.client.IconCache;
import com.balancedbytes.games.ffb.client.UserInterface;
import com.balancedbytes.games.ffb.client.layer.FieldLayerRangeRuler;
import com.balancedbytes.games.ffb.client.net.ClientCommunication;
import com.balancedbytes.games.ffb.client.util.UtilClientCursor;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilPlayer;

/**
 * 
 * @author Christer
 */
public class ClientStateKickTeamMate extends ClientStateMove {
  
  protected ClientStateKickTeamMate(FantasyFootballClient pClient) {
    super(pClient);
  }

  public ClientStateId getId() {
    return ClientStateId.KICK_TEAM_MATE;
  }
  
  public void enterState() {
    super.enterState();
    setSelectable(true);
    markKickablePlayers();
  }
    
  protected void clickOnPlayer(Player pPlayer) {
    Game game = getClient().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    if (pPlayer == actingPlayer.getPlayer()) {
      super.clickOnPlayer(pPlayer);
    } else {
      if ((game.getDefender() == null) && canBeKicked(pPlayer)) {
        
        IconCache iconCache = getClient().getUserInterface().getIconCache();
        List<JMenuItem> menuItemList = new ArrayList<JMenuItem>();

        JMenuItem shortKick = new JMenuItem("Short Kick", new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_BLITZ)));
        shortKick.setMnemonic(IPlayerPopupMenuKeys.KEY_SHORT);
        shortKick.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_SHORT, 0));
        menuItemList.add(shortKick);

        JMenuItem longKick = new JMenuItem("Long Kick", new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_BLITZ)));
        longKick.setMnemonic(IPlayerPopupMenuKeys.KEY_LONG);
        longKick.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_LONG, 0));
        menuItemList.add(longKick);
        
        createPopupMenu(menuItemList.toArray(new JMenuItem[menuItemList.size()]));
        showPopupMenuForPlayer(pPlayer);
        
        //getClient().getCommunication().sendKickTeamMate(actingPlayer.getPlayerId(), pPlayer.getId(), 0);
      }
    }
  }
  
  protected void clickOnField(FieldCoordinate pCoordinate) {
    Game game = getClient().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    UserInterface userInterface = getClient().getUserInterface();
    if (actingPlayer.getPlayerAction() == PlayerAction.KICK_TEAM_MATE_MOVE) {
      super.clickOnField(pCoordinate);
      markKickablePlayers();
      userInterface.getFieldComponent().refresh();
    }    
  }
  
  protected boolean mouseOverField(FieldCoordinate pCoordinate) {
    return super.mouseOverField(pCoordinate);
  }
  
  protected boolean mouseOverPlayer(Player pPlayer) {
    Game game = getClient().getGame();
    UserInterface userInterface = getClient().getUserInterface();
    if ((game.getDefender() == null) && (game.getPassCoordinate() == null)) {
      if (canBeKicked(pPlayer)) {
        UtilClientCursor.setCustomCursor(userInterface, IIconProperty.CURSOR_BLOCK);
      } else {
        UtilClientCursor.setDefaultCursor(userInterface);
      }
    }

    getClient().getClientData().setSelectedPlayer(pPlayer);
    userInterface.refreshSideBars();
    return true;
  }
  
  private boolean canBeKicked(Player pPlayer) {
    Game game = getClient().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    PlayerState catcherState = game.getFieldModel().getPlayerState(pPlayer);
    FieldCoordinate throwerCoordinate = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
    FieldCoordinate catcherCoordinate = game.getFieldModel().getPlayerCoordinate(pPlayer);
    // added a check so you could not throw the opponents players, maybe this should be in the server-check?
    return (UtilCards.hasSkill(game, actingPlayer, Skill.KICK_TEAM_MATE) && UtilCards.hasSkill(game, pPlayer, Skill.RIGHT_STUFF) && catcherState.hasTacklezones() && catcherCoordinate.isAdjacent(throwerCoordinate) && (actingPlayer.getPlayer().getTeam() == pPlayer.getTeam()));
  }
    
  private void markKickablePlayers() {
    Game game = getClient().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    UserInterface userInterface = getClient().getUserInterface();
    Player[] kickablePlayers = UtilPlayer.findKickableTeamMates(game, actingPlayer.getPlayer());
    if ((game.getDefender() == null) && ArrayTool.isProvided(kickablePlayers)) {
      userInterface.getFieldComponent().getLayerRangeRuler().markPlayers(kickablePlayers, FieldLayerRangeRuler.COLOR_THROWABLE_PLAYER);
    } else {
      userInterface.getFieldComponent().getLayerRangeRuler().clearMarkedCoordinates();
    }
    userInterface.getFieldComponent().refresh();
  }
  
  @Override
  public void handleCommand(NetCommand pNetCommand) {
    super.handleCommand(pNetCommand);
  }
  
  @Override
  public void leaveState() {
    // clear marked players
    UserInterface userInterface = getClient().getUserInterface();
    userInterface.getFieldComponent().getLayerRangeRuler().clearMarkedCoordinates();
    userInterface.getFieldComponent().refresh();
  }
  
  protected void menuItemSelected(Player pPlayer, int pMenuKey) {
    super.menuItemSelected(pPlayer, pMenuKey);
    
    if (pPlayer != null) {
      Game game = getClient().getGame();
      ActingPlayer actingPlayer = game.getActingPlayer();
      
      ClientCommunication communication = getClient().getCommunication();
      switch (pMenuKey) {
      case IPlayerPopupMenuKeys.KEY_SHORT:
        communication.sendKickTeamMate(actingPlayer.getPlayerId(), pPlayer.getId(), 1);
        break;
      case IPlayerPopupMenuKeys.KEY_LONG:
        communication.sendKickTeamMate(actingPlayer.getPlayerId(), pPlayer.getId(), 2);
        break;
      }
    }
  }
  
  public boolean actionKeyPressed(ActionKey pActionKey) {
    return super.actionKeyPressed(pActionKey);
  }
}
