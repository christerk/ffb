package com.balancedbytes.games.ffb.client.util;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;


/**
 * 
 * @author Kalimar
 */
public class UtilClientGraphics {

  public static void drawShadowedText(Graphics2D pG2d, String pText, int pX, int pY) {
    pG2d.setColor(Color.BLACK);
    pG2d.drawString(pText, pX + 1, pY + 1);
    pG2d.setColor(Color.WHITE);
    pG2d.drawString(pText, pX, pY);
  }
  
  public static int findCenteredX(Graphics2D pG2d, String pText, int pWidth) {
    FontMetrics metrics = pG2d.getFontMetrics();
    Rectangle2D bounds = metrics.getStringBounds(pText, pG2d);
    return ((pWidth - (int) bounds.getWidth()) / 2);
  }

}
