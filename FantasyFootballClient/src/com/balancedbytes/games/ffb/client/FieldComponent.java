package com.balancedbytes.games.ffb.client;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.ToolTipManager;
import javax.swing.event.MouseInputListener;

import com.balancedbytes.games.ffb.BloodSpot;
import com.balancedbytes.games.ffb.DiceDecoration;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.FieldMarker;
import com.balancedbytes.games.ffb.MoveSquare;
import com.balancedbytes.games.ffb.PlayerMarker;
import com.balancedbytes.games.ffb.PushbackSquare;
import com.balancedbytes.games.ffb.RangeRuler;
import com.balancedbytes.games.ffb.TrackNumber;
import com.balancedbytes.games.ffb.Weather;
import com.balancedbytes.games.ffb.client.layer.FieldLayer;
import com.balancedbytes.games.ffb.client.layer.FieldLayerBloodspots;
import com.balancedbytes.games.ffb.client.layer.FieldLayerField;
import com.balancedbytes.games.ffb.client.layer.FieldLayerMarker;
import com.balancedbytes.games.ffb.client.layer.FieldLayerOverPlayers;
import com.balancedbytes.games.ffb.client.layer.FieldLayerPlayers;
import com.balancedbytes.games.ffb.client.layer.FieldLayerRangeGrid;
import com.balancedbytes.games.ffb.client.layer.FieldLayerRangeRuler;
import com.balancedbytes.games.ffb.client.layer.FieldLayerTeamLogo;
import com.balancedbytes.games.ffb.client.layer.FieldLayerUnderPlayers;
import com.balancedbytes.games.ffb.client.state.ClientState;
import com.balancedbytes.games.ffb.model.FieldModel;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.change.IModelChangeObserver;
import com.balancedbytes.games.ffb.model.change.ModelChange;

/**
 * @author j129340
 * 
 */
@SuppressWarnings("serial")
public class FieldComponent extends JPanel implements IModelChangeObserver, MouseInputListener {

  private FantasyFootballClient fClient;

  private FieldLayerField fLayerField;
  private FieldLayerTeamLogo fLayerTeamLogo;
  private FieldLayerBloodspots fLayerBloodspots;
  private FieldLayerRangeGrid fLayerRangeGrid;
  private FieldLayerMarker fLayerMarker;
  private FieldLayerUnderPlayers fLayerUnderPlayers;
  private FieldLayerPlayers fLayerPlayers;
  private FieldLayerOverPlayers fLayerOverPlayers;
  private FieldLayerRangeRuler fLayerRangeRuler;
  private BufferedImage fImage;

  // we need to keep some old model values for a redraw (if those get set to
  // null)
  private FieldCoordinate fBallCoordinate;
  private FieldCoordinate fBombCoordinate;
  private Map<String, FieldCoordinate> fCoordinateByPlayerId;

  public FieldComponent(FantasyFootballClient pClient) {

    fClient = pClient;
    fLayerField = new FieldLayerField(pClient);
    fLayerTeamLogo = new FieldLayerTeamLogo(pClient);
    fLayerBloodspots = new FieldLayerBloodspots(pClient);
    fLayerRangeGrid = new FieldLayerRangeGrid(pClient);
    fLayerMarker = new FieldLayerMarker(pClient);
    fLayerUnderPlayers = new FieldLayerUnderPlayers(pClient);
    fLayerPlayers = new FieldLayerPlayers(pClient);
    fLayerOverPlayers = new FieldLayerOverPlayers(pClient);
    fLayerRangeRuler = new FieldLayerRangeRuler(pClient);

    fCoordinateByPlayerId = new HashMap<String, FieldCoordinate>();

    fImage = new BufferedImage(FieldLayer.FIELD_IMAGE_WIDTH, FieldLayer.FIELD_IMAGE_HEIGHT, BufferedImage.TYPE_INT_ARGB);

    Dimension size = new Dimension(FieldLayer.FIELD_IMAGE_WIDTH, FieldLayer.FIELD_IMAGE_HEIGHT);
    setMinimumSize(size);
    setPreferredSize(size);
    setMaximumSize(size);

    addMouseListener(this);
    addMouseMotionListener(this);

    ToolTipManager.sharedInstance().registerComponent(this);

    refresh();

  }

  public FieldLayerField getLayerField() {
    return fLayerField;
  }

  public FieldLayerTeamLogo getLayerTeamLogo() {
    return fLayerTeamLogo;
  }

  public FieldLayerBloodspots getLayerBloodspots() {
    return fLayerBloodspots;
  }

  public FieldLayerRangeGrid getLayerRangeGrid() {
    return fLayerRangeGrid;
  }

  public FieldLayerMarker getLayerMarker() {
    return fLayerMarker;
  }

  public FieldLayerUnderPlayers getLayerUnderPlayers() {
    return fLayerUnderPlayers;
  }

  public FieldLayerPlayers getLayerPlayers() {
    return fLayerPlayers;
  }

  public FieldLayerOverPlayers getLayerOverPlayers() {
    return fLayerOverPlayers;
  }

