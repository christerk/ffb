package com.fumbbl.ffb.client.layer;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.client.*;
import com.fumbbl.ffb.marking.FieldMarker;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.util.StringTool;

import java.awt.Dimension;
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

	private final Map<FieldCoordinate, Rectangle> fFieldMarkerBounds;

	public FieldLayerMarker(FantasyFootballClient pClient, UiDimensionProvider uiDimensionProvider, PitchDimensionProvider pitchDimensionProvider, FontCache fontCache) {
		super(pClient, uiDimensionProvider, pitchDimensionProvider, fontCache);
		fFieldMarkerBounds = new HashMap<>();
	}

	public void drawFieldMarker(FieldMarker pFieldMarker) {
		drawFieldMarker(pFieldMarker, getClient().getParameters().getMode() == ClientMode.PLAYER);
	}

	public void drawFieldMarker(FieldMarker pFieldMarker, boolean draw) {
		if ((pFieldMarker != null) && StringTool.isProvided(pFieldMarker.getHomeText())
			&& draw) {
			StyleProvider styleProvider = getClient().getUserInterface().getStyleProvider();
			removeFieldMarker(pFieldMarker);
			Graphics2D g2d = getImage().createGraphics();
			g2d.setColor(styleProvider.getFieldMarker());
			if (pFieldMarker.getHomeText().length() < 2) {
				g2d.setFont(fontCache.font(Font.BOLD, 16, pitchDimensionProvider));
			} else {
				g2d.setFont(fontCache.font(Font.BOLD, 12, pitchDimensionProvider));
			}
			FontMetrics metrics = g2d.getFontMetrics();
			Rectangle2D textBounds = metrics.getStringBounds(pFieldMarker.getHomeText(), g2d);
			Dimension dimension = pitchDimensionProvider.mapToLocal(pFieldMarker.getCoordinate(), true);
			int x = dimension.width - (int) (textBounds.getWidth() / 2) + 1;
			int y = dimension.height + (int) (textBounds.getHeight() / 2) - 2;
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
