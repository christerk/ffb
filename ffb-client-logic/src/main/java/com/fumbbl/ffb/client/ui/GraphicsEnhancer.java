package com.fumbbl.ffb.client.ui;

import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class GraphicsEnhancer {

	public static void applyAAHints(Graphics2D g2d) {
		g2d.setRenderingHint(
				RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
	}
}
