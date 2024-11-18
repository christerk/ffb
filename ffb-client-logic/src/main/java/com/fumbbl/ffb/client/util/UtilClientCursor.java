package com.fumbbl.ffb.client.util;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import com.fumbbl.ffb.client.RenderContext;
import com.fumbbl.ffb.client.UserInterface;

/**
 * 
 * @author Kalimar
 */
public final class UtilClientCursor {

	public static void setCustomCursor(UserInterface pUserInterface, String pCursorIconProperty) {
		BufferedImage customCursorIcon = pUserInterface.getIconCache().getIconByProperty(pCursorIconProperty, RenderContext.ON_PITCH);
		Cursor customCursor = Toolkit.getDefaultToolkit().createCustomCursor(customCursorIcon, new Point(0, 0),
				"CustomCursor");
		pUserInterface.getFieldComponent().setCursor(customCursor);
	}

	public static void setDefaultCursor(UserInterface pUserInterface) {
		pUserInterface.getFieldComponent().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}

}
