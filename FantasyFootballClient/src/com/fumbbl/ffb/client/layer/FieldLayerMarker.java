package com.fumbbl.ffb.client.layer;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldMarker;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.util.StringTool;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Kalimar
 */
public class FieldLayerMarker extends FieldLayer {

	public static final Color COLOR_MARKER = new Color(1.0f, 1.0f, 1.0f, 1.0f);

	private final Map<FieldCoordinate, Rectangle> fFieldMarkerBounds;

	public FieldLayerMarker(FantasyFootballClient pClient, DimensionProvider dimensionProvider) {
		super(pClient, dimensionProvider);
		fFieldMarkerBounds = new HashMap<>();
	}

	public void drawFieldMarker(FieldMarker pFieldMarker) {
		drawFieldMarker(pFieldMarker, getClient().getParameters().getMode() == ClientMode.PLAYER);
	}

	public void drawFieldMarker(FieldMarker pFieldMarker, boolean draw) {
		if ((pFieldMarker != null) && StringTool.isProvided(pFieldMarker.getHomeText())
			&& draw) {
			removeFieldMarker(pFieldMarker);
			Graphics2D g2d = getImage().createGraphics();
			g2d.setColor(COLOR_MARKER);
			if (pFieldMarker.getHomeText().length() < 2) {
				g2d.setFont(new Font("Sans Serif", Font.BOLD, 16));
			} else {
				g2d.setFont(new Font("Sans Serif", Font.BOLD, 12));
			}
			FontMetrics metrics = g2d.getFontMetrics();
			Rectangle2D textBounds = metrics.getStringBounds(pFieldMarker.getHomeText(), g2d);
			int x = FIELD_IMAGE_OFFSET_CENTER_X + (pFieldMarker.getCoordinate().getX() * FIELD_SQUARE_SIZE)
				- (int) (textBounds.getWidth() / 2) + 1;
			int y = FIELD_IMAGE_OFFSET_CENTER_Y + (pFieldMarker.getCoordinate().getY() * FIELD_SQUARE_SIZE)
				+ (int) (textBounds.getHeight() / 2) - 2;
			g2d.drawString(pFieldMarker.getHomeText(), x, y);
			Rectangle bounds = new Rectangle(x, y - (int) textBounds.getHeight(), (int) Math.ceil(textBounds.getWidth()),
				(int) Math.ceil(textBounds.getHeight()));
			fFieldMarkerBounds.put(pFieldMarker.getCoordinate(), bounds);
			addUpdatedArea(bounds);
			g2d.dispose();
		}
	}

	public void removeFieldMarker(FieldMarker pFieldMarker) {
		removeFieldMarker(pFieldMarker, ClientMode.PLAYER == getClient().getMode());
	}

	public void removeFieldMarker(FieldMarker pFieldMarker, boolean forceRemove) {
		if ((pFieldMarker != null) && forceRemove) {
			Rectangle bounds = fFieldMarkerBounds.get(pFieldMarker.getCoordinate());
			if (bounds != null) {
				clear(bounds.x, bounds.y, bounds.width, bounds.height, true);
				fFieldMarkerBounds.remove(pFieldMarker.getCoordinate());
			}
		}
	}

	public void init() {
		clear(true);
		fFieldMarkerBounds.clear();
		Game game = getClient().getGame();
		FieldModel fieldModel = game.getFieldModel();
		if (fieldModel != null) {
			for (FieldMarker fieldMarker : fieldModel.getFieldMarkers()) {
				drawFieldMarker(fieldMarker);
			}
			for (FieldMarker fieldMarker : fieldModel.getTransientFieldMarkers()) {
				drawFieldMarker(fieldMarker, true);
			}
		}
	}

}
