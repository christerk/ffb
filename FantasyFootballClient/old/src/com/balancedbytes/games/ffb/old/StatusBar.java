package com.balancedbytes.games.ffb.old;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.Weather;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.util.StringTool;



/**
 * 
 * @author Kalimar
 */
@SuppressWarnings("serial")
public class StatusBar extends JPanel {
  
  private static final Color _COLOR_SWELTERING_HEAT = new Color(175, 142, 3);
  private static final Color _COLOR_VERY_SUNNY = new Color(180, 207, 68);
  private static final Color _COLOR_NICE = new Color(106, 178, 16);
  private static final Color _COLOR_POURING_RAIN = new Color(93, 118, 60);
  private static final Color _COLOR_BLIZZARD = new Color(40, 89, 129);
  private static final Color _COLOR_INTRO = _COLOR_NICE;

  public static final int WIDTH = 902;  
  public static final int HEIGHT = 18;
  
  private FantasyFootballClient fClient;
  private BufferedImage fImage;
  private String fStatus;
  private boolean fChatMode;
  
  protected StatusBar(FantasyFootballClient pClient) {
    fClient = pClient;
    fImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
    setLayout(null);
    Dimension size = new Dimension(WIDTH, HEIGHT);
    setMinimumSize(size);
    setPreferredSize(size);
    setMaximumSize(size);
    refresh();
  }
  
  public BufferedImage getImage() {
    return fImage;
  }
  
  public void setStatus(String pStatus) {
    fStatus = pStatus;
  }
  
  public String getStatus() {
    return fStatus;
  }
  
  public void refresh(String pStatus) {
    setStatus(pStatus);
    refresh();
  }
  
  public void refresh() {

    Graphics2D g2d = getImage().createGraphics();
    
    Color gradientColor = findGradientColor();
    g2d.setPaint(gradientColor);
    g2d.fillRect(0, 0, 60, HEIGHT);
    g2d.setPaint(new GradientPaint(60, 0, gradientColor, WIDTH - 1, 0, Color.WHITE, false));
    g2d.fillRect(60, 0, WIDTH, HEIGHT);
    g2d.setColor(Color.BLACK);
    
    if (StringTool.isProvided(getStatus())) {
      g2d.setFont(new Font("Sans Serif", Font.BOLD, 14));
      g2d.drawString(getStatus(), 4, HEIGHT - 4);
    }
    
    String mode;
    if (isChatMode()) {
      mode = "CHAT";
    } else {
      if (getClient().getReplayer().isReplaying()) {
        mode = "REPLAY";
      } else {
        if (getClient().getLoginMode() == ClientMode.PLAYER) {
          mode = "PLAY";
        } else {
          mode = "SPECTATE";
        }
      }
    }

    g2d.setFont(new Font("Sans Serif", Font.BOLD, 11));
    FontMetrics metrics = g2d.getFontMetrics ();
    Rectangle2D modeBounds = metrics.getStringBounds(mode, g2d);
    g2d.drawString(mode, (int) (WIDTH - modeBounds.getWidth() - 3), HEIGHT - 5);
    
    g2d.dispose();
    repaint();
    
  }
  
  protected void paintComponent(Graphics pGraphics) {
    pGraphics.drawImage(getImage(), 0, 0, null);
  }
  
  public FantasyFootballClient getClient() {
    return fClient;
  }
  
  public void setChatMode(boolean pChatMode) {
    fChatMode = pChatMode;
  }
  
  public boolean isChatMode() {
    return fChatMode;
  }
  
  private Color findGradientColor() {
    Weather weather = getClient().getGame().getFieldModel().getWeather();
    if (weather != null) {
      switch (weather) {
        case SWELTERING_HEAT:
          return _COLOR_SWELTERING_HEAT;
        case VERY_SUNNY:
          return _COLOR_VERY_SUNNY;
        case NICE:
          return _COLOR_NICE;
        case POURING_RAIN:
          return _COLOR_POURING_RAIN;
        case BLIZZARD:
          return _COLOR_BLIZZARD;
        default:
          return _COLOR_INTRO;
      }
    } else {
      return _COLOR_INTRO;
    }
  }
 
}
