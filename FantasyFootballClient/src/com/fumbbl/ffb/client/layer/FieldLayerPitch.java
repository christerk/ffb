package com.fumbbl.ffb.client.layer;

import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.FontCache;
import com.fumbbl.ffb.client.IconCache;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * @author Kalimar
 */
public class FieldLayerPitch extends FieldLayer {

	public FieldLayerPitch(FantasyFootballClient pClient, DimensionProvider dimensionProvider, FontCache fontCache) {
		super(pClient, dimensionProvider, fontCache);
	}

	public void drawWeather(Weather pWeather) {
		if (pWeather != null) {
			IconCache iconCache = getClient().getUserInterface().getIconCache();
			BufferedImage fieldImage = iconCache.getPitch(getClient().getGame(), pWeather);
			if (fieldImage != null) {
				drawPitch(fieldImage);
			}
			drawTeamNames();
		}
	}

	protected void drawPitch(BufferedImage pImage) {
		Graphics2D g2d = fImage.createGraphics();
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) 1.0));
		if (dimensionProvider.isPitchPortrait()) {
			g2d.translate(0, size.height);
			g2d.rotate(-Math.PI / 2);
			g2d.drawImage(pImage, 0, 0, fImage.getHeight(), fImage.getWidth(),null);
		} else {
			g2d.drawImage(pImage, 0, 0, fImage.getWidth(), fImage.getHeight(), null);
		}
		g2d.dispose();
		Rectangle updatedArea = new Rectangle(0, 0, fImage.getWidth(), fImage.getHeight());
		addUpdatedArea(updatedArea);
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
			int translateX;
			int translateY;
			int squareSize = dimensionProvider.fieldSquareSize();
			if (dimensionProvider.isPitchPortrait()) {
				translateX = (int) ((getImage().getWidth() / 2) - (teamNameBounds.getWidth() / 2));
				translateY = (int) ((25.5 * squareSize) + (teamNameBounds.getHeight() / 2)) - 4;
				g2d.translate(translateX, translateY);
			} else {
				translateX = (int) ((0.5 * squareSize) + (teamNameBounds.getHeight() / 2)) - 4;
				translateY = (int) ((getImage().getHeight() / 2) + (teamNameBounds.getWidth() / 2));
				g2d.translate(translateX, translateY);
				g2d.rotate(-Math.PI / 2.0);
			}
			g2d.drawString(teamNameHome, 0, 0);
			g2d.dispose();

			g2d = getGraphicsWithFontAndColor();
			metrics = g2d.getFontMetrics();
			teamNameBounds = metrics.getStringBounds(teamNameAway, g2d);
			if (dimensionProvider.isPitchPortrait()) {
				translateX = (int) ((getImage().getWidth() / 2) - (teamNameBounds.getWidth() / 2));
				translateY = (int) ((0.5 * squareSize) + (teamNameBounds.getHeight() / 2)) - 4;
				g2d.translate(translateX, translateY);
			} else {
				translateX = (int) ((25.0 * squareSize) + (teamNameBounds.getHeight() / 2)) - 4;
				translateY = (int) ((getImage().getHeight() / 2) - (teamNameBounds.getWidth() / 2));
				g2d.translate(translateX, translateY);
				g2d.rotate(Math.PI / 2.0);
			}
			g2d.drawString(teamNameAway, 0, 0);
			g2d.dispose();

		}

	}

	private Graphics2D getGraphicsWithFontAndColor() {
		Graphics2D g2d = getImage().createGraphics();
		g2d.setColor(Color.WHITE);
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
		g2d.setFont(fontCache().font(Font.BOLD, 20));
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		return g2d;
	}

	public void init() {
		clear(true);
		FieldModel fieldModel = getClient().getGame().getFieldModel();
		if (fieldModel != null) {
			Weather weather = fieldModel.getWeather();
			if (weather != null) {
				drawWeather(weather);
				return;
			}
		}
		drawWeather(Weather.INTRO);
	}

}
