package com.balancedbytes.games.ffb.client.state;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import com.balancedbytes.games.ffb.ClientStateId;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.MoveSquare;
import com.balancedbytes.games.ffb.PathFinderWithPassBlockSupport;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.TurnMode;
import com.balancedbytes.games.ffb.client.ActionKey;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.FieldComponent;
import com.balancedbytes.games.ffb.client.IClientProperty;
import com.balancedbytes.games.ffb.client.IClientPropertyValue;
import com.balancedbytes.games.ffb.client.IIconProperty;
import com.balancedbytes.games.ffb.client.IconCache;
import com.balancedbytes.games.ffb.client.UserInterface;
import com.balancedbytes.games.ffb.client.net.ClientCommunication;
import com.balancedbytes.games.ffb.client.ui.SideBarComponent;
import com.balancedbytes.games.ffb.client.util.UtilClientActionKeys;
import com.balancedbytes.games.ffb.client.util.UtilClientCursor;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilPlayer;

/**
 * 
 * @author Kalimar
 */
public class ClientStateMove extends ClientState {
  
  protected ClientStateMove(FantasyFootballClient pClient) {
    super(pClient);
  }
  
  public ClientStateId getId() {
    return ClientStateId.MOVE;
  }
  
  protected boolean mouseOverField(FieldCoordinate pCoordinate) {
    super.mouseOverField(pCoordinate);
    Game game = getClient().getGame();
    FieldComponent fieldComponent = getClient().getUserInterface().getFieldComponent();
    fieldComponent.getLayerUnderPlayers().clearMovePath();
    ActingPlayer actingPlayer = game.getActingPlayer();
    MoveSquare moveSquare = game.getFieldModel().getMoveSquare(pCoordinate);
    if (moveSquare != null) {
    	setCustomCursor(moveSquare);
    } else {
      UtilClientCursor.setDefaultCursor(getClient().getUserInterface());
      String automoveProperty = getClient().getProperty(IClientProperty.SETTING_AUTOMOVE);
      if ((actingPlayer != null)
      	&& (actingPlayer.getPlayerAction() != null)
      	&& actingPlayer.getPlayerAction().isMoving()
      	&& ArrayTool.isProvided(game.getFieldModel().getMoveSquares())
      	&& !IClientPropertyValue.SETTING_AUTOMOVE_OFF.equals(automoveProperty)
      	&& (game.getTurnMode() != TurnMode.PASS_BLOCK)
      	&& (game.getTurnMode() != TurnMode.KICKOFF_RETURN)
        && (game.getTurnMode() != TurnMode.SWARMING)
      	&& !UtilCards.hasSkill(game, actingPlayer, Skill.BALL_AND_CHAIN)
      ) {
        FieldCoordinate[] shortestPath = PathFinderWithPassBlockSupport.getShortestPath(game, pCoordinate);
        if (ArrayTool.isProvided(shortestPath)) {
          fieldComponent.getLayerUnderPlayers().drawMovePath(shortestPath, actingPlayer.getCurrentMove());
          fieldComponent.refresh();
        }
      }
    }
    return super.mouseOverField(pCoordinate);
  }
  