  public FieldLayerRangeRuler getLayerRangeRuler() {
    return fLayerRangeRuler;
  }

  public void refresh() {

		Rectangle updatedArea = combineRectangles(
		  new Rectangle[] {
		    getLayerField().fetchUpdatedArea(),
        getLayerTeamLogo().fetchUpdatedArea(),
				getLayerBloodspots().fetchUpdatedArea(),
				getLayerRangeGrid().fetchUpdatedArea(),
        getLayerMarker().fetchUpdatedArea(),
				getLayerUnderPlayers().fetchUpdatedArea(),
				getLayerPlayers().fetchUpdatedArea(),
				getLayerOverPlayers().fetchUpdatedArea(),
				getLayerRangeRuler().fetchUpdatedArea()
		  }
		);

    if (updatedArea != null) {
      refresh(updatedArea);
    }

  }

  public void refresh(Rectangle pUpdatedArea) {

    Graphics2D g2d = fImage.createGraphics();

    if (pUpdatedArea != null) {
      g2d.setClip(pUpdatedArea.x, pUpdatedArea.y, pUpdatedArea.width, pUpdatedArea.height);
    }

    g2d.drawImage(getLayerField().getImage(), 0, 0, null);
    g2d.drawImage(getLayerTeamLogo().getImage(), 0, 0, null);
    g2d.drawImage(getLayerBloodspots().getImage(), 0, 0, null);
    g2d.drawImage(getLayerRangeGrid().getImage(), 0, 0, null);
    g2d.drawImage(getLayerMarker().getImage(), 0, 0, null);
    g2d.drawImage(getLayerUnderPlayers().getImage(), 0, 0, null);
    g2d.drawImage(getLayerPlayers().getImage(), 0, 0, null);
    g2d.drawImage(getLayerOverPlayers().getImage(), 0, 0, null);
    g2d.drawImage(getLayerRangeRuler().getImage(), 0, 0, null);

    // g2d.setColor(Color.RED);
    // g2d.drawRect(pUpdatedArea.x, pUpdatedArea.y, pUpdatedArea.width - 1,
    // pUpdatedArea.height - 1);

    g2d.dispose();

    if (pUpdatedArea != null) {
      repaint(pUpdatedArea);
    } else {
      repaint();
    }

  }

  public void update(ModelChange pModelChange) {
    if ((pModelChange == null) || (pModelChange.getChangeId() == null)) {
      return;
    }
    Game game = getClient().getGame();
    FieldModel fieldModel = game.getFieldModel();
    switch (pModelChange.getChangeId()) {
    case FIELD_MODEL_ADD_BLOOD_SPOT:
      getLayerBloodspots().drawBloodspot((BloodSpot) pModelChange.getValue());
      break;
    case FIELD_MODEL_ADD_DICE_DECORATION:
      getLayerOverPlayers().drawDiceDecoration((DiceDecoration) pModelChange.getValue());
      break;
    case FIELD_MODEL_ADD_FIELD_MARKER:
      getLayerMarker().drawFieldMarker((FieldMarker) pModelChange.getValue());
      break;
    case FIELD_MODEL_ADD_MOVE_SQUARE:
      getLayerOverPlayers().drawMoveSquare((MoveSquare) pModelChange.getValue());
      break;
    case FIELD_MODEL_ADD_PLAYER_MARKER:
      getLayerPlayers().updatePlayerMarker((PlayerMarker) pModelChange.getValue());
      break;
    case FIELD_MODEL_ADD_PUSHBACK_SQUARE:
      getLayerOverPlayers().drawPushbackSquare((PushbackSquare) pModelChange.getValue());
      break;
    case FIELD_MODEL_ADD_TRACK_NUMBER:
      getLayerUnderPlayers().drawTrackNumber((TrackNumber) pModelChange.getValue());
      break;
    case FIELD_MODEL_REMOVE_DICE_DECORATION:
      getLayerOverPlayers().removeDiceDecoration((DiceDecoration) pModelChange.getValue());
      break;
    case FIELD_MODEL_REMOVE_FIELD_MARKER:
      getLayerMarker().removeFieldMarker((FieldMarker) pModelChange.getValue());
      break;
    case FIELD_MODEL_REMOVE_MOVE_SQUARE:
      getLayerOverPlayers().removeMoveSquare((MoveSquare) pModelChange.getValue());
      break;
    case FIELD_MODEL_REMOVE_PLAYER_MARKER:
      getLayerPlayers().updatePlayerMarker((PlayerMarker) pModelChange.getValue());
      break;
    case FIELD_MODEL_REMOVE_PUSHBACK_SQUARE:
      getLayerOverPlayers().removePushbackSquare((PushbackSquare) pModelChange.getValue());
      break;
    case FIELD_MODEL_REMOVE_TRACK_NUMBER:
      getLayerUnderPlayers().removeTrackNumber((TrackNumber) pModelChange.getValue());
      break;
    case FIELD_MODEL_SET_BALL_COORDINATE:
      if (fBallCoordinate != null) {
        getLayerPlayers().updateBallAndPlayers(fBallCoordinate, false);
      }
      FieldCoordinate ballCoordinate = (FieldCoordinate) pModelChange.getValue();
      if (ballCoordinate != null) {
        getLayerPlayers().updateBallAndPlayers(ballCoordinate, false);
      }
      fBallCoordinate = ballCoordinate;
      break;
    case FIELD_MODEL_SET_BALL_MOVING:
      getLayerPlayers().updateBallAndPlayers(fieldModel.getBallCoordinate(), false);
      break;
    case FIELD_MODEL_SET_BOMB_COORDINATE:
      if (fBombCoordinate != null) {
        getLayerPlayers().updateBallAndPlayers(fBombCoordinate, false);
      }
      FieldCoordinate bombCoordinate = (FieldCoordinate) pModelChange.getValue();
      if (bombCoordinate != null) {
        getLayerPlayers().updateBallAndPlayers(bombCoordinate, false);
      }
      fBombCoordinate = bombCoordinate;
      break;
    case FIELD_MODEL_SET_BOMB_MOVING:
      getLayerPlayers().updateBallAndPlayers(fieldModel.getBombCoordinate(), false);
      break;
    case FIELD_MODEL_SET_PLAYER_COORDINATE:
      FieldCoordinate oldPlayerCoordinate = fCoordinateByPlayerId.get(pModelChange.getKey());
      if (oldPlayerCoordinate != null) {
        getLayerPlayers().updateBallAndPlayers(oldPlayerCoordinate, true);
      }
      FieldCoordinate playerCoordinate = (FieldCoordinate) pModelChange.getValue();
      if (playerCoordinate != null) {
        getLayerPlayers().updateBallAndPlayers(playerCoordinate, true);
      }
      fCoordinateByPlayerId.put(pModelChange.getKey(), playerCoordinate);
      break;
    case FIELD_MODEL_SET_PLAYER_STATE:
      Player player = game.getPlayerById(pModelChange.getKey());
      getLayerPlayers().updateBallAndPlayers(fieldModel.getPlayerCoordinate(player), true);
      break;
    case FIELD_MODEL_SET_RANGE_RULER:
      getLayerRangeRuler().drawRangeRuler((RangeRuler) pModelChange.getValue());
      break;
    case FIELD_MODEL_SET_WEATHER:
      getLayerField().drawWeather((Weather) pModelChange.getValue());
      break;
    default:
      break;
    }
  }

