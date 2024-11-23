package com.fumbbl.ffb.client.layer;

import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IClientPropertyValue;
import com.fumbbl.ffb.client.Component;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.FontCache;
import com.fumbbl.ffb.client.IconCache;
import com.fumbbl.ffb.client.RenderContext;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.util.StringTool;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * @author Kalimar
 */
public class FieldLayerTeamLogo extends FieldLayer {

  private static final Color AWAY_COLOR = new Color(0, 0, 255);
  private static final Color HOME_COLOR = new Color(255, 120, 120);

  private static final String[] LETTERS =
    new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N"};

  public FieldLayerTeamLogo(FantasyFootballClient pClient, DimensionProvider dimensionProvider, FontCache fontCache) {
    super(pClient, dimensionProvider, fontCache);
  }

  public void drawDistanceMarkers() {
    for (int x = 1; x < 25; x++) {
      drawDistanceMarker(x);
    }
  }

  public void drawRowMarkers() {
    for (int y = 1; y < 14; y++) {
      drawRowMarker(y);
    }
  }

  private void drawRowMarker(int pY) {
    String distanceString = LETTERS[pY];
    Graphics2D g2d = getImage().createGraphics();
    g2d.setFont(fontCache.font(Font.BOLD, 12, RenderContext.ON_PITCH));
    FontMetrics metrics = g2d.getFontMetrics();
    Rectangle2D distanceBounds = metrics.getStringBounds(distanceString, g2d);
    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
    Color heightColor = HOME_COLOR;
    FieldCoordinate homeZone = new FieldCoordinate(1, pY);
    clear(homeZone, true); // marks square as updated
    Dimension dimension = dimensionProvider.mapToLocal(homeZone, true);
    int x = dimension.width;
    int y = dimension.height;

    if (dimensionProvider.isPitchPortrait()) {
      x -= (int) (distanceBounds.getWidth() / 2) + 1;
      y += (int) (distanceBounds.getHeight() / 2) + dimensionProvider.scale(4, RenderContext.ON_PITCH);
    } else {
      x -= (int) (distanceBounds.getWidth() / 2) + dimensionProvider.scale(4, RenderContext.ON_PITCH);
      y += (int) (distanceBounds.getHeight() / 2) - 1;
    }

    g2d.setColor(Color.BLACK);
    g2d.drawString(distanceString, x + 1, y + 1);
    g2d.setColor(heightColor);
    g2d.drawString(distanceString, x, y);


    heightColor = AWAY_COLOR;

    FieldCoordinate lowerLine = new FieldCoordinate(24, pY);
    clear(lowerLine, true); // marks square as updated
    dimension = dimensionProvider.mapToLocal(lowerLine, true);
    x = dimension.width;
    y = dimension.height;

    if (dimensionProvider.isPitchPortrait()) {
      x -= (int) (distanceBounds.getWidth() / 2) + 1;
      y += (int) (distanceBounds.getHeight() / 2) - dimensionProvider.scale(8, RenderContext.ON_PITCH);
    } else {
      x -= (int) (distanceBounds.getWidth() / 2) - dimensionProvider.scale(4, RenderContext.ON_PITCH);
      y += (int) (distanceBounds.getHeight() / 2) - 1;
    }

    g2d.setColor(Color.LIGHT_GRAY);
    g2d.drawString(distanceString, x + 1, y + 1);
    g2d.setColor(heightColor);
    g2d.drawString(distanceString, x, y);
    g2d.dispose();
  }


  private void drawDistanceMarker(int pX) {
    int distance = (pX >= 13) ? (25 - pX) : pX;
    String distanceString = Integer.toString(distance);
    Graphics2D g2d = getImage().createGraphics();
    g2d.setFont(fontCache.font(Font.BOLD, 12, RenderContext.ON_PITCH));
    FontMetrics metrics = g2d.getFontMetrics();
    Rectangle2D distanceBounds = metrics.getStringBounds(distanceString, g2d);
    Color distanceColor;
    Color shadowColor;
    if (pX >= 13) {
      distanceColor = AWAY_COLOR;
      shadowColor = Color.LIGHT_GRAY;
    } else {
      distanceColor = HOME_COLOR;
      shadowColor = Color.BLACK;
    }
    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
    FieldCoordinate upperLine = new FieldCoordinate(pX, 0);
    clear(upperLine, true); // marks square as updated
    Dimension dimension = dimensionProvider.mapToLocal(upperLine, true);
    int x = dimension.width;
    int y = dimension.height;

    if (dimensionProvider.isPitchPortrait()) {
      x -= (int) (distanceBounds.getWidth() / 2) + dimensionProvider.scale(4, RenderContext.ON_PITCH);
      y += (int) (distanceBounds.getHeight() / 2) - 1;
    } else {
      x -= (int) (distanceBounds.getWidth() / 2) + 1;
      y += (int) (distanceBounds.getHeight() / 2) - dimensionProvider.scale(8, RenderContext.ON_PITCH);
    }

    g2d.setColor(shadowColor);
    g2d.drawString(distanceString, x + 1, y + 1);
    g2d.setColor(distanceColor);
    g2d.drawString(distanceString, x, y);
    FieldCoordinate lowerLine = new FieldCoordinate(pX, 14);
    clear(lowerLine, true); // marks square as updated
    dimension = dimensionProvider.mapToLocal(lowerLine, true);
    x = dimension.width;
    y = dimension.height;

    if (dimensionProvider.isPitchPortrait()) {
      x -= (int) (distanceBounds.getWidth() / 2) - dimensionProvider.scale(4, RenderContext.ON_PITCH);
      y += (int) (distanceBounds.getHeight() / 2) - 1;
    } else {
      x -= (int) (distanceBounds.getWidth() / 2) + 1;
      y += (int) (distanceBounds.getHeight() / 2) + dimensionProvider.scale(4, RenderContext.ON_PITCH);
    }

    g2d.setColor(shadowColor);
    g2d.drawString(distanceString, x + 1, y + 1);
    g2d.setColor(distanceColor);
    g2d.drawString(distanceString, x, y);
    g2d.dispose();
  }

