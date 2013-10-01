package com.balancedbytes.games.ffb.client.layer;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.FieldMarker;
import com.balancedbytes.games.ffb.FieldModelChangeEvent;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.model.FieldModel;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.util.StringTool;


/**
 * 
 * @author Kalimar
 */
public class FieldLayerMarker extends FieldLayer {
  
  public static final Color COLOR_MARKER = new Color(1.0f, 1.0f, 1.0f, 1.0f);
  
  private Map<FieldCoordinate, Rectangle> fFieldMarkerBounds;
  
  public FieldLayerMarker(FantasyFootballClient pClient) {
    super(pClient);
    fFieldMarkerBounds = new HashMap<FieldCoordinate, Rectangle>();
  }
  
  public void draw(FieldMarker pFieldMarker) {
    if ((pFieldMarker != null) && StringTool.isProvided(pFieldMarker.getHomeText()) && (getClient().getParameters().getMode() == ClientMode.PLAYER)) {
      remove(pFieldMarker);
      Graphics2D g2d = getImage().createGraphics();
      g2d.setColor(COLOR_MARKER);
      if (pFieldMarker.getHomeText().length() < 2) {
        g2d.setFont(new Font("Sans Serif", Font.BOLD, 16));
      } else {
        g2d.setFont(new Font("Sans Serif", Font.BOLD, 12));
      }
      FontMetrics metrics = g2d.getFontMetrics();
      Rectangle2D textBounds = metrics.getStringBounds(pFieldMarker.getHomeText(), g2d);
      int x = FIELD_IMAGE_OFFSET_CENTER_X + (pFieldMarker.getCoordinate().getX() * FIELD_SQUARE_SIZE) - (int) (textBounds.getWidth() / 2) + 1;
      int y = FIELD_IMAGE_OFFSET_CENTER_Y + (pFieldMarker.getCoordinate().getY() * FIELD_SQUARE_SIZE) + (int) (textBounds.getHeight() / 2) - 2;
      g2d.drawString(pFieldMarker.getHomeText(), x, y);
      Rectangle bounds = new Rectangle(x, y - (int) textBounds.getHeight(), (int) Math.ceil(textBounds.getWidth()), (int) Math.ceil(textBounds.getHeight()));
      fFieldMarkerBounds.put(pFieldMarker.getCoordinate(), bounds);
      addUpdatedArea(bounds);
      g2d.dispose();
    }
  }
  
  public void remove(FieldMarker pFieldMarker) {
    if ((pFieldMarker != null) && (ClientMode.PLAYER == getClient().getMode())) {
      Rectangle bounds = fFieldMarkerBounds.get(pFieldMarker.getCoordinate());
      if (bounds != null) {
        clear(bounds.x, bounds.y, bounds.width, bounds.height, true);
        fFieldMarkerBounds.remove(pFieldMarker.getCoordinate());
      }
    }
  }

  public void fieldModelChanged(FieldModelChangeEvent pChangeEvent) {
    switch (pChangeEvent.getType()) {
      case FieldModelChangeEvent.TYPE_FIELD_MARKER:
        if (pChangeEvent.isAdded()) {
          draw((FieldMarker) pChangeEvent.getNewValue());
        } else {
          remove((FieldMarker) pChangeEvent.getOldValue());
        }
        break;
    }
  }
  
  public void init() {
    clear(true);
    fFieldMarkerBounds.clear();
    Game game = getClient().getGame();
    FieldModel fieldModel = game.getFieldModel();
    if (fieldModel != null) {
      for (FieldMarker fieldMarker : fieldModel.getFieldMarkers()) {
        draw(fieldMarker);
      }
      fieldModel.addListener(this);
    }
  }
  
}
