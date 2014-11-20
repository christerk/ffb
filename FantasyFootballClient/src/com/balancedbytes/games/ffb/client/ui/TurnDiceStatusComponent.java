package com.balancedbytes.games.ffb.client.ui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.AttributedString;
import java.util.Date;

import javax.swing.JPanel;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.StatusType;
import com.balancedbytes.games.ffb.TurnMode;
import com.balancedbytes.games.ffb.client.ActionKey;
import com.balancedbytes.games.ffb.client.ClientData;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.IIconProperty;
import com.balancedbytes.games.ffb.client.IconCache;
import com.balancedbytes.games.ffb.client.UserInterface;
import com.balancedbytes.games.ffb.client.dialog.DialogEndTurn;
import com.balancedbytes.games.ffb.client.dialog.IDialog;
import com.balancedbytes.games.ffb.client.dialog.IDialogCloseListener;
import com.balancedbytes.games.ffb.client.util.UtilClientGraphics;
import com.balancedbytes.games.ffb.dialog.DialogId;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.DateTool;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.util.UtilPlayer;

/**
 * 
 * @author Kalimar
 */
@SuppressWarnings("serial")
public class TurnDiceStatusComponent extends JPanel implements MouseListener, MouseMotionListener, IDialogCloseListener {

  public static final int WIDTH = 116;
  public static final int HEIGHT = 92;

  private static final String _LABEL_END_TURN = "End Turn";
  private static final String _LABEL_END_SETUP = "End Setup";
  private static final String _LABEL_CONTINUE = "Continue";
  private static final String _LABEL_KICKOFF = "Kick Off";
  private static final String _LABEL_TIMEOUT = "Timeout";

  private static final Font _BUTTON_FONT = new Font("Sans Serif", Font.BOLD, 14);
  private static final Rectangle _BUTTON_AREA = new Rectangle(1, 1, 114, 31);

  private static final Font _DICE_FONT = new Font("Sans Serif", Font.BOLD, 11);
  private static final Font _STATUS_TITLE_FONT = new Font("Sans Serif", Font.BOLD, 12);
  private static final Font _STATUS_MESSAGE_FONT = new Font("Sans Serif", Font.PLAIN, 12);
  private static final int _STATUS_TEXT_WIDTH = WIDTH - 10;

  private SideBarComponent fSideBar;
  private BufferedImage fImage;

  private boolean fEndTurnButtonShown;
  private boolean fTimeoutButtonShown;
  private boolean fButtonSelected;

  private TurnMode fTurnMode;
  private boolean fHomePlaying;
  private boolean fTimeoutPossible;
  private boolean fTimeoutEnforced;

  private String fStatusTitle;
  private String fStatusMessage;
  private StatusType fStatusType;
  
  private boolean fWaitingForOpponent;
  private boolean fEndTurnButtonHidden;
  private Date fFinished;
  
  private int fNrOfBlockDice;
  private int[] fBlockRoll;
  private int fBlockDiceIndex;
  
  private boolean fRefreshNecessary;
  
  public TurnDiceStatusComponent(SideBarComponent pSideBar) {
    fSideBar = pSideBar;
    fImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
    setLayout(null);
    Dimension size = new Dimension(WIDTH, HEIGHT);
    setMinimumSize(size);
    setPreferredSize(size);
    setMaximumSize(size);
    addMouseListener(this);
    addMouseMotionListener(this);
    fRefreshNecessary = true;
  }

  public SideBarComponent getSideBar() {
    return fSideBar;
  }

  private void drawBackground() {
    Graphics2D g2d = fImage.createGraphics();
    IconCache iconCache = getSideBar().getClient().getUserInterface().getIconCache();
    BufferedImage background;
    if (getSideBar().isHomeSide()) {
      background = iconCache.getIconByProperty(IIconProperty.SIDEBAR_BACKGROUND_TURN_DICE_STATUS_RED); 
    } else {
      background = iconCache.getIconByProperty(IIconProperty.SIDEBAR_BACKGROUND_TURN_DICE_STATUS_BLUE); 
    }
    g2d.drawImage(background, 0, 0, null);
    g2d.dispose();
  }