  private void setCustomCursor(MoveSquare pMoveSquare) {
    Game game = getClient().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    if (pMoveSquare.isGoingForIt() && (pMoveSquare.isDodging() && !actingPlayer.isLeaping())) {
      UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_GFI_DODGE);
    } else if (pMoveSquare.isGoingForIt()) {
      UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_GFI);
    } else if (pMoveSquare.isDodging() && !actingPlayer.isLeaping()) {
      UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_DODGE);
    } else {
      UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_MOVE);
    }
  }
  
  protected boolean mouseOverPlayer(Player pPlayer) {
    Game game = getClient().getGame();
  	FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(pPlayer);
  	MoveSquare moveSquare = game.getFieldModel().getMoveSquare(playerCoordinate);
    if (moveSquare != null) {
    	setCustomCursor(moveSquare);
    } else {
      UtilClientCursor.setDefaultCursor(getClient().getUserInterface());
	    FieldComponent fieldComponent = getClient().getUserInterface().getFieldComponent();
	    if (fieldComponent.getLayerUnderPlayers().clearMovePath()) {
	      fieldComponent.refresh();
	    }
    }
    return super.mouseOverPlayer(pPlayer);
  }
  
  protected void clickOnField(FieldCoordinate pCoordinate) {
    Game game = getClient().getGame();
    FieldComponent fieldComponent = getClient().getUserInterface().getFieldComponent();
    MoveSquare moveSquare = game.getFieldModel().getMoveSquare(pCoordinate);
    FieldCoordinate[] movePath = fieldComponent.getLayerUnderPlayers().getMovePath();
    if (ArrayTool.isProvided(movePath) || (moveSquare != null)) {
      if (ArrayTool.isProvided(movePath)) {
        if (fieldComponent.getLayerUnderPlayers().clearMovePath()) {
          fieldComponent.refresh();
        }
        movePlayer(movePath);
      } else {
        movePlayer(pCoordinate);
      }
    }
  }
  
  protected void clickOnPlayer(Player pPlayer) {
  	Game game = getClient().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    if (pPlayer == actingPlayer.getPlayer()) {
      if (actingPlayer.hasActed()
      	|| UtilCards.hasSkill(game, pPlayer, Skill.LEAP)
      	|| UtilCards.hasSkill(game, pPlayer, Skill.HYPNOTIC_GAZE)
      	|| ((actingPlayer.getPlayerAction() == PlayerAction.PASS_MOVE) && UtilPlayer.hasBall(game, pPlayer))
      	|| ((actingPlayer.getPlayerAction() == PlayerAction.HAND_OVER_MOVE) && UtilPlayer.hasBall(game, pPlayer))
      	|| (actingPlayer.getPlayerAction() == PlayerAction.THROW_TEAM_MATE_MOVE)
      	|| (actingPlayer.getPlayerAction() == PlayerAction.THROW_TEAM_MATE)
      	|| (actingPlayer.getPlayerAction() == PlayerAction.KICK_TEAM_MATE_MOVE)
        || (actingPlayer.getPlayerAction() == PlayerAction.KICK_TEAM_MATE)) {
        createAndShowPopupMenuForActingPlayer();
      } else {
        getClient().getCommunication().sendActingPlayer(null, null, false);
      }
    } else {
    	FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(pPlayer);
      MoveSquare moveSquare = game.getFieldModel().getMoveSquare(playerCoordinate);
      if (moveSquare != null) {
      	movePlayer(playerCoordinate);
      }
    }
  }
  
  protected void menuItemSelected(Player pPlayer, int pMenuKey) {
    if (pPlayer != null) {
      Game game = getClient().getGame();
      ActingPlayer actingPlayer = game.getActingPlayer();
      ClientCommunication communication = getClient().getCommunication();
      switch (pMenuKey) {
        case IPlayerPopupMenuKeys.KEY_END_MOVE:
          if (isEndPlayerActionAvailable()) {
          	communication.sendActingPlayer(null, null, false);
          }
          break;
        case IPlayerPopupMenuKeys.KEY_LEAP:
	        if (UtilCards.hasUnusedSkill(game, actingPlayer, Skill.LEAP) && UtilPlayer.isNextMovePossible(game, false)) {
	          communication.sendActingPlayer(pPlayer, actingPlayer.getPlayerAction(), !actingPlayer.isLeaping());
	        }       
          break;
        case IPlayerPopupMenuKeys.KEY_HAND_OVER:
          if (PlayerAction.HAND_OVER_MOVE == actingPlayer.getPlayerAction() && UtilPlayer.hasBall(game, actingPlayer.getPlayer())) {
          	communication.sendActingPlayer(pPlayer, PlayerAction.HAND_OVER, actingPlayer.isLeaping());
          }
          break;
        case IPlayerPopupMenuKeys.KEY_PASS:
          if (PlayerAction.PASS_MOVE == actingPlayer.getPlayerAction() && UtilPlayer.hasBall(game, actingPlayer.getPlayer())) {
          	communication.sendActingPlayer(pPlayer, PlayerAction.PASS, actingPlayer.isLeaping());
          }
          break;
        case IPlayerPopupMenuKeys.KEY_THROW_TEAM_MATE:
          communication.sendActingPlayer(pPlayer, PlayerAction.THROW_TEAM_MATE, actingPlayer.isLeaping());
          break;
        case IPlayerPopupMenuKeys.KEY_KICK_TEAM_MATE:
          communication.sendActingPlayer(pPlayer, PlayerAction.KICK_TEAM_MATE, actingPlayer.isLeaping());
          break;
        case IPlayerPopupMenuKeys.KEY_MOVE:
          if (PlayerAction.GAZE == actingPlayer.getPlayerAction()) {
            communication.sendActingPlayer(pPlayer, PlayerAction.MOVE, actingPlayer.isLeaping());
          }
          if (PlayerAction.PASS == actingPlayer.getPlayerAction()) {
            communication.sendActingPlayer(pPlayer, PlayerAction.PASS_MOVE, actingPlayer.isLeaping());
          }
          if (PlayerAction.THROW_TEAM_MATE == actingPlayer.getPlayerAction()) {
            communication.sendActingPlayer(pPlayer, PlayerAction.THROW_TEAM_MATE_MOVE, actingPlayer.isLeaping());
          }
          if (PlayerAction.KICK_TEAM_MATE == actingPlayer.getPlayerAction()) {
            communication.sendActingPlayer(pPlayer, PlayerAction.KICK_TEAM_MATE_MOVE, actingPlayer.isLeaping());
          }
          break;
        case IPlayerPopupMenuKeys.KEY_GAZE:
          communication.sendActingPlayer(pPlayer, PlayerAction.GAZE, actingPlayer.isLeaping());
          break;
      }
    }
  }
  
  protected void createAndShowPopupMenuForActingPlayer() {
    Game game = getClient().getGame();
    UserInterface userInterface = getClient().getUserInterface();
    IconCache iconCache = userInterface.getIconCache();
    userInterface.getFieldComponent().getLayerUnderPlayers().clearMovePath();
    List<JMenuItem> menuItemList = new ArrayList<JMenuItem>();
    ActingPlayer actingPlayer = game.getActingPlayer();
    if ((PlayerAction.PASS_MOVE == actingPlayer.getPlayerAction()) && UtilPlayer.hasBall(game, actingPlayer.getPlayer())) {
      JMenuItem passAction = new JMenuItem("Pass Ball (any square)", new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_PASS)));
      passAction.setMnemonic(IPlayerPopupMenuKeys.KEY_PASS);
      passAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_PASS, 0));
      menuItemList.add(passAction);
    }
    if (((PlayerAction.PASS_MOVE == actingPlayer.getPlayerAction()) && UtilPlayer.hasBall(game, actingPlayer.getPlayer()))
    	|| ((PlayerAction.THROW_TEAM_MATE_MOVE == actingPlayer.getPlayerAction()) && UtilPlayer.canThrowTeamMate(game, actingPlayer.getPlayer(), true))) {
      JMenuItem toggleRangeGridAction = new JMenuItem("Range Grid on/off", new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_TOGGLE_RANGE_GRID)));
      toggleRangeGridAction.setMnemonic(IPlayerPopupMenuKeys.KEY_RANGE_GRID);
      toggleRangeGridAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_RANGE_GRID, 0));
      menuItemList.add(toggleRangeGridAction);
    }
    if (PlayerAction.GAZE == actingPlayer.getPlayerAction()) {
      JMenuItem moveAction = new JMenuItem("Move", new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_MOVE)));
      moveAction.setMnemonic(IPlayerPopupMenuKeys.KEY_MOVE);
      moveAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_MOVE, 0));
      menuItemList.add(moveAction);
    }
    if (UtilCards.hasUnusedSkill(game, actingPlayer, Skill.LEAP) && UtilPlayer.isNextMovePossible(game, true)) {
      if (actingPlayer.isLeaping()) {
        JMenuItem leapAction = new JMenuItem("Don't Leap", new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_MOVE)));
        leapAction.setMnemonic(IPlayerPopupMenuKeys.KEY_LEAP);
        leapAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_LEAP, 0));
        menuItemList.add(leapAction);
      } else {
        JMenuItem leapAction = new JMenuItem("Leap", new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_LEAP)));
        leapAction.setMnemonic(IPlayerPopupMenuKeys.KEY_LEAP);
        leapAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_LEAP, 0));
        menuItemList.add(leapAction);
      }
    }
    if (isHypnoticGazeActionAvailable()) {
      JMenuItem hypnoticGazeAction = new JMenuItem("Hypnotic Gaze", new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_GAZE)));
      hypnoticGazeAction.setMnemonic(IPlayerPopupMenuKeys.KEY_GAZE);
      hypnoticGazeAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_GAZE, 0));
      menuItemList.add(hypnoticGazeAction);
    }
    if (isEndPlayerActionAvailable()) {
	    String endMoveActionLabel = actingPlayer.hasActed() ? "End Move" : "Deselect Player";
	    JMenuItem endMoveAction = new JMenuItem(endMoveActionLabel, new ImageIcon(iconCache.getIconByProperty(IIconProperty.ACTION_END_MOVE)));
	    endMoveAction.setMnemonic(IPlayerPopupMenuKeys.KEY_END_MOVE);
	    endMoveAction.setAccelerator(KeyStroke.getKeyStroke(IPlayerPopupMenuKeys.KEY_END_MOVE, 0));
	    menuItemList.add(endMoveAction);
    }
    createPopupMenu(menuItemList.toArray(new JMenuItem[menuItemList.size()]));
    showPopupMenuForPlayer(actingPlayer.getPlayer());
  }
  
  public boolean actionKeyPressed(ActionKey pActionKey) {
    boolean actionHandled = true;
    Game game = getClient().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    FieldCoordinate playerPosition = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
    FieldCoordinate moveCoordinate = UtilClientActionKeys.findMoveCoordinate(getClient(), playerPosition, pActionKey);
    if (moveCoordinate != null) {
      MoveSquare[] moveSquares = game.getFieldModel().getMoveSquares();
      for (MoveSquare moveSquare : moveSquares) {
        if (moveSquare.getCoordinate().equals(moveCoordinate)) {
          movePlayer(moveCoordinate);
          break;
        }
      }
    } else {
      switch (pActionKey) {
        case PLAYER_SELECT:
          createAndShowPopupMenuForActingPlayer();
          break;
        case PLAYER_ACTION_HAND_OVER:
          menuItemSelected(actingPlayer.getPlayer(), IPlayerPopupMenuKeys.KEY_HAND_OVER);
          break;
        case PLAYER_ACTION_PASS:
          menuItemSelected(actingPlayer.getPlayer(), IPlayerPopupMenuKeys.KEY_PASS);
          break;
        case PLAYER_ACTION_LEAP:
          menuItemSelected(actingPlayer.getPlayer(), IPlayerPopupMenuKeys.KEY_LEAP);
          break;
        case PLAYER_ACTION_END_MOVE:
          menuItemSelected(actingPlayer.getPlayer(), IPlayerPopupMenuKeys.KEY_END_MOVE);
          break;
        case PLAYER_ACTION_GAZE:
          menuItemSelected(actingPlayer.getPlayer(), IPlayerPopupMenuKeys.KEY_GAZE);
          break;
        default:
          actionHandled = false;
          break;
      }
    }
    return actionHandled;
  }
  
  @Override
  public void endTurn() {
    Game game = getClient().getGame();
    ActingPlayer actingPlayer = game.getActingPlayer();
    menuItemSelected(actingPlayer.getPlayer(), IPlayerPopupMenuKeys.KEY_END_MOVE);
    getClient().getCommunication().sendEndTurn();
    getClient().getClientData().setEndTurnButtonHidden(true);
    SideBarComponent sideBarHome = getClient().getUserInterface().getSideBarHome();
    sideBarHome.refresh();
  }

  protected void movePlayer(FieldCoordinate pCoordinate) {
    if (pCoordinate == null) {
    	return;
    }
    movePlayer(new FieldCoordinate[] { pCoordinate });
  }

  protected void movePlayer(FieldCoordinate[] pCoordinates) {
  	if (!ArrayTool.isProvided(pCoordinates)) {
  		return;
  	}
  	Game game = getClient().getGame();
  	ActingPlayer actingPlayer = game.getActingPlayer();
    FieldCoordinate coordinateFrom = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
    if (coordinateFrom == null) {
    	return;
    }
    getClient().getGame().getFieldModel().clearMoveSquares();
    getClient().getUserInterface().getFieldComponent().refresh();
    getClient().getCommunication().sendPlayerMove(actingPlayer.getPlayerId(), coordinateFrom, pCoordinates);
  }
  
  private boolean isEndPlayerActionAvailable() {
  	Game game = getClient().getGame();
  	ActingPlayer actingPlayer = game.getActingPlayer();
  	return (!actingPlayer.hasActed() || !UtilCards.hasSkill(game, actingPlayer, Skill.BALL_AND_CHAIN) || (actingPlayer.getCurrentMove() >= UtilCards.getPlayerMovement(game, actingPlayer.getPlayer())));
  }

  private boolean isHypnoticGazeActionAvailable() {
  	Game game = getClient().getGame();
  	ActingPlayer actingPlayer = game.getActingPlayer();
  	return ((actingPlayer.getPlayerAction() == PlayerAction.MOVE) && UtilPlayer.canGaze(game, actingPlayer.getPlayer()));
  }

}
 