package com.fumbbl.ffb.client.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ColorIcon implements Icon {
	private final BufferedImage image;
	public ColorIcon(int width, int height, Color color) {
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = image.createGraphics();
		graphics.setColor(Color.BLACK);
		graphics.fillRect(0, 0, width, height);
		graphics.setColor(color);
		graphics.fillRect(1, 1, width - 2, height - 2);
		graphics.dispose();
	}


	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		g.drawImage(image, x, y, c);
	}

	@Override
	public int getIconWidth() {
		return image.getWidth();
	}

	@Override
	public int getIconHeight() {
		return image.getHeight();
	}
}