  private void drawEndTurnButton() {
    fEndTurnButtonShown = false;
    ClientData clientData = getSideBar().getClient().getClientData();
    if ((fFinished == null) && (fTurnMode != null) && fHomePlaying && !fWaitingForOpponent && !clientData.isEndTurnButtonHidden()) {
      switch (fTurnMode) {
        case START_GAME:
          break;
        case SETUP:
        case PERFECT_DEFENCE:
        case QUICK_SNAP:
        case HIGH_KICK:
          drawButton(_LABEL_END_SETUP);
          fEndTurnButtonShown = true;
          break;
        case KICKOFF_RETURN:
        case PASS_BLOCK:
        case ILLEGAL_SUBSTITUTION:
          drawButton(_LABEL_CONTINUE);
          fEndTurnButtonShown = true;
          break;
        case KICKOFF:
          drawButton(_LABEL_KICKOFF);
          fEndTurnButtonShown = true;
          break;
        default:
          drawButton(_LABEL_END_TURN);
          fEndTurnButtonShown = true;
          break;
      }
    }
  }
  
  private void drawTimeoutButton() {
    fTimeoutButtonShown = false;
    if ((fFinished == null) && (fTurnMode != null) && !fHomePlaying && fTimeoutPossible && !fTimeoutEnforced) {
      drawButton(_LABEL_TIMEOUT);
      fTimeoutButtonShown = true;
    }
  }

  private void drawButton(String pButtonText) {
    if (pButtonText != null) {
      Graphics2D g2d = fImage.createGraphics();
      IconCache iconCache = getSideBar().getClient().getUserInterface().getIconCache();
      BufferedImage buttonImage = iconCache.getIconByProperty(fButtonSelected ? IIconProperty.SIDEBAR_TURN_BUTTON_SELECTED : IIconProperty.SIDEBAR_TURN_BUTTON);
      g2d.drawImage(buttonImage, _BUTTON_AREA.x, _BUTTON_AREA.y, null);
      g2d.setFont(_BUTTON_FONT);
      g2d.setColor(Color.BLACK);
      FontMetrics metrics = g2d.getFontMetrics();
      Rectangle2D bounds = metrics.getStringBounds(pButtonText, g2d);
      int x = ((WIDTH - (int) bounds.getWidth()) / 2);
      int y = ((_BUTTON_AREA.height + metrics.getHeight()) / 2) - metrics.getDescent();
      g2d.drawString(pButtonText, x, y);
      g2d.dispose();
    }
  }
  
  private void drawPlaying() {
    if ((fTurnMode != null) && (fTurnMode != TurnMode.START_GAME) && (fFinished == null)) {
      Graphics2D g2d = fImage.createGraphics();
      IconCache iconCache = getSideBar().getClient().getUserInterface().getIconCache();
      BufferedImage playingImage = iconCache.getIconByProperty(IIconProperty.SIDEBAR_STATUS_PLAYING);
      g2d.drawImage(playingImage, _BUTTON_AREA.x, _BUTTON_AREA.y, null);
      g2d.dispose();
    }
  }

  private void drawStatus() {
    if (StringTool.isProvided(fStatusTitle) && StringTool.isProvided(fStatusMessage)) {
      Graphics2D g2d = fImage.createGraphics();
      IconCache iconCache = getSideBar().getClient().getUserInterface().getIconCache();
      String imageProperty = null;
      switch (fStatusType) {
        case WAITING:
          imageProperty = IIconProperty.SIDEBAR_STATUS_WAITING;
          break;
        case REF:
          imageProperty = IIconProperty.SIDEBAR_STATUS_REF;
          break;
      }
      if (imageProperty != null) {
        BufferedImage statusImage = iconCache.getIconByProperty(imageProperty);
        g2d.drawImage(statusImage, 1, 1, null);
      }
      g2d.setColor(Color.BLACK);
      g2d.setFont(_STATUS_TITLE_FONT);
      FontMetrics fontMetrics = g2d.getFontMetrics();
      int x = 4;
      int y = fontMetrics.getHeight();
      g2d.drawString(fStatusTitle, x, y);
      g2d.setFont(_STATUS_MESSAGE_FONT);
      fontMetrics = g2d.getFontMetrics();
      y += fontMetrics.getHeight();
      final AttributedString attStr = new AttributedString(fStatusMessage);
      attStr.addAttribute(TextAttribute.FONT, g2d.getFont());
      final LineBreakMeasurer measurer = new LineBreakMeasurer(attStr.getIterator(), new FontRenderContext(null, false, false));
      TextLayout layoutLine = measurer.nextLayout(_STATUS_TEXT_WIDTH);
      while (layoutLine != null) {
        layoutLine.draw(g2d, x, y);
        y += fontMetrics.getHeight();
        if (y <= 3 * fontMetrics.getHeight()) {
          layoutLine = measurer.nextLayout(_STATUS_TEXT_WIDTH);
        } else {
          layoutLine = measurer.nextLayout(_STATUS_TEXT_WIDTH - 20); // hourglass icon
        }
      }
      g2d.dispose();
    }
  }
  