  public void init() {
    getClient().getGame().addObserver(this);
    getLayerField().init();
    getLayerTeamLogo().init();
    getLayerBloodspots().init();
    getLayerRangeGrid().init();
    getLayerMarker().init();
    getLayerUnderPlayers().init();
    getLayerPlayers().init();
    getLayerOverPlayers().init();
    getLayerRangeRuler().init();
    refresh();
  }

  private Rectangle combineRectangles(Rectangle[] pRectangles) {
    Rectangle result = null;
    for (int i = 0; i < pRectangles.length; i++) {
      if (pRectangles[i] != null) {
        if (result != null) {
          result.add(pRectangles[i]);
        } else {
          result = pRectangles[i];
        }
      }
    }
    return result;
  }

  protected void paintComponent(Graphics pGraphics) {
    pGraphics.drawImage(fImage, 0, 0, null);
  }

  // MouseMotionListener
  public void mouseMoved(MouseEvent pMouseEvent) {
    getClient().getUserInterface().getMouseEntropySource().reportMousePosition(pMouseEvent);
    ClientState uiState = getClient().getClientState();
    if (uiState != null) {
      uiState.mouseMoved(pMouseEvent);
    }
  }

  // MouseMotionListener
  public void mouseDragged(MouseEvent pMouseEvent) {
    getClient().getUserInterface().getMouseEntropySource().reportMousePosition(pMouseEvent);
    ClientState uiState = getClient().getClientState();
    if (uiState != null) {
      uiState.mouseDragged(pMouseEvent);
    }
  }

  // MouseListener
  public void mouseClicked(MouseEvent pMouseEvent) {
    ClientState uiState = getClient().getClientState();
    if (uiState != null) {
      uiState.mouseClicked(pMouseEvent);
    }
  }

  // MouseListener
  public void mouseEntered(MouseEvent pMouseEvent) {
    ClientState uiState = getClient().getClientState();
    if (uiState != null) {
      uiState.mouseEntered(pMouseEvent);
    }
  }

  // MouseListener
  public void mouseExited(MouseEvent pMouseEvent) {
    ClientState uiState = getClient().getClientState();
    if (uiState != null) {
      uiState.mouseExited(pMouseEvent);
    }
  }

  // MouseListener
  public void mousePressed(MouseEvent pMouseEvent) {
    ClientState uiState = getClient().getClientState();
    if (uiState != null) {
      uiState.mousePressed(pMouseEvent);
    }
  }

  // MouseListener
  public void mouseReleased(MouseEvent pMouseEvent) {
    ClientState uiState = getClient().getClientState();
    if (uiState != null) {
      uiState.mouseReleased(pMouseEvent);
    }
  }

  public FantasyFootballClient getClient() {
    return fClient;
  }

  public BufferedImage getImage() {
    return fImage;
  }

}
