package com.balancedbytes.games.ffb.client.layer;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.FieldCoordinateBounds;
import com.balancedbytes.games.ffb.FieldModelChangeEvent;
import com.balancedbytes.games.ffb.Player;
import com.balancedbytes.games.ffb.PlayerMarker;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.IIconProperty;
import com.balancedbytes.games.ffb.client.IconCache;
import com.balancedbytes.games.ffb.client.PlayerIconFactory;
import com.balancedbytes.games.ffb.client.UserInterface;
import com.balancedbytes.games.ffb.model.FieldModel;
import com.balancedbytes.games.ffb.model.Game;



/**
 * 
 * @author Kalimar
 */
public class FieldLayerPlayers extends FieldLayer {
  
	public FieldLayerPlayers(FantasyFootballClient pClient) {
    super(pClient);
  }
  
  public void refresh(FieldCoordinateBounds pBounds) {
    if (pBounds != null) {
      for (FieldCoordinate fieldCoordinate : pBounds.fieldCoordinates()) {
        updateBallAndPlayers(fieldCoordinate, false);
      }
    }
  }
  
  public void updateBallAndPlayers(FieldCoordinate pCoordinate, boolean pPlayerOverBall) {
    if ((pCoordinate != null) && !pCoordinate.isBoxCoordinate()) {
      Game game = getClient().getGame();
      int x = FIELD_IMAGE_OFFSET_CENTER_X + (pCoordinate.getX() * FIELD_SQUARE_SIZE) - (PlayerIconFactory.MAX_ICON_WIDTH / 2);
      int y = FIELD_IMAGE_OFFSET_CENTER_Y + (pCoordinate.getY() * FIELD_SQUARE_SIZE) - (PlayerIconFactory.MAX_ICON_HEIGHT / 2);
      clear(x, y, PlayerIconFactory.MAX_ICON_WIDTH, PlayerIconFactory.MAX_ICON_HEIGHT, true);  // also adds updated area
      Graphics2D g2d = getImage().createGraphics();
      g2d.setClip(x, y, PlayerIconFactory.MAX_ICON_WIDTH, PlayerIconFactory.MAX_ICON_HEIGHT);
      FieldCoordinate[] adjacentCoordinates = game.getFieldModel().findAdjacentCoordinates(pCoordinate, FieldCoordinateBounds.FIELD, 1, true);
      for (int i = 0; i < adjacentCoordinates.length; i++) {
        if (pPlayerOverBall) {
          drawBall(g2d, adjacentCoordinates[i]);
          drawPlayer(g2d, adjacentCoordinates[i]);
          drawBomb(g2d, adjacentCoordinates[i]);  // moving bomb always on top
        } else {
          drawPlayer(g2d, adjacentCoordinates[i]);
          drawBall(g2d, adjacentCoordinates[i]);
          drawBomb(g2d, adjacentCoordinates[i]);  // moving bomb always on top
        }
      }
    }
  }
  
  private void drawPlayer(Graphics2D pG2d, FieldCoordinate pCoordinate) {
    if (pCoordinate != null) {
      Player player = getClient().getGame().getFieldModel().getPlayer(pCoordinate);
      if (player != null) {
        PlayerIconFactory playerIconFactory = getClient().getUserInterface().getPlayerIconFactory();
        BufferedImage icon = playerIconFactory.getIcon(getClient(), player);
        if (icon != null) {
          pG2d.drawImage(icon, findCenteredIconUpperLeftX(icon, pCoordinate), findCenteredIconUpperLeftY(icon, pCoordinate), null);
        }
      }
    }
  }
  
  private void drawBall(Graphics2D pG2d, FieldCoordinate pCoordinate) {
    FieldModel fieldModel = getClient().getGame().getFieldModel();
    UserInterface userInterface = getClient().getUserInterface();
    if (pCoordinate.equals(fieldModel.getBallCoordinate()) && fieldModel.isBallMoving()) {
      IconCache iconCache = userInterface.getIconCache();
      BufferedImage ballIcon = iconCache.getIconByProperty(IIconProperty.GAME_BALL);
      if (!fieldModel.isBallInPlay()) {
        ballIcon = PlayerIconFactory.fadeIcon(ballIcon);
      }
      pG2d.drawImage(ballIcon, findCenteredIconUpperLeftX(ballIcon, pCoordinate), findCenteredIconUpperLeftY(ballIcon, pCoordinate), null);
    }
  }

