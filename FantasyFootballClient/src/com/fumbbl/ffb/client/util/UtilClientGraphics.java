package com.fumbbl.ffb.client.util;

import com.fumbbl.ffb.client.StyleProvider;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
 * @author Kalimar
 */
public class UtilClientGraphics {

	public static void drawShadowedText(Graphics2D pG2d, String pText, int pX, int pY, StyleProvider styleProvider) {
		pG2d.setColor(styleProvider.getFrameShadow());
		pG2d.drawString(pText, pX + 1, pY + 1);
		pG2d.setColor(styleProvider.getFrame());
		pG2d.drawString(pText, pX, pY);
	}

	public static int findCenteredX(Graphics2D pG2d, String pText, int pWidth) {
		FontMetrics metrics = pG2d.getFontMetrics();
		Rectangle2D bounds = metrics.getStringBounds(pText, pG2d);
		return ((pWidth - (int) bounds.getWidth()) / 2);
	}

}
