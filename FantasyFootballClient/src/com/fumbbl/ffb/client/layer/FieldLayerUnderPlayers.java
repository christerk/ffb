package com.fumbbl.ffb.client.layer;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.TrackNumber;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.util.ArrayTool;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
 * @author Kalimar
 */
public class FieldLayerUnderPlayers extends FieldLayer {

	private FieldCoordinate[] fMovePath;

	public FieldLayerUnderPlayers(FantasyFootballClient pClient, DimensionProvider dimensionProvider) {
		super(pClient, dimensionProvider);
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
				for (int i = 0; i < fMovePath.length; i++) {
					TrackNumber trackNumber = game.getFieldModel().getTrackNumber(fMovePath[i]);
					if (trackNumber != null) {
						drawTrackNumber(trackNumber);
					} else {
						clear(fMovePath[i], true);
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
			g2d.setFont(new Font("Sans Serif", Font.BOLD, 15));
			FontMetrics metrics = g2d.getFontMetrics();
			Rectangle2D numberBounds = metrics.getStringBounds(numberString, g2d);
			int baselineX = FIELD_IMAGE_OFFSET_CENTER_X + (pCoordinate.getX() * FIELD_SQUARE_SIZE)
					- (int) (numberBounds.getWidth() / 2) + 1;
			int baselineY = FIELD_IMAGE_OFFSET_CENTER_Y + (pCoordinate.getY() * FIELD_SQUARE_SIZE)
					+ (int) (numberBounds.getHeight() / 2) - 2;
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
		FieldModel fieldModel = getClient().getGame().getFieldModel();
		if (fieldModel != null) {
			TrackNumber[] trackNumbers = fieldModel.getTrackNumbers();
			for (int i = 0; i < trackNumbers.length; i++) {
				drawTrackNumber(trackNumbers[i]);
			}
		}
	}

}
