package com.balancedbytes.games.ffb.client.layer;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.IClientProperty;
import com.balancedbytes.games.ffb.client.IClientPropertyValue;
import com.balancedbytes.games.ffb.client.IconCache;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.util.StringTool;



/**
 * 
 * @author Kalimar
 */
public class FieldLayerTeamLogo extends FieldLayer {
  
	public FieldLayerTeamLogo(FantasyFootballClient pClient) {
    super(pClient);
  }
  
  public void drawDistanceMarkers() {
    for (int x = 1; x < 25; x++) {
      drawDistanceMarker(x);
    }
  }
  
  private void drawDistanceMarker(int pX) {
    int distance = (pX >= 13) ? (25 - pX) : pX ;
    String distanceString = Integer.toString(distance);
    Graphics2D g2d = getImage().createGraphics();
    g2d.setFont(new Font("Sans Serif", Font.BOLD, 12));
    FontMetrics metrics = g2d.getFontMetrics();
    Rectangle2D distanceBounds = metrics.getStringBounds(distanceString, g2d);
    Color distanceColor = (pX >= 13) ? new Color(120, 120, 255) : new Color(255, 120, 120);
    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
    FieldCoordinate upperLine = new FieldCoordinate(pX, 0);
    clear(upperLine, true);  // marks square as updated
    int x = FIELD_IMAGE_OFFSET_CENTER_X + (upperLine.getX() * FIELD_SQUARE_SIZE) - (int) (distanceBounds.getWidth() / 2) + 1;
    int y = FIELD_IMAGE_OFFSET_CENTER_Y + (upperLine.getY() * FIELD_SQUARE_SIZE) + (int) (distanceBounds.getHeight() / 2) - 8;
    g2d.setColor(Color.BLACK);
    g2d.drawString(distanceString, x + 1, y + 1);
    g2d.setColor(distanceColor);
    g2d.drawString(distanceString, x, y);
    FieldCoordinate lowerLine = new FieldCoordinate(pX, 14);
    clear(lowerLine, true);  // marks square as updated
    x = FIELD_IMAGE_OFFSET_CENTER_X + (lowerLine.getX() * FIELD_SQUARE_SIZE) - (int) (distanceBounds.getWidth() / 2) + 1;
    y = FIELD_IMAGE_OFFSET_CENTER_Y + (lowerLine.getY() * FIELD_SQUARE_SIZE) + (int) (distanceBounds.getHeight() / 2) + 4;
    g2d.setColor(Color.BLACK);
    g2d.drawString(distanceString, x + 1, y + 1);
    g2d.setColor(distanceColor);
    g2d.drawString(distanceString, x, y);
    g2d.dispose();
  }
  
  private void drawTeamLogo(Team pTeam, boolean pHomeTeam) {
    if ((pTeam != null) && StringTool.isProvided(pTeam.getLogoUrl())) {
      IconCache iconCache = getClient().getUserInterface().getIconCache();
      BufferedImage teamLogo = iconCache.getIconByUrl(IconCache.findTeamLogoUrl(pTeam));
      if (teamLogo != null) {
        Graphics2D g2d = getImage().createGraphics();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
        int x, y;
        if (pHomeTeam) {
          x = (FIELD_IMAGE_WIDTH / 4) - (teamLogo.getWidth() / 2);
        } else {
          x = (3 * (FIELD_IMAGE_WIDTH / 4)) - (teamLogo.getWidth() / 2);
        }
        y = (FIELD_IMAGE_HEIGHT / 2) - (teamLogo.getHeight() / 2);
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
