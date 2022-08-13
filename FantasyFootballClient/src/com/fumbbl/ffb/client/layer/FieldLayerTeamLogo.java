package com.fumbbl.ffb.client.layer;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.IClientProperty;
import com.fumbbl.ffb.client.IClientPropertyValue;
import com.fumbbl.ffb.client.IconCache;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.util.StringTool;

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
public class FieldLayerTeamLogo extends FieldLayer {

	public FieldLayerTeamLogo(FantasyFootballClient pClient, DimensionProvider dimensionProvider) {
		super(pClient, dimensionProvider);
	}

	public void drawDistanceMarkers() {
		for (int x = 1; x < 25; x++) {
			drawDistanceMarker(x);
		}
	}

	private void drawDistanceMarker(int pX) {
		int distance = (pX >= 13) ? (25 - pX) : pX;
		String distanceString = Integer.toString(distance);
		Graphics2D g2d = getImage().createGraphics();
		g2d.setFont(new Font("Sans Serif", Font.BOLD, 12));
		FontMetrics metrics = g2d.getFontMetrics();
		Rectangle2D distanceBounds = metrics.getStringBounds(distanceString, g2d);
		Color distanceColor = (pX >= 13) ? new Color(120, 120, 255) : new Color(255, 120, 120);
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
		FieldCoordinate upperLine = new FieldCoordinate(pX, 0);
		clear(upperLine, true); // marks square as updated
		int x = FIELD_IMAGE_OFFSET_CENTER_X + (upperLine.getX() * FIELD_SQUARE_SIZE) - (int) (distanceBounds.getWidth() / 2)
				+ 1;
		int y = FIELD_IMAGE_OFFSET_CENTER_Y + (upperLine.getY() * FIELD_SQUARE_SIZE)
				+ (int) (distanceBounds.getHeight() / 2) - 8;
		g2d.setColor(Color.BLACK);
		g2d.drawString(distanceString, x + 1, y + 1);
		g2d.setColor(distanceColor);
		g2d.drawString(distanceString, x, y);
		FieldCoordinate lowerLine = new FieldCoordinate(pX, 14);
		clear(lowerLine, true); // marks square as updated
		x = FIELD_IMAGE_OFFSET_CENTER_X + (lowerLine.getX() * FIELD_SQUARE_SIZE) - (int) (distanceBounds.getWidth() / 2)
				+ 1;
		y = FIELD_IMAGE_OFFSET_CENTER_Y + (lowerLine.getY() * FIELD_SQUARE_SIZE) + (int) (distanceBounds.getHeight() / 2)
				+ 4;
		g2d.setColor(Color.BLACK);
		g2d.drawString(distanceString, x + 1, y + 1);
		g2d.setColor(distanceColor);
		g2d.drawString(distanceString, x, y);
		g2d.dispose();
	}

	private void drawTeamLogo(Team pTeam, boolean pHomeTeam) {
		if ((pTeam != null) && StringTool.isProvided(pTeam.getLogoUrl())) {
			Dimension fieldDimension = getClient().getUserInterface().getDimensionProvider().dimension(DimensionProvider.Component.FIELD);
			IconCache iconCache = getClient().getUserInterface().getIconCache();
			BufferedImage teamLogo = iconCache.getIconByUrl(IconCache.findTeamLogoUrl(pTeam));
			if (teamLogo != null) {
				Graphics2D g2d = getImage().createGraphics();
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
				int x, y;
				if (pHomeTeam) {
					x = (fieldDimension.width / 4) - (teamLogo.getWidth() / 2) + (FIELD_SQUARE_SIZE / 2);
				} else {
					x = (3 * (fieldDimension.width / 4)) - (teamLogo.getWidth() / 2) - (FIELD_SQUARE_SIZE / 2);
				}
				y = (fieldDimension.height / 2) - (teamLogo.getHeight() / 2);
				g2d.setClip(pHomeTeam ? FIELD_SQUARE_SIZE : fieldDimension.width / 2, 0,
					(fieldDimension.width / 2) - FIELD_SQUARE_SIZE, fieldDimension.height);
				g2d.drawImage(teamLogo, x, y, null);
				g2d.dispose();
			}
		}
	}

	public void init() {
		clear(true);
		Game game = getClient().getGame();
		String markingsSetting = getClient().getProperty(IClientProperty.SETTING_PITCH_MARKINGS);
		if (IClientPropertyValue.SETTING_PITCH_MARKINGS_ON.equals(markingsSetting)) {
			drawDistanceMarkers();
		}
		String teamLogosSetting = getClient().getProperty(IClientProperty.SETTING_TEAM_LOGOS);
		if (IClientPropertyValue.SETTING_TEAM_LOGOS_BOTH.equals(teamLogosSetting)) {
			drawTeamLogo(game.getTeamHome(), true);
			drawTeamLogo(game.getTeamAway(), false);
		}
		if (IClientPropertyValue.SETTING_TEAM_LOGOS_OWN.equals(teamLogosSetting)) {
			drawTeamLogo(game.getTeamHome(), true);
		}
	}

}