  private void drawTeamLogo(Team pTeam, boolean pHomeTeam) {
    if ((pTeam != null) && StringTool.isProvided(pTeam.getLogoUrl())) {
      Dimension fieldDimension = dimensionProvider.dimension(Component.FIELD, RenderContext.UI);
      IconCache iconCache = getClient().getUserInterface().getIconCache();
      BufferedImage teamLogo = iconCache.getIconByUrl(IconCache.findTeamLogoUrl(pTeam), RenderContext.ON_PITCH);
      if (teamLogo != null) {
        Graphics2D g2d = getImage().createGraphics();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        int x, y;
        if (dimensionProvider.isPitchPortrait()) {
          if (pHomeTeam) {
            y = (3 * (fieldDimension.height / 4)) - (teamLogo.getHeight() / 2) - dimensionProvider.imageOffset(RenderContext.ON_PITCH);
          } else {
            y = (fieldDimension.height / 4) - (teamLogo.getHeight() / 2) + dimensionProvider.imageOffset(RenderContext.ON_PITCH);
          }
          x = (fieldDimension.width / 2) - (teamLogo.getWidth() / 2);
          g2d.setClip(0, pHomeTeam ? fieldDimension.height / 2 : dimensionProvider.fieldSquareSize(RenderContext.ON_PITCH),
            fieldDimension.width, (fieldDimension.height / 2) - dimensionProvider.fieldSquareSize(RenderContext.ON_PITCH));
        } else {
          if (pHomeTeam) {
            x = (fieldDimension.width / 4) - (teamLogo.getWidth() / 2) + dimensionProvider.imageOffset(RenderContext.ON_PITCH);
          } else {
            x = (3 * (fieldDimension.width / 4)) - (teamLogo.getWidth() / 2) - dimensionProvider.imageOffset(RenderContext.ON_PITCH);
          }
          y = (fieldDimension.height / 2) - (teamLogo.getHeight() / 2);
          g2d.setClip(pHomeTeam ? dimensionProvider.fieldSquareSize(RenderContext.ON_PITCH) : fieldDimension.width / 2, 0,
            (fieldDimension.width / 2) - dimensionProvider.fieldSquareSize(RenderContext.ON_PITCH), fieldDimension.height);
        }
        g2d.drawImage(teamLogo, x, y, null);
        g2d.dispose();
      }
    }
  }

  public void init() {
    clear(true);
    Game game = getClient().getGame();
    String markingsSetting = getClient().getProperty(CommonProperty.SETTING_PITCH_MARKINGS);
    if (IClientPropertyValue.SETTING_PITCH_MARKINGS_ON.equals(markingsSetting)) {
      drawDistanceMarkers();
    }
    String rowMarkingsSetting = getClient().getProperty(CommonProperty.SETTING_PITCH_MARKINGS_ROW);
    if (IClientPropertyValue.SETTING_PITCH_MARKINGS_ROW_ON.equals(rowMarkingsSetting)) {
      drawRowMarkers();
    }
    String teamLogosSetting = getClient().getProperty(CommonProperty.SETTING_TEAM_LOGOS);
    if (IClientPropertyValue.SETTING_TEAM_LOGOS_BOTH.equals(teamLogosSetting)) {
      drawTeamLogo(game.getTeamHome(), true);
      drawTeamLogo(game.getTeamAway(), false);
    }
    if (IClientPropertyValue.SETTING_TEAM_LOGOS_OWN.equals(teamLogosSetting)) {
      drawTeamLogo(game.getTeamHome(), true);
    }
  }

}
