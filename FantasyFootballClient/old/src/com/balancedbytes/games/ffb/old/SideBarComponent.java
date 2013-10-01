package com.balancedbytes.games.ffb.old;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.TurnMode;
import com.balancedbytes.games.ffb.client.ActionKey;
import com.balancedbytes.games.ffb.client.ActionKeyGroup;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.FieldLayer;
import com.balancedbytes.games.ffb.client.IIconProperty;
import com.balancedbytes.games.ffb.client.IconCache;
import com.balancedbytes.games.ffb.client.UserInterface;
import com.balancedbytes.games.ffb.client.util.UtilReflection;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.GameResult;
import com.balancedbytes.games.ffb.model.TeamResult;
import com.balancedbytes.games.ffb.model.TurnData;
import com.balancedbytes.games.ffb.util.ArrayTool;

/**
 * 
 * @author Kalimar
 */
@SuppressWarnings("serial")
public class SideBarComponent extends JPanel implements ActionListener, FocusListener {
  
  public static final int WIDTH = 55;
  public static final int HEIGHT = FieldLayer.FIELD_IMAGE_HEIGHT + StatusBar.HEIGHT + PlayerDetailComponent.HEIGHT + 2;

  private static final String _LABEL_END_TURN = "<html><center>End<br>Turn</center></html>";
  private static final String _LABEL_END_SETUP = "<html><center>End<br>Setup</center></html>";
  private static final String _LABEL_KICKOFF = "<html><center>Kick<br>Off</center></html>";
  
  private static final String _1ST_HALF = "1st Half";
  private static final String _2ND_HALF = "2nd Half";
  private static final String _OVERTIME = "Overtime";
  
  private FantasyFootballClient fClient;
  
  private BufferedImage fImage;
  
  private boolean fHomeSideBar;
  private boolean fWaiting;
  private boolean fRefreshNecessary;
  
  private JButton fEndTurnButton;
  
  private int[] fBlockRoll;
  private int fNrOfDice;
  private int fDiceIndex;
  
  private int fScore;
  private int fHalf;
  private int fTurnNr;
  private int fReRolls;
  private boolean fReRollUsed;
  private int fApothecaries;
  private boolean fGetTheRef;
  private boolean fHomePlaying;
  private TurnMode fTurnMode;
  
  public SideBarComponent(FantasyFootballClient pClient, boolean pHomeSideBar) {
    
    fClient = pClient;
    fHomeSideBar = pHomeSideBar;
    fImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
    
    fEndTurnButton = new JButton();
    fEndTurnButton.setBounds(0, HEIGHT - 37, 54, 36);
    fEndTurnButton.setFocusPainted(false);
    fEndTurnButton.setMargin(new Insets(5, 5, 5, 5));
    fEndTurnButton.setVisible(false);
    fEndTurnButton.addActionListener(this);
    fEndTurnButton.addFocusListener(this);
    getClient().getActionKeyBindings().addKeyBindings(fEndTurnButton, ActionKeyGroup.ALL);

    setLayout(null);
    add(fEndTurnButton);
    
    Dimension dimension = new Dimension(WIDTH, HEIGHT);
    setMinimumSize(dimension);
    setPreferredSize(dimension);
    setMaximumSize(dimension);
    
    fRefreshNecessary = true;
    
  }
  
