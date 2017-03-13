package com.balancedbytes.games.ffb.client.ui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.ToolTipManager;

import com.balancedbytes.games.ffb.Weather;
import com.balancedbytes.games.ffb.client.ClientData;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.IIconProperty;
import com.balancedbytes.games.ffb.client.IconCache;
import com.balancedbytes.games.ffb.client.util.UtilClientGraphics;
import com.balancedbytes.games.ffb.model.FieldModel;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.util.StringTool;

/**
 * 
 * @author Kalimar
 */
@SuppressWarnings("serial")
public class ScoreBarComponent extends JPanel implements MouseMotionListener {
  
  public static final int WIDTH = 782;  
  public static final int HEIGHT = 32;
  
//  private static final String _HOME = "Home";
//  private static final String _GUEST = "Guest";
  private static final String _TURN = "Turn";
  
  private static final Font _SCORE_FONT = new Font("Sans Serif", Font.BOLD, 24);
  private static final Font _TURN_NUMBER_FONT = new Font("Sans Serif", Font.BOLD, 22);
  private static final Font _TURN_TEXT_FONT = new Font("Sans Serif", Font.BOLD, 14);

  private static final Font _SPECTATOR_FONT = new Font("Sans Serif", Font.BOLD, 14);
  
  private Rectangle _WEATHER_LOCATION = new Rectangle(WIDTH - 101, 0, 100, 32);
  private Rectangle _SPECTATOR_LOCATION = new Rectangle((WIDTH / 2 + 160), 0, 130, 32);
  private Rectangle _COACH_BANNED_HOME = new Rectangle((WIDTH / 2 - 130 - 36), 0, 36, 32);
  private Rectangle _COACH_BANNED_AWAY = new Rectangle((WIDTH / 2 + 130), 0, 36, 32);
  
  private FantasyFootballClient fClient;
  private BufferedImage fImage;
  
  private int fTurnHome;
  private int fTurnAway;
  private int fHalf;
  private int fScoreHome;
  private int fScoreAway;
  private int fSpectators;
  private Weather fWeather;
  private boolean fCoachBannedHome;
  private boolean fCoachBannedAway;
  private boolean fRefreshNecessary;
  
  public ScoreBarComponent(FantasyFootballClient pClient) {
    fClient = pClient;
    fImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
    setLayout(null);
    Dimension size = new Dimension(WIDTH, HEIGHT);
    setMinimumSize(size);
    setPreferredSize(size);
    setMaximumSize(size);
    ToolTipManager.sharedInstance().registerComponent(this);
    fRefreshNecessary = true;
    addMouseMotionListener(this);
  }
  
  private void drawBackground() {
    Graphics2D g2d = fImage.createGraphics();
    IconCache iconCache = getClient().getUserInterface().getIconCache();
    BufferedImage background = iconCache.getIconByProperty(IIconProperty.SCOREBAR_BACKGROUND); 
    g2d.drawImage(background, 0, 0, null);
    g2d.dispose();
  }

  private void drawScore() {
    Graphics2D g2d = fImage.createGraphics();
    String scoreHome = Integer.toString(fScoreHome);
    String scoreAway = Integer.toString(fScoreAway);
    g2d.setFont(_SCORE_FONT);
    FontMetrics fontMetrics = g2d.getFontMetrics();
    Rectangle2D boundsHome = fontMetrics.getStringBounds(scoreHome, g2d);
    int x = ((WIDTH - (int) boundsHome.getWidth()) / 2) - 40;
    int y = ((HEIGHT + fontMetrics.getHeight()) / 2) - fontMetrics.getDescent() - 1;
    UtilClientGraphics.drawShadowedText(g2d, scoreHome, x, y);
    Rectangle2D boundsAway = fontMetrics.getStringBounds(scoreAway, g2d);
    x = ((WIDTH - (int) boundsAway.getWidth()) / 2) + 40;
    UtilClientGraphics.drawShadowedText(g2d, scoreAway, x, y);
    g2d.dispose();
  }
  
