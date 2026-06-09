package com.fumbbl.ffb.client.util;

import com.fumbbl.ffb.client.StyleProvider;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

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


    /**
     * Intended for development purposes to see the actual size of the image.
     *
     * @param image
     */
    public static void drawInsideBorder(BufferedImage image) {
        drawInsideBorder(image, Color.WHITE, 2);
    }

    /**
     * Intended for development purposes to see the actual size of the image.
     *
     * @param image
     * @param borderColor
     * @param borderWidth
     */
    public static void drawInsideBorder(BufferedImage image, Color borderColor, int borderWidth) {
        if (image == null || borderWidth <= 0) {
            return;
        }

        // 1. Get the Graphics2D object from the image
        Graphics2D g2d = image.createGraphics();

        try {
            // 2. Set the color and stroke thickness
            g2d.setColor(borderColor);
            g2d.setStroke(new BasicStroke(borderWidth));

            // 3. Calculate coordinates to keep the stroke entirely inside
            // Because Java2D draws strokes centered on the line, we shift by half the width
            int offset = borderWidth / 2;
            int x = offset;
            int y = offset;
            int width = image.getWidth() - borderWidth;
            int height = image.getHeight() - borderWidth;

            // 4. Draw the rectangle
            g2d.drawRect(x, y, width, height);

        } finally {
            // 5. Always dispose the graphics context to free up resources
            g2d.dispose();
        }
    }

}
