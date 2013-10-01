package com.balancedbytes.games.ffb.client.layer;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import com.balancedbytes.games.ffb.FieldModelChangeEvent;
import com.balancedbytes.games.ffb.Weather;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.IconCache;
import com.balancedbytes.games.ffb.model.FieldModel;
import com.balancedbytes.games.ffb.model.Game;


/**
 * 
 * @author Kalimar
 */
public class FieldLayerField extends FieldLayer {
  
  public FieldLayerField(FantasyFootballClient pClient) {
    super(pClient);
  }
  
  public void drawWeather(Weather pWeather) {
    if (pWeather != null) {
      IconCache iconCache = getClient().getUserInterface().getIconCache();
      BufferedImage fieldImage = iconCache.getIcon(pWeather);
      draw(fieldImage, 0, 0, 1.0f);
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
      FontMetrics metrics = g2d.getFontMetrics ();
      Rectangle2D teamNameBounds = metrics.getStringBounds(teamNameHome, g2d);
      int translateX = (int) ((0.5 * FIELD_SQUARE_SIZE) + (teamNameBounds.getHeight() / 2)) - 4;
      int translateY = (int) ((getImage().getHeight() / 2) + (teamNameBounds.getWidth() / 2));
      g2d.translate(translateX, translateY);
      g2d.rotate(-Math.PI/2.0);
      g2d.drawString(teamNameHome, 0, 0);
      g2d.dispose();

      g2d = getGraphicsWithFontAndColor();
      metrics = g2d.getFontMetrics ();
      teamNameBounds = metrics.getStringBounds(teamNameAway, g2d);
      translateX = (int) ((25.0 * FIELD_SQUARE_SIZE) + (teamNameBounds.getHeight() / 2)) - 3;
      translateY = (int) ((getImage().getHeight() / 2) - (teamNameBounds.getWidth() / 2));
      g2d.translate(translateX, translateY);
      g2d.rotate(Math.PI/2.0);
      g2d.drawString(teamNameAway, 0, 0);
      g2d.dispose();
    
    }
    
  }

  private Graphics2D getGraphicsWithFontAndColor() {
    Graphics2D g2d = getImage().createGraphics();
    g2d.setColor(Color.WHITE);
    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
    g2d.setFont(new Font("Sans Serif", Font.BOLD, 20));
    return g2d;
  }
  
  public void fieldModelChanged(FieldModelChangeEvent pChangeEvent) {
    switch (pChangeEvent.getType()) {
      case FieldModelChangeEvent.TYPE_WEATHER:
        Weather weather = (Weather) pChangeEvent.getNewValue();
        drawWeather(weather);
        break;
    }
  }
    
  public void init() {
    clear(true);
    FieldModel fieldModel = getClient().getGame().getFieldModel();
    if (fieldModel != null) {
      drawWeather(fieldModel.getWeather());
      fieldModel.addListener(this);
    }
  }
  
}