  private void drawBomb(Graphics2D pG2d, FieldCoordinate pCoordinate) {
    FieldModel fieldModel = getClient().getGame().getFieldModel();
    UserInterface userInterface = getClient().getUserInterface();
    if (pCoordinate.equals(fieldModel.getBombCoordinate()) && fieldModel.isBombMoving()) {
      IconCache iconCache = userInterface.getIconCache();
      BufferedImage bombIcon = iconCache.getIconByProperty(IIconProperty.GAME_BOMB);
      pG2d.drawImage(bombIcon, findCenteredIconUpperLeftX(bombIcon, pCoordinate), findCenteredIconUpperLeftY(bombIcon, pCoordinate), null);
    }
  }

  
  // TODO: add bomb events
  public void fieldModelChanged(FieldModelChangeEvent pChangeEvent) {
    Game game = getClient().getGame();
    switch (pChangeEvent.getType()) {
      case FieldModelChangeEvent.TYPE_BALL_MOVING:
        updateBallAndPlayers(game.getFieldModel().getBallCoordinate(), false);
        break;
      case FieldModelChangeEvent.TYPE_BOMB_MOVING:
        updateBallAndPlayers(game.getFieldModel().getBombCoordinate(), false);
        break;
      case FieldModelChangeEvent.TYPE_BALL_COORDINATE:
      case FieldModelChangeEvent.TYPE_BOMB_COORDINATE:
        if (pChangeEvent.isRemoved() || pChangeEvent.isUpdated()) {
          updateBallAndPlayers((FieldCoordinate) pChangeEvent.getOldValue(), false);
        }
        if (pChangeEvent.isAdded() || pChangeEvent.isUpdated()) {
          updateBallAndPlayers((FieldCoordinate) pChangeEvent.getNewValue(), false);
        }
        break;
      case FieldModelChangeEvent.TYPE_PLAYER_POSITION:
        if (pChangeEvent.isRemoved() || pChangeEvent.isUpdated()) {
          updateBallAndPlayers((FieldCoordinate) pChangeEvent.getOldValue(), true);
        }
        if (pChangeEvent.isAdded() || pChangeEvent.isUpdated()) {
          updateBallAndPlayers((FieldCoordinate) pChangeEvent.getNewValue(), true);
        }
        break;
      case FieldModelChangeEvent.TYPE_PLAYER_MARKER:
        PlayerMarker playerMarker = (pChangeEvent.isAdded() || pChangeEvent.isUpdated()) ? (PlayerMarker) pChangeEvent.getNewValue() : (PlayerMarker) pChangeEvent.getOldValue();
        if (playerMarker != null) {
          Player player = game.getPlayerById(playerMarker.getPlayerId());
          if (player != null) {
            FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(player);
            updateBallAndPlayers(playerCoordinate, true);
          }
        }
        break;
      case FieldModelChangeEvent.TYPE_PLAYER_STATE:
        Player player = (Player) pChangeEvent.getProperty();
        if (player != null) {
          FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(player);
          updateBallAndPlayers(playerCoordinate, true);
        }
        break;
    }
  }
  
  public void init() {
    clear(true);
    FieldModel fieldModel = getClient().getGame().getFieldModel();
    if (fieldModel != null) {
      FieldCoordinate[] playerCoordinates = fieldModel.getPlayerCoordinates();
      for (int i = 0; i < playerCoordinates.length; i++) {
        updateBallAndPlayers(playerCoordinates[i], true);
      }
      updateBallAndPlayers(fieldModel.getBallCoordinate(), false);
      fieldModel.addListener(this);
    }
  }
  
}
