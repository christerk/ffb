package com.fumbbl.ffb.client.layer;

import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IClientPropertyValue;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.TrackNumber;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.FontCache;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.util.ArrayTool;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * @author Kalimar
 */
public class FieldLayerUnderPlayers extends FieldLayer {

	private FieldCoordinate[] fMovePath;

	public FieldLayerUnderPlayers(FantasyFootballClient pClient, DimensionProvider dimensionProvider, FontCache fontCache) {
		super(pClient, dimensionProvider, fontCache);
	}

	public void drawTrackNumber(TrackNumber pTrackNumber) {
		drawTrackNumber(pTrackNumber.getCoordinate(), pTrackNumber.getNumber(), Color.CYAN);
	}

	public void drawMovePath(FieldCoordinate[] pCoordinates, int pStartNumber) {
		fMovePath = pCoordinates;
		if (ArrayTool.isProvided(pCoordinates)) {
			for (int i = 0; i < pCoordinates.length; i++) {
				drawTrackNumber(pCoordinates[i], pStartNumber + i + 1, Color.WHITE);
			}
		}
	}

	public FieldCoordinate[] getMovePath() {
		return fMovePath;
	}

	public synchronized boolean clearMovePath() {
		synchronized (this) {
			Game game = getClient().getGame();
			boolean pathCleared = ArrayTool.isProvided(fMovePath);
			if (pathCleared) {
				for (FieldCoordinate fieldCoordinate : fMovePath) {
					TrackNumber trackNumber = game.getFieldModel().getTrackNumber(fieldCoordinate);
					if (trackNumber != null) {
						drawTrackNumber(trackNumber);
					} else {
						clear(fieldCoordinate, true);
					}
				}
				fMovePath = null;
			}
			return pathCleared;
		}
	}

	private void drawTrackNumber(FieldCoordinate pCoordinate, int pNumber, Color pColor) {
		if (pCoordinate != null) {
			clear(pCoordinate, true);
			String numberString = Integer.toString(pNumber);
			Graphics2D g2d = getImage().createGraphics();
			g2d.setFont(fontCache.font(Font.BOLD, 15));
			FontMetrics metrics = g2d.getFontMetrics();
			Rectangle2D numberBounds = metrics.getStringBounds(numberString, g2d);
			Dimension dimension = dimensionProvider.mapToLocal(pCoordinate, true);
			int baselineX = dimension.width - (int) (numberBounds.getWidth() / 2) + 1;
			int baselineY = dimension.height + (int) (numberBounds.getHeight() / 2) - 2;
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
			g2d.setColor(Color.BLACK);
			g2d.drawString(numberString, baselineX + 1, baselineY + 1);
			g2d.setColor(pColor);
			g2d.drawString(numberString, baselineX, baselineY);
			g2d.dispose();
		}
	}

	public void removeTrackNumber(TrackNumber pTrackNumber) {
		if (pTrackNumber != null) {
			clear(pTrackNumber.getCoordinate(), true);
		}
	}

	public void init() {
		clear(true);
		Game game = getClient().getGame();
		FieldModel fieldModel = game.getFieldModel();
		if (fieldModel != null) {
			TrackNumber[] trackNumbers = fieldModel.getTrackNumbers();
			for (TrackNumber trackNumber : trackNumbers) {
				drawTrackNumber(trackNumber);
			}
		}

		FieldCoordinate coordinate = null;

		if (game.getTurnMode() == TurnMode.SETUP && game.isHomePlaying() && game.isSetupOffense()) {
			coordinate = FieldCoordinate.SWEET_SPOT_HOME;
		}

		if (game.getTurnMode() == TurnMode.KICKOFF && game.isHomePlaying()) {
			coordinate = FieldCoordinate.SWEET_SPOT_AWAY;
		}

		if (coordinate != null) {
			String sweetSpot = getClient().getProperty(CommonProperty.SETTING_SWEET_SPOT);

			if (IClientPropertyValue.SETTING_SWEET_SPOT_BLACK.equals(sweetSpot)) {
				drawSweetSpot(IIconProperty.AUGMENTS_SWEET_BLACK, coordinate);
			} else if (IClientPropertyValue.SETTING_SWEET_SPOT_WHITE.equals(sweetSpot)) {
				drawSweetSpot(IIconProperty.AUGMENTS_SWEET_WHITE, coordinate);
			}

		}
	}


	private void drawSweetSpot(String iconProperty, FieldCoordinate coordinate) {
		BufferedImage icon = getClient().getUserInterface().getIconCache().getIconByProperty(iconProperty);
		draw(icon, coordinate, 1.0f);
	}
}