  private void drawBlockDice() {
    if (ArrayTool.isProvided(fBlockRoll)) {
      int x = 0, y = 38;
      Graphics2D g2d = fImage.createGraphics();
      Composite oldComposite = g2d.getComposite();
      IconCache iconCache = getSideBar().getClient().getUserInterface().getIconCache();
      for (int i = 0; i < fBlockRoll.length; i++) {
        g2d.setComposite(oldComposite);
        BufferedImage diceIcon = iconCache.getDiceIcon(fBlockRoll[i]);
        if ((fBlockDiceIndex >= 0) && (fBlockDiceIndex != i)) {
          g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        }
        if (fBlockRoll.length > 2) {
          x = 1 + (39 * i);
        } else if (fBlockRoll.length > 1) {
          x = 20 + (39 * i);
        } else {
          x = 39;
        }
        g2d.drawImage(diceIcon, x, y, null);
      }
      if (fNrOfBlockDice < 0) {
        g2d.setFont(_DICE_FONT);
        g2d.setComposite(oldComposite);
        FontMetrics fontMetrics = g2d.getFontMetrics();
        String opponentsChoice = "Opponent's choice";
        y += 38 + fontMetrics.getAscent();
        x = UtilClientGraphics.findCenteredX(g2d, opponentsChoice, WIDTH);
        UtilClientGraphics.drawShadowedText(g2d, opponentsChoice, x, y);
      }
      g2d.dispose();
    }
  }
  
  public void init() {
  	fStatusTitle = null;
  	fStatusMessage = null;
  	fTurnMode = null;
  	fHomePlaying = false;
  	fEndTurnButtonHidden = false;
  	fBlockDiceIndex = -1;
  	fNrOfBlockDice = 0;
  	fBlockRoll = null;
  	fWaitingForOpponent = false;
  	fTimeoutPossible = false;
  	fTimeoutEnforced = false;
  	fRefreshNecessary = true;
  	refresh();
  }

  public void refresh() {
    FantasyFootballClient client = getSideBar().getClient();
    Game game = client.getGame();
    ClientData clientData = client.getClientData();
    if (!fRefreshNecessary) {
      fRefreshNecessary = (!StringTool.isEqual(fStatusTitle, clientData.getStatusTitle()) || !StringTool.isEqual(fStatusMessage, clientData.getStatusMessage())); 
    }
    if (!fRefreshNecessary) {
      fRefreshNecessary = ((fTurnMode == null) || (fTurnMode != game.getTurnMode()) || (fHomePlaying != game.isHomePlaying()));
    }
    if (!fRefreshNecessary) {
    	fRefreshNecessary = (fEndTurnButtonHidden != clientData.isEndTurnButtonHidden());
    }
    if (!fRefreshNecessary) {
      fRefreshNecessary = ((fBlockDiceIndex != clientData.getBlockDiceIndex()) || (fNrOfBlockDice != clientData.getNrOfBlockDice()) || !ArrayTool.isEqual(fBlockRoll, clientData.getBlockRoll()));
    }
    if (!fRefreshNecessary) {
      fRefreshNecessary = ((fWaitingForOpponent != game.isWaitingForOpponent()) || (fTimeoutPossible != game.isTimeoutPossible()) || (fTimeoutEnforced != game.isTimeoutEnforced()) || !DateTool.isEqual(fFinished, game.getFinished()));
    }
    if (fRefreshNecessary) {
      fButtonSelected = false;
      fTurnMode = game.getTurnMode();
      fHomePlaying = game.isHomePlaying();
      fNrOfBlockDice = clientData.getNrOfBlockDice();
      fBlockRoll = clientData.getBlockRoll();
      fBlockDiceIndex = clientData.getBlockDiceIndex();
      fStatusTitle = clientData.getStatusTitle();
      fStatusMessage = clientData.getStatusMessage();
      fStatusType = clientData.getStatusType();
      fTimeoutPossible = game.isTimeoutPossible();
      fTimeoutEnforced = game.isTimeoutEnforced();
      fWaitingForOpponent = game.isWaitingForOpponent();
      fFinished = game.getFinished();
      fEndTurnButtonHidden = clientData.isEndTurnButtonHidden();
      drawBackground();
      if (getSideBar().isHomeSide()) {
        if (ClientMode.PLAYER == client.getMode()) {
          if (fHomePlaying) {
            drawEndTurnButton();
            drawBlockDice();
          } else {
            drawTimeoutButton();
          }
        } else {
          if (fHomePlaying) {
            drawPlaying();
            drawBlockDice();
          }
        }
      } else {
        if (StringTool.isProvided(fStatusTitle) && StringTool.isProvided(fStatusMessage)) {
          drawStatus();
        } else {
          if (!fHomePlaying) {
            drawPlaying();
            drawBlockDice();
          }
        }
      }
      repaint();
      fRefreshNecessary = false;
    }
  }
  
