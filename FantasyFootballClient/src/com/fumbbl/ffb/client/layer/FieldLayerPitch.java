package com.fumbbl.ffb.client.layer;

import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.IconCache;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * @author Kalimar
 */
public class FieldLayerPitch extends FieldLayer {

	public FieldLayerPitch(FantasyFootballClient pClient, DimensionProvider dimensionProvider) {
		super(pClient, dimensionProvider);
	}

	public void drawWeather(Weather pWeather) {
		if (pWeather != null) {
			IconCache iconCache = getClient().getUserInterface().getIconCache();
			BufferedImage fieldImage = iconCache.getPitch(getClient().getGame(), pWeather);
			if (fieldImage != null) {
				draw(fieldImage, 0, 0, 1.0f);
			}
			drawTeamNames();
		}
	}

	private void drawTeamNames() {

		Game game = getClient().getGame();
		String teamNameHome = game.getTeamHome().getName();
		String teamNameAway = game.getTeamAway().getName();

		if ((teamNameHome != null) && (teamNameAway != null)) {

			teamNameHome = teamNameHome.toUpperCase();
			teamNameAway = teamNameAway.toUpperCase();

			Graphics2D g2d = getGraphicsWithFontAndColor();
			FontMetrics metrics = g2d.getFontMetrics();
			Rectangle2D teamNameBounds = metrics.getStringBounds(teamNameHome, g2d);
			int translateX = (int) ((0.5 * FIELD_SQUARE_SIZE) + (teamNameBounds.getHeight() / 2)) - 4;
			int translateY = (int) ((getImage().getHeight() / 2) + (teamNameBounds.getWidth() / 2));
			g2d.translate(translateX, translateY);
			g2d.rotate(-Math.PI / 2.0);
			g2d.drawString(teamNameHome, 0, 0);
			g2d.dispose();

			g2d = getGraphicsWithFontAndColor();
			metrics = g2d.getFontMetrics();
			teamNameBounds = metrics.getStringBounds(teamNameAway, g2d);
			translateX = (int) ((25.0 * FIELD_SQUARE_SIZE) + (teamNameBounds.getHeight() / 2)) - 4;
			translateY = (int) ((getImage().getHeight() / 2) - (teamNameBounds.getWidth() / 2));
			g2d.translate(translateX, translateY);
			g2d.rotate(Math.PI / 2.0);
			g2d.drawString(teamNameAway, 0, 0);
			g2d.dispose();

		}

	}

	private Graphics2D getGraphicsWithFontAndColor() {
		Graphics2D g2d = getImage().createGraphics();
		g2d.setColor(Color.WHITE);
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
		g2d.setFont(new Font("Sans Serif", Font.BOLD, 20));
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		return g2d;
	}

	public void init() {
		clear(true);
		FieldModel fieldModel = getClient().getGame().getFieldModel();
		if (fieldModel != null) {
			drawWeather(fieldModel.getWeather());
		}
	}

}