  private void drawTurn() {
    Graphics2D g2d = fImage.createGraphics();
    Game game = getClient().getGame();
    g2d.setFont(_TURN_TEXT_FONT);
    FontMetrics metrics = g2d.getFontMetrics();
    int x = 4;
    int y = ((HEIGHT + metrics.getHeight()) / 2) - metrics.getDescent();
    UtilClientGraphics.drawShadowedText(g2d, _TURN, x, y);
    Rectangle2D bounds = metrics.getStringBounds(_TURN, g2d);
    x += bounds.getWidth() + 10;
    String turn = new StringBuilder().append(game.getTurnDataHome().getTurnNr()).append(" / ").append(game.getTurnDataAway().getTurnNr()).toString();
    g2d.setFont(_TURN_NUMBER_FONT);
    metrics = g2d.getFontMetrics();
    y = ((HEIGHT + metrics.getHeight()) / 2) - metrics.getDescent() - 1;
    UtilClientGraphics.drawShadowedText(g2d, turn, x, y);
    bounds = metrics.getStringBounds(turn, g2d);
    x += bounds.getWidth() + 10;
    String half;
    if (game.getHalf() > 2) {
      half = "of Overtime";
    } else if (game.getHalf() > 1) {
      half = "of 2nd half";
    } else {
      half = "of 1st half";
    }
    g2d.setFont(_TURN_TEXT_FONT);
    metrics = g2d.getFontMetrics();
    y = (HEIGHT + metrics.getHeight()) / 2 - metrics.getDescent();
    UtilClientGraphics.drawShadowedText(g2d, half, x, y);
    g2d.dispose();
  }
  
  private void drawSpectators() {
    if (fSpectators > 0) {
      Graphics2D g2d = fImage.createGraphics();
      IconCache iconCache = getClient().getUserInterface().getIconCache();
      BufferedImage spectatorsImage = iconCache.getIconByProperty(IIconProperty.SCOREBAR_SPECTATORS); 
      g2d.drawImage(spectatorsImage, _SPECTATOR_LOCATION.x, _SPECTATOR_LOCATION.y, null);
      g2d.setFont(_SPECTATOR_FONT);
      String spectatorString = Integer.toString(fSpectators);
      UtilClientGraphics.drawShadowedText(g2d, spectatorString, _SPECTATOR_LOCATION.x + 108, 21);
      g2d.dispose();
    }
  }

  private void drawBannedCoaches() {
    if (fCoachBannedHome || fCoachBannedAway) {
      Graphics2D g2d = fImage.createGraphics();
      IconCache iconCache = getClient().getUserInterface().getIconCache();
      if (fCoachBannedHome) {
        BufferedImage coachBannedImage = iconCache.getIconByProperty(IIconProperty.SCOREBAR_COACH_BANNED_HOME); 
        g2d.drawImage(coachBannedImage, _COACH_BANNED_HOME.x, _COACH_BANNED_HOME.y, null);
      }
      if (fCoachBannedAway) {
        BufferedImage coachBannedImage = iconCache.getIconByProperty(IIconProperty.SCOREBAR_COACH_BANNED_AWAY); 
        g2d.drawImage(coachBannedImage, _COACH_BANNED_AWAY.x, _COACH_BANNED_AWAY.y, null);
      }
      g2d.dispose();
    }
  }

  private void drawWeather() {
    if (fWeather != null) {
      String weatherIconProperty = null;
      switch (fWeather) {
        case BLIZZARD:
          weatherIconProperty = IIconProperty.WEATHER_BLIZZARD;
          break;
        case INTRO:
          weatherIconProperty = IIconProperty.WEATHER_INTRO;
          break;
        case NICE:
          weatherIconProperty = IIconProperty.WEATHER_NICE;
          break;
        case POURING_RAIN:
          weatherIconProperty = IIconProperty.WEATHER_RAIN;
          break;
        case SWELTERING_HEAT:
          weatherIconProperty = IIconProperty.WEATHER_HEAT;
          break;
        case VERY_SUNNY:
          weatherIconProperty = IIconProperty.WEATHER_SUNNY;
          break;
      }
      if (StringTool.isProvided(weatherIconProperty)) {
        IconCache iconCache = getClient().getUserInterface().getIconCache();
        BufferedImage weatherIcon = iconCache.getIconByProperty(weatherIconProperty);
        Graphics2D g2d = fImage.createGraphics();
        g2d.drawImage(weatherIcon, _WEATHER_LOCATION.x, _WEATHER_LOCATION.y, null);
        g2d.dispose();
      }
    }
  }
  
