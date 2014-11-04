package com.balancedbytes.games.ffb.client.util;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import com.balancedbytes.games.ffb.client.UserInterface;

/**
 * 
 * @author Kalimar
 */
public final class UtilClientCursor {
  
  public static void setCustomCursor(UserInterface pUserInterface, String pCursorIconProperty) {
    BufferedImage customCursorIcon = pUserInterface.getIconCache().getIconByProperty(pCursorIconProperty);
    Cursor customCursor = Toolkit.getDefaultToolkit().createCustomCursor(customCursorIcon, new Point(0, 0), "CustomCursor");
    pUserInterface.getFieldComponent().setCursor(customCursor);
  }

  public static void setDefaultCursor(UserInterface pUserInterface) {
    pUserInterface.getFieldComponent().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
  }

}
