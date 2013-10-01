package com.balancedbytes.games.ffb.client.state;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import com.balancedbytes.games.ffb.ClientStateId;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.Player;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.client.ActionKey;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.IIconProperty;
import com.balancedbytes.games.ffb.client.IconCache;
import com.balancedbytes.games.ffb.client.UserInterface;
import com.balancedbytes.games.ffb.client.util.UtilActionKeys;
import com.balancedbytes.games.ffb.client.util.UtilCursor;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilPlayer;

/**
 * 
 * @author Kalimar
 */
public class ClientStateFoul extends ClientStateMove {

  protected ClientStateFoul(FantasyFootballClient pClient) {
    super(pClient);
  }
  
  public ClientStateId getId() {
    return ClientStateId.FOUL;
  }
  
  public void clickOnPlayer(Player pPlayer) {
    Game game = getClient().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    if (pPlayer == actingPlayer.getPlayer()) {
      if (actingPlayer.isSufferingBloodLust()) {
        createAndShowPopupMenuForBloodLustPlayer();
      } else {
        super.clickOnPlayer(pPlayer);
      }
    } else {
      if (UtilPlayer.isNextMoveGoingForIt(game) && !actingPlayer.isGoingForIt()) {
        createAndShowPopupMenuForActingPlayer();
      } else {
        foul(pPlayer);
      }
    }
  }
  
  public boolean actionKeyPressed(ActionKey pActionKey) {    
    boolean actionHandled = false;
    Game game = getClient().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    if (actingPlayer.isSufferingBloodLust()) {
      switch (pActionKey) {
        case PLAYER_SELECT:
          createAndShowPopupMenuForBloodLustPlayer();
          break;
        case PLAYER_ACTION_MOVE:
          menuItemSelected(actingPlayer.getPlayer(), IPlayerPopupMenuKeys.KEY_MOVE);
          break;
        case PLAYER_ACTION_END_MOVE:
          menuItemSelected(actingPlayer.getPlayer(), IPlayerPopupMenuKeys.KEY_END_MOVE);
          break;
        default:
          actionHandled = false;
          break;
      }
    } else {
      FieldCoordinate playerPosition = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
      FieldCoordinate defenderPosition = UtilActionKeys.findMoveCoordinate(getClient(), playerPosition, pActionKey);
      Player defender = game.getFieldModel().getPlayer(defenderPosition);
      if (defender != null) {
        actionHandled = foul(defender);
      } else {
        actionHandled = super.actionKeyPressed(pActionKey);
      }
    }
    return actionHandled;
  }
  
  protected boolean mouseOverPlayer(Player pPlayer) {
    super.mouseOverPlayer(pPlayer);
    Game game = getClient().getGame();
    if (UtilPlayer.isFoulable(game, pPlayer)) {
      UtilCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_FOUL);
    }
    return true;
  }
  
  private boolean foul(Player pDefender) {
    Game game = getClient().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    boolean doFoul = UtilPlayer.isFoulable(game, pDefender);
    if (doFoul) {
      getClient().getCommunication().sendFoul(actingPlayer.getPlayerId(), pDefender);
    }
    return doFoul;
  }
  
  protected void menuItemSelected(Player pPlayer, int pMenuKey) {
    if (pPlayer != null) {
      Game game = getClient().getGame();
      ActingPlayer actingPlayer = game.getActingPlayer();
      switch (pMenuKey) {
        case IPlayerPopupMenuKeys.KEY_END_MOVE:
          getClient().getCommunication().sendActingPlayer(null, null, false);
          break;
        case IPlayerPopupMenuKeys.KEY_LEAP:
        	   if(UtilCards.hasUnusedSkill(game, actingPlayer, Skill.LEAP) && UtilPlayer.isNextMovePossible(game, false)){
        	getClient().getCommunication().sendActingPlayer(pPlayer, actingPlayer.getPlayerAction(), !actingPlayer.isLeaping());
        	   }
            break;
        case IPlayerPopupMenuKeys.KEY_MOVE:
          if (actingPlayer.isSufferingBloodLust()) {
            getClient().getCommunication().sendActingPlayer(pPlayer, PlayerAction.MOVE, actingPlayer.isLeaping());
          }
          break;
      }
    }
  }
  
  protected void createAndShowPopupMenuForBloodLustPlayer() {
    Game game = getClient().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    if (actingPlayer.isSufferingBloodLust()) {
      UserInterface userInterface = getClient().getUserInterface();
      IconCache iconCache = userInterface.getIconCache();
      userInterface.getFieldComponent().getLayerUnderPlayers().clearMovePath();
      List<JMenuItem> menuItemList = new ArrayList<JMenuItem>();
      JMenuItem moveAction = new JMenuItem("Move", new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_MOVE)));
      moveAction.setMnemonic(IPlayerPopupMenuKeys.KEY_MOVE);
      moveAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_MOVE, 0));
      menuItemList.add(moveAction);
      JMenuItem endMoveAction = new JMenuItem("End Move", new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_END_MOVE)));
      endMoveAction.setMnemonic(IPlayerPopupMenuKeys.KEY_END_MOVE);
      endMoveAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_END_MOVE, 0));
      menuItemList.add(endMoveAction);
      createPopupMenu(menuItemList.toArray(new JMenuItem[menuItemList.size()]));
      showPopupMenuForPlayer(actingPlayer.getPlayer());
    }
  }

}
