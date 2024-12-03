package com.fumbbl.ffb.client.util;

import com.fumbbl.ffb.client.UserInterface;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * 
 * @author Kalimar
 */
public final class UtilClientCursor {

	public static void setCustomCursor(UserInterface pUserInterface, String pCursorIconProperty) {
		BufferedImage customCursorIcon = pUserInterface.getIconCache().getIconByProperty(pCursorIconProperty, pUserInterface.getPitchDimensionProvider());
		Cursor customCursor = Toolkit.getDefaultToolkit().createCustomCursor(customCursorIcon, new Point(0, 0),
				"CustomCursor");
		pUserInterface.getFieldComponent().setCursor(customCursor);
	}

	public static void setDefaultCursor(UserInterface pUserInterface) {
		pUserInterface.getFieldComponent().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}

}
