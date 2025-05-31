package com.fumbbl.ffb.client.util;

import com.fumbbl.ffb.client.UserInterface;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * 
 * @author Kalimar
 */
public final class UtilClientCursor {

	public static void setCustomCursor(UserInterface pUserInterface, String pCursorIconProperty, boolean top) {
		BufferedImage customCursorIcon = pUserInterface.getIconCache().getIconByProperty(pCursorIconProperty, pUserInterface.getPitchDimensionProvider());

		Dimension bestCursorSize = Toolkit.getDefaultToolkit().getBestCursorSize(customCursorIcon.getWidth(), customCursorIcon.getHeight());

		int y = top ? 0 : bestCursorSize.height - 1;
		Cursor customCursor = Toolkit.getDefaultToolkit().createCustomCursor(customCursorIcon, new Point(0, y),
			"CustomCursor");
		pUserInterface.getFieldComponent().setCursor(customCursor);

	}

	public static void setCustomCursor(UserInterface pUserInterface, String pCursorIconProperty) {
		setCustomCursor(pUserInterface, pCursorIconProperty, true);
	}

	public static void setDefaultCursor(UserInterface pUserInterface) {
		pUserInterface.getFieldComponent().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}

}