  public void mouseClicked(MouseEvent pMouseEvent) {
  }

  public void mouseEntered(MouseEvent pMouseEvent) {
  }

  public void mouseExited(MouseEvent pMouseEvent) {
    mouseMoved(pMouseEvent);
  }

  public void mousePressed(MouseEvent pMouseEvent) {
  }

  public void mouseReleased(MouseEvent pMouseEvent) {
    FantasyFootballClient client = getSideBar().getClient(); 
    Game game = client.getGame();
    UserInterface userInterface = client.getUserInterface();
    if ((fEndTurnButtonShown || fTimeoutButtonShown) && getSideBar().isHomeSide() && _BUTTON_AREA.contains(pMouseEvent.getPoint())) {
    	if (userInterface.getDialogManager().isEndTurnAllowed()) {
        fButtonSelected = false;
        if (fHomePlaying) {
          if (((fTurnMode == TurnMode.REGULAR) || (fTurnMode == TurnMode.BLITZ)) && UtilPlayer.testPlayersAbleToAct(game, game.getTeamHome())) {
            DialogEndTurn endTurnDialog = new DialogEndTurn(getSideBar().getClient());
            endTurnDialog.showDialog(this);
          } else {
            client.getClientState().endTurn();
          }
        } else {
          client.getClientState().actionKeyPressed(ActionKey.TOOLBAR_ILLEGAL_PROCEDURE);
        }
    	}
    }
  }

  public void mouseDragged(MouseEvent pMouseEvent) {
  }

  public void mouseMoved(MouseEvent pMouseEvent) {
    // System.out.println("mouseMoved (" + pMouseEvent.getX() + "," +
    // pMouseEvent.getY() + ") fButtonText=" + fButtonText + " fButtonSelected="
    // + fButtonSelected + " contained=" +
    // _BUTTON_AREA.contains(pMouseEvent.getPoint()));
    if ((fEndTurnButtonShown || fTimeoutButtonShown) && !fButtonSelected && _BUTTON_AREA.contains(pMouseEvent.getPoint())) {
      fButtonSelected = true;
      if (fEndTurnButtonShown) {
        drawEndTurnButton();
      }
      if (fTimeoutButtonShown) {
        drawTimeoutButton();
      }
      repaint(_BUTTON_AREA);
    }
    if ((fEndTurnButtonShown || fTimeoutButtonShown) && fButtonSelected && !_BUTTON_AREA.contains(pMouseEvent.getPoint())) {
      fButtonSelected = false;
      if (fEndTurnButtonShown) {
        drawEndTurnButton();
      }
      if (fTimeoutButtonShown) {
        drawTimeoutButton();
      }
      repaint(_BUTTON_AREA);
    }
  }

  protected void paintComponent(Graphics pGraphics) {
    pGraphics.drawImage(fImage, 0, 0, null);
  }
  
  public void dialogClosed(IDialog pDialog) {
    pDialog.hideDialog();
    if (DialogId.END_TURN == pDialog.getId()) {
    	DialogEndTurn dialogEndTurn = (DialogEndTurn) pDialog;
      FantasyFootballClient client = getSideBar().getClient(); 
      if (dialogEndTurn.getChoice() == DialogEndTurn.YES) {
        client.getClientState().endTurn();
      }
    }
  }

}