  public void init() {
  	fTurnHome = 0;
  	fTurnAway = 0;
  	fScoreHome = 0;
  	fScoreAway = 0;
  	fSpectators = 0;
  	fWeather = null;
  	fRefreshNecessary = true;
  	refresh();
  }
  

  public void refresh() {
    Game game = getClient().getGame();
    if (game.getHalf() > 0) {
      ClientData clientData = getClient().getClientData();
      if (!fRefreshNecessary) {
        fRefreshNecessary = ((fTurnHome != game.getTurnDataHome().getTurnNr()) || (fTurnAway != game.getTurnDataAway().getTurnNr()) || (fHalf != game.getHalf()));
      }
      if (!fRefreshNecessary) {
        fRefreshNecessary = ((fScoreHome != game.getGameResult().getTeamResultHome().getScore()) || (fTurnAway != game.getGameResult().getTeamResultAway().getScore()));
      }
      if (!fRefreshNecessary) {
        fRefreshNecessary = (fSpectators != clientData.getSpectators());
      }
      if (!fRefreshNecessary) {
        fRefreshNecessary = (fWeather != game.getFieldModel().getWeather());
      }
      if (!fRefreshNecessary) {
        fRefreshNecessary = (fCoachBannedHome != game.getTurnDataHome().isCoachBanned()) || (fCoachBannedAway != game.getTurnDataAway().isCoachBanned());
      }
      if (fRefreshNecessary) {
        fTurnHome = game.getTurnDataHome().getTurnNr();
        fTurnAway = game.getTurnDataAway().getTurnNr();
        fHalf = game.getHalf();
        fScoreHome = game.getGameResult().getTeamResultHome().getScore();
        fScoreAway = game.getGameResult().getTeamResultAway().getScore();
        fSpectators = clientData.getSpectators();
        fWeather = game.getFieldModel().getWeather();
        fCoachBannedHome = game.getTurnDataHome().isCoachBanned();
        fCoachBannedAway = game.getTurnDataAway().isCoachBanned();
        drawBackground();
        drawTurn();
        drawScore();
        drawSpectators();
        drawWeather();
        drawBannedCoaches();
        repaint();
        fRefreshNecessary = false;
      }
    } else {
      drawBackground();
      repaint();
    }
  }

  protected void paintComponent(Graphics pGraphics) {
    pGraphics.drawImage(fImage, 0, 0, null);
  }

  public FantasyFootballClient getClient() {
    return fClient;
  }
  
  public String getToolTipText(MouseEvent pMouseEvent) {
    String toolTip = null;
    FieldModel fieldModel = getClient().getGame().getFieldModel();
    if ((fWeather != null) && _WEATHER_LOCATION.contains(pMouseEvent.getPoint())) {
      StringBuilder weatherInfo = new StringBuilder();
      weatherInfo.append("<html><b>").append(fieldModel.getWeather().getName()).append("</b><br>").append(fieldModel.getWeather().getDescription()).append("</html>");
      toolTip = weatherInfo.toString();
    }
    if ((fSpectators > 0) && _SPECTATOR_LOCATION.contains(pMouseEvent.getPoint())) {
      StringBuilder spectatorInfo = new StringBuilder();
      spectatorInfo.append("<html>").append(fSpectators);
      spectatorInfo.append((fSpectators == 1) ? " spectator is watching the game." : " spectators are watching the game.");
      spectatorInfo.append("</html>");
      toolTip = spectatorInfo.toString();
    }
    return toolTip;
  }
  
  public void mouseMoved(MouseEvent pMouseEvent) {
    getClient().getUserInterface().getMouseEntropySource().reportMousePosition(pMouseEvent);
  }
  
  public void mouseDragged(MouseEvent pMouseEvent) {
    getClient().getUserInterface().getMouseEntropySource().reportMousePosition(pMouseEvent);
  }
    
}
