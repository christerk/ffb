package com.fumbbl.ffb.client.layer;

import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.FontCache;
import com.fumbbl.ffb.client.IconCache;
import com.fumbbl.ffb.client.LayoutSettings;
import com.fumbbl.ffb.client.PitchDimensionProvider;
import com.fumbbl.ffb.client.PitchViewport;
import com.fumbbl.ffb.client.UiDimensionProvider;
import com.fumbbl.ffb.client.ui.GraphicsEnhancer;
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

	public FieldLayerPitch(FantasyFootballClient pClient, UiDimensionProvider uiDimensionProvider,
												 PitchDimensionProvider pitchDimensionProvider,
												 PitchViewport pitchViewport, FontCache fontCache) {
		super(pClient, uiDimensionProvider, pitchDimensionProvider, pitchViewport, fontCache);
	}

	public void drawWeather(Weather pWeather) {
		if (pWeather != null) {
			IconCache iconCache = getClient().getUserInterface().getIconCache();
			BufferedImage fieldImage = iconCache.getUnscaledPitch(getClient().getGame(), pWeather);
			if (fieldImage != null) {
				drawPitch(fieldImage);
			}
			drawTeamNames();
		}
	}

	protected void drawPitch(BufferedImage pImage) {
		Graphics2D g2d = fImage.createGraphics();
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) 1.0));
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		int origin = pitchViewport.gridOffset();
		double scale = (double) pitchViewport.squareSize() / LayoutSettings.BASE_SQUARE_SIZE;
		if (uiDimensionProvider.isPitchPortrait()) {
			g2d.translate(origin, origin + 26 * pitchViewport.squareSize());
			g2d.rotate(-Math.PI / 2);
		} else {
			g2d.translate(origin, origin);
		}
		g2d.scale(scale, scale);
		g2d.drawImage(pImage, -1, -1, 782, 452, null);
		// Temporary diagnostic: show the tile boundaries calculated by the viewport.
		g2d.setTransform(new java.awt.geom.AffineTransform());
		g2d.setColor(Color.MAGENTA);
		int columns = uiDimensionProvider.isPitchPortrait() ? 15 : 26;
		int rows = uiDimensionProvider.isPitchPortrait() ? 26 : 15;
		for (int x = 0; x <= columns; x++) {
			int boundary = origin + x * pitchViewport.squareSize();
			g2d.drawLine(boundary, 0, boundary, size.height - 1);
		}
		for (int y = 0; y <= rows; y++) {
			int boundary = origin + y * pitchViewport.squareSize();
			g2d.drawLine(0, boundary, size.width - 1, boundary);
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
			if (pitchDimensionProvider.isPitchPortrait()) {
				translateX = (int) (((double) getImage().getWidth() / 2) - (teamNameBounds.getWidth() / 2));
				translateY = (int) (pitchViewport.squareSize(25.5) + (teamNameBounds.getHeight() / 2)) - 4;
				g2d.translate(translateX, translateY);
			} else {
				translateX = (int) (pitchViewport.squareSize(0.5) + (teamNameBounds.getHeight() / 2)) - 4;
				translateY = (int) (((double) getImage().getHeight() / 2) + (teamNameBounds.getWidth() / 2));
				g2d.translate(translateX, translateY);
				g2d.rotate(-Math.PI / 2.0);
			}
			g2d.drawString(teamNameHome, 0, 0);
			g2d.dispose();

			g2d = getGraphicsWithFontAndColor();
			metrics = g2d.getFontMetrics();
			teamNameBounds = metrics.getStringBounds(teamNameAway, g2d);
			if (pitchDimensionProvider.isPitchPortrait()) {
				translateX = (int) (((double) getImage().getWidth() / 2) - (teamNameBounds.getWidth() / 2));
				translateY = (int) (pitchViewport.squareSize(0.5) + (teamNameBounds.getHeight() / 2)) - 4;
				g2d.translate(translateX, translateY);
			} else {
				translateX = (int) (pitchViewport.squareSize(25) + (teamNameBounds.getHeight() / 2)) - 4;
				translateY = (int) (((double) getImage().getHeight() / 2) - (teamNameBounds.getWidth() / 2));
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
		g2d.setFont(fontCache.font(Font.BOLD, 20, pitchDimensionProvider));
		GraphicsEnhancer.applyAAHints(g2d);
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
