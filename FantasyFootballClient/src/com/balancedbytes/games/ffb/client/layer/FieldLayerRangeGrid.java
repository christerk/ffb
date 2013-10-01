package com.balancedbytes.games.ffb.client.layer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.FieldCoordinateBounds;
import com.balancedbytes.games.ffb.PassingDistance;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.util.UtilPassing;


/**
 * 
 * @author Kalimar
 */
public class FieldLayerRangeGrid extends FieldLayer {
  
  private FieldCoordinate fCenterCoordinate;
  
  public FieldLayerRangeGrid(FantasyFootballClient pClient) {
    super(pClient);
  }
  
  public boolean drawRangeGrid(FieldCoordinate pCenterCoordinate, boolean pThrowTeamMate) {
    if ((pCenterCoordinate != null) && !pCenterCoordinate.equals(fCenterCoordinate)) {
      fCenterCoordinate = pCenterCoordinate;
      for (int y = 0; y < FieldCoordinate.FIELD_HEIGHT; y++) {
        for (int x = 0; x < FieldCoordinate.FIELD_WIDTH; x++) {
          FieldCoordinate coordinate = new FieldCoordinate(x, y);
          clear(coordinate, false);          
          PassingDistance passingDistance = UtilPassing.findPassingDistance(getClient().getGame(), fCenterCoordinate, coordinate, pThrowTeamMate);
          if (passingDistance != null) {
            markSquare(coordinate, FieldLayerRangeRuler.getColorForPassingDistance(passingDistance));
          }
        }
      }
      addUpdatedArea(new Rectangle(0, 0, getImage().getWidth(), getImage().getHeight()));
      return true;
    } else {
      return false;
    }
  } 

  public boolean clearRangeGrid() {
    if (fCenterCoordinate != null) {
      fCenterCoordinate = null;
      clear(true);
      return true;
    } else {
      return false;
    }
  }  
 
  private void markSquare(FieldCoordinate pCoordinate, Color pColor) {
    if ((pCoordinate != null) && FieldCoordinateBounds.FIELD.isInBounds(pCoordinate)) {
      int x = pCoordinate.getX() * FIELD_SQUARE_SIZE;
      int y = pCoordinate.getY() * FIELD_SQUARE_SIZE;
      Rectangle bounds = new Rectangle(x + 1, y + 1, FIELD_SQUARE_SIZE - 2, FIELD_SQUARE_SIZE - 2);
      Graphics2D g2d = getImage().createGraphics();
      g2d.setPaint(pColor);
      g2d.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
      g2d.dispose();
    }
  }
  
  public FieldCoordinate getCenterCoordinate() {
    return fCenterCoordinate;
  }
    
}