  public void refresh() {

    Game game = getClient().getGame();
    GameResult gameResult = game.getGameResult();

    if (fHalf != game.getHalf()) {
      fHalf = game.getHalf();
      fRefreshNecessary = true;
    }
    if (fGetTheRef != game.isGetTheRef()) {
      fGetTheRef = game.isGetTheRef();
      fRefreshNecessary = true;
    }
    if (fHomePlaying != game.isHomePlaying()) {
      fHomePlaying = game.isHomePlaying();
      fRefreshNecessary = true;
    }
    if (fTurnMode != game.getTurnMode()) {
      fTurnMode = game.getTurnMode();
      fRefreshNecessary = true;
    }
    TeamResult teamResult = isHomeSideBar() ? gameResult.getTeamResultHome() : gameResult.getTeamResultAway();
    if (fScore != teamResult.getScore()) {
      fScore = teamResult.getScore();
      fRefreshNecessary = true;
    }
    TurnData turnData = isHomeSideBar() ? game.getTurnDataHome() : game.getTurnDataAway();
    if (fTurnNr != turnData.getTurnNr()) {
      fTurnNr = turnData.getTurnNr();
      fRefreshNecessary = true;
    }
    if (fReRolls != turnData.getReRolls()) {
      fReRolls = turnData.getReRolls();
      fRefreshNecessary = true;
    }
    if (fReRollUsed != turnData.isReRollUsed()) {
      fReRollUsed = turnData.isReRollUsed();
      fRefreshNecessary = true;
    }
    if (fApothecaries != turnData.getApothecaries()) {
      fApothecaries = turnData.getApothecaries();
      fRefreshNecessary = true;
    }
    
    if (fRefreshNecessary) {
    
      Graphics2D g2d = getImage().createGraphics();
      IconCache iconCache = getUserInterface().getIconCache();
      
      // Fill with a gradient.
      if (isHomeSideBar()) {
        g2d.setPaint(new GradientPaint(0, 0, new Color(128, 0, 0), 0, HEIGHT - 1, Color.WHITE, false));
      } else {
        g2d.setPaint(new GradientPaint(0, 0, new Color(12, 20, 136), 0, HEIGHT - 1, Color.WHITE, false));
      }
      g2d.fillRect(0, 0, WIDTH, HEIGHT);
      
      drawHeader(g2d, 20, "Score");
      drawNumber(g2d, 45, fScore);
      
      if ((fHalf > 2)) {
        drawHeader(g2d, 65, _OVERTIME);
      } else if ((fHalf == 2)) {
        drawHeader(g2d, 65, _2ND_HALF);
      } else {
        drawHeader(g2d, 65, _1ST_HALF);
      }
      
      drawHeader(g2d, 80, "Turn");
      drawNumber(g2d, 105, fTurnNr);
      
      drawHeader(g2d, 125, "Re-Rolls");
      drawNumber(g2d, 150, fReRolls);
  
      if (fReRollUsed) {
        BufferedImage checkIcon = iconCache.getIconByProperty(IIconProperty.GAME_CHECK);
        g2d.drawImage(checkIcon, 36, 127, null);
      }
      
      drawApothecary(g2d, 170, fApothecaries);
  
      if (fGetTheRef) {
        BufferedImage getTheRef = iconCache.getIconByProperty(IIconProperty.GAME_GET_THE_REF);
        g2d.drawImage(getTheRef, 3, 225, null);
      }
      
      if (ArrayTool.isProvided(fBlockRoll)) {
        Composite oldComposite = g2d.getComposite();
        for (int i = 0; i < fBlockRoll.length; i++) {
          g2d.setComposite(oldComposite);
          String diceBackgroundProperty = (((isHomeSideBar() && (fNrOfDice > 0)) || (!isHomeSideBar() && (fNrOfDice < 0))) ? IIconProperty.GAME_BLOCKDICE_BACKGROUND_RED : IIconProperty.GAME_BLOCKDICE_BACKGROUND_BLUE);
          BufferedImage diceBackground = iconCache.getIconByProperty(diceBackgroundProperty);
          BufferedImage diceForeground = iconCache.getDiceIcon(fBlockRoll[i]);
          if ((fDiceIndex >= 0) && (fDiceIndex != i)) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
          }
          g2d.drawImage(diceBackground, 6, HEIGHT - 87 - (i * 48), null);
          g2d.drawImage(diceForeground, 9, HEIGHT - 85 - (i * 48), null);
        }
        g2d.setComposite(oldComposite);
      }
  
      hideEndTurnButton();
      
      if (fWaiting) {
        BufferedImage hourglass = getUserInterface().getIconCache().getIconByProperty(IIconProperty.GAME_HOURGLASS);
        g2d.drawImage(hourglass, 11, HEIGHT - 33, null);
      } else {
        if (isHomeSideBar() && fHomePlaying && (game.getFinished() == null)) {
          if (ClientMode.PLAYER == getClient().getLoginMode()) {
            showEndTurnButton();
          } else {
            drawPlayingDice(g2d);
          }
        }
        if (!isHomeSideBar() && !fHomePlaying && (game.getTurnMode() != null) && (game.getTurnMode() != TurnMode.START_GAME) && (game.getFinished() == null)) {
          drawPlayingDice(g2d);
        }
      }
      
      g2d.dispose();
      repaint();
      
      fRefreshNecessary = false;
      
    }
    
  }
  
  private void drawPlayingDice(Graphics2D pGraphics) {
    BufferedImage dice = getUserInterface().getIconCache().getIconByProperty(IIconProperty.GAME_DICE);
    pGraphics.drawImage(dice, 0, HEIGHT - 38, null);
  }
  
  private void drawNumber(Graphics2D pGraphics, int pYPosistion, int pNumber) {
    String numberString = Integer.toString(pNumber);
    pGraphics.setFont(new Font("Sans Serif", Font.BOLD, 26));
    FontMetrics metrics = pGraphics.getFontMetrics();
    Rectangle2D numberBounds = metrics.getStringBounds(numberString, pGraphics);
    int x = (int) (WIDTH - numberBounds.getWidth()) / 2;
    pGraphics.setColor(Color.BLACK);
    pGraphics.drawString(numberString, x + 1, pYPosistion + 1);
    setNumberColor(pGraphics);
    pGraphics.drawString(numberString, x, pYPosistion);
  }

  private void setNumberColor(Graphics pGraphics) {
    if (isHomeSideBar()) {
      pGraphics.setColor(new Color(255, 140, 120));
    } else {
      pGraphics.setColor(new Color(170, 255, 224));
    }
  }
  
  private void drawHeader(Graphics2D pGraphics, int pYPosition, String pText) {
    pGraphics.setFont(new Font("Sans Serif", Font.PLAIN, 12));
    FontMetrics metrics = pGraphics.getFontMetrics();
    Rectangle2D textBounds = metrics.getStringBounds(pText, pGraphics);
    int x = (int) (WIDTH - textBounds.getWidth()) / 2;
    pGraphics.setColor(Color.BLACK);
    pGraphics.drawString(pText, x + 1, pYPosition + 1);
    pGraphics.setColor(Color.WHITE);
    pGraphics.drawString(pText, x, pYPosition);
  }
  
  private void drawApothecary(Graphics2D pGraphics, int pYPosition, int pNumber) {
    if (pNumber > 0) {
      BufferedImage apothecary = getUserInterface().getIconCache().getIconByProperty(IIconProperty.GAME_APOTHECARY);
      pGraphics.drawImage(apothecary, 3, pYPosition, null);
      if (pNumber > 1) {
        String numberString = Integer.toString(pNumber);
        pGraphics.setFont(new Font("Sans Serif", Font.BOLD, 16));
        FontMetrics metrics = pGraphics.getFontMetrics();
        Rectangle2D numberBounds = metrics.getStringBounds(numberString, pGraphics);
        int x = (int) (WIDTH - numberBounds.getWidth() - 6);
        pGraphics.setColor(Color.BLACK);
        pGraphics.drawString(numberString, x + 1, pYPosition + 6);
        setNumberColor(pGraphics);
        pGraphics.drawString(numberString, x, pYPosition + 5);
      }
    }
  }

  public void showEndTurnButton() {
    String buttonText = null;
    if (fTurnMode != null) {
      switch (fTurnMode) {
        case START_GAME:
          buttonText = null;
          break;
        case SETUP:
        case PERFECT_DEFENSE:
        case QUICK_SNAP:
        case HIGH_KICK:
          buttonText = _LABEL_END_SETUP;
          break;
        case KICKOFF:
          buttonText = _LABEL_KICKOFF;
          break;
        default: 
          buttonText = _LABEL_END_TURN;
          break;
      }
    }
    if (buttonText != null) {
      fEndTurnButton.setText(buttonText);
      int fixAlign = getAlignFix(fEndTurnButton, buttonText);
      fEndTurnButton.setMargin(new Insets(5, fixAlign, 5, 5));
      fEndTurnButton.setVisible(true);
    }
  }

	private int getAlignFix(JButton button, String buttonText) {
		int fixAlign = 5;
		if (UtilReflection.getOS() == UtilReflection.OS.OSX) {
			String[] parts = buttonText.split("<br[^>]*>");
			int maxWidth = 0;
			for (String part : parts) {
			  String t = part.replaceAll("<[^>]+>", "");
			  maxWidth = Math.max(maxWidth, button.getGraphics().getFontMetrics().stringWidth(t));
			}
	    fixAlign = 7-(int) (maxWidth / 2);
		}
		return fixAlign;
	}
  
  public void hideEndTurnButton() {
    fEndTurnButton.setVisible(false);
  }
  
  protected void paintComponent(Graphics pGraphics) {
    pGraphics.drawImage(fImage, 0, 0, null);
    // fEndTurnButton.repaint();
  }
    
  public FantasyFootballClient getClient() {
    return fClient; 
  }
  
  protected UserInterface getUserInterface() {
    return getClient().getUserInterface();
  }  
  
  public BufferedImage getImage() {
    return fImage;
  }
  
  public void actionPerformed(ActionEvent pActionEvent) {
    if (isHomeSideBar()) {
      if (pActionEvent.getSource() == fEndTurnButton) {
        if (getClient().getClientState().actionKeyPressed(ActionKey.TOOLBAR_TURN_END)) {
          hideEndTurnButton();
        }
      }
    }
//    JComponent lastFocusedComponent = getClient().getChatLogManager().getLastFocusedComponent();
//    if (lastFocusedComponent != null) {
//      lastFocusedComponent.requestFocus();
//    }
  }
  
  public void setBlockDiceResult(int pNrOfDice, int[] pBlockRoll, int pDiceIndex) {
    if (fDiceIndex != pDiceIndex) {
      fRefreshNecessary = true;
    }
    fDiceIndex = pDiceIndex;
    if (fNrOfDice != pNrOfDice) {
      fRefreshNecessary = true;
    }
    fNrOfDice = pNrOfDice;
    if (ArrayTool.isProvided(pBlockRoll)) {
      if (ArrayTool.isProvided(fBlockRoll)) {
        if (fBlockRoll.length == pBlockRoll.length) {
          for (int i = 0; i < fBlockRoll.length; i++) {
            if (fBlockRoll[i] != pBlockRoll[i]) {
              fRefreshNecessary = true;
            }
          }
        } else {
          fRefreshNecessary = true;
        }
      } else {
        fRefreshNecessary = true;
      }
    } else {
      if (ArrayTool.isProvided(fBlockRoll)) {
        fRefreshNecessary = true;
      }
    }
    fBlockRoll = pBlockRoll;
  }
    
  public void clearBlockDiceResult() {
    setBlockDiceResult(0, null, -1);
  }
  
  public boolean isHomeSideBar() {
    return fHomeSideBar;
  }
  
  public void setWaiting(boolean pWaiting) {
    if (pWaiting != fWaiting) {
      fRefreshNecessary = true;
    }
    fWaiting = pWaiting;
  }
  
  public boolean isWaiting() {
    return fWaiting;
  }
  
  public void focusGained(FocusEvent pE) {
//    getClient().getUserInterface().getStatusBar().setChatMode(false);
    getClient().getUserInterface().getScoreBar().refresh();
  }
  
  public void focusLost(FocusEvent pE) {
  }
    
}
