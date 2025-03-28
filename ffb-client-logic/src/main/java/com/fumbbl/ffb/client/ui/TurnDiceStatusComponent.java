package com.fumbbl.ffb.client.ui;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.IClientPropertyValue;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.StatusType;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.client.*;
import com.fumbbl.ffb.client.dialog.DialogEndTurn;
import com.fumbbl.ffb.client.dialog.IDialog;
import com.fumbbl.ffb.client.dialog.IDialogCloseListener;
import com.fumbbl.ffb.client.util.UtilClientGraphics;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.model.BlockRoll;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.util.DateTool;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilPlayer;

import javax.swing.JPanel;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 
 * @author Kalimar
 */
public class TurnDiceStatusComponent extends JPanel
	implements MouseListener, MouseMotionListener, IDialogCloseListener {

	private static final String _LABEL_END_TURN = "End Turn";
	private static final String _LABEL_END_SETUP = "End Setup";
	private static final String _LABEL_CONTINUE = "Continue";
	private static final String _LABEL_KICKOFF = "Kick Off";
	private static final String _LABEL_TIMEOUT = "Timeout";

	private Font buttonFont;

	private Font diceFont;
	private Font statusTitleFont;
	private Font statusMessageFont;

	private final SideBarComponent fSideBar;
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

	private final List<BlockRoll> blockRolls = new ArrayList<>();

	private boolean fRefreshNecessary;
	private boolean buttonEnabled = true;

	private Dimension size;

	private final DimensionProvider dimensionProvider;
	private final StyleProvider styleProvider;
	private Rectangle buttonArea;


	public TurnDiceStatusComponent(SideBarComponent pSideBar, DimensionProvider dimensionProvider, StyleProvider styleProvider) {
		fSideBar = pSideBar;
		addMouseListener(this);
		addMouseMotionListener(this);
		fRefreshNecessary = true;
		this.dimensionProvider = dimensionProvider;
		this.styleProvider = styleProvider;
	}

	public void initLayout() {
		size = dimensionProvider.dimension(Component.TURN_DICE_STATUS);
		fImage = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
		setLayout(null);
		setMinimumSize(size);
		setPreferredSize(size);
		setMaximumSize(size);
		Dimension buttonDimension = dimensionProvider.dimension(Component.END_TURN_BUTTON);
		buttonArea = new Rectangle(1, 1, buttonDimension.width, buttonDimension.height);
	}

	public SideBarComponent getSideBar() {
		return fSideBar;
	}

	private int statusTextWidth() {
		return size.width - dimensionProvider.scale(10);
	}

	private void drawBackground() {
		Graphics2D g2d = fImage.createGraphics();
		if (styleProvider.getFrameBackground() == null) {
			IconCache iconCache = getSideBar().getClient().getUserInterface().getIconCache();
			BufferedImage background;

			String swapSetting = fSideBar.getClient().getProperty(CommonProperty.SETTING_SWAP_TEAM_COLORS);
			boolean swapColors = IClientPropertyValue.SETTING_SWAP_TEAM_COLORS_ON.equals(swapSetting);

			boolean homeSide = getSideBar().isHomeSide();
			if (swapColors) {
				homeSide = !homeSide;
			}
			if (homeSide) {
				background = iconCache.getIconByProperty(IIconProperty.SIDEBAR_BACKGROUND_TURN_DICE_STATUS_RED, dimensionProvider);
			} else {
				background = iconCache.getIconByProperty(IIconProperty.SIDEBAR_BACKGROUND_TURN_DICE_STATUS_BLUE, dimensionProvider);
			}
			g2d.drawImage(background, 0, 0, size.width, size.height, null);
		} else {
			g2d.setColor(styleProvider.getFrameBackground());
			g2d.fillRect(0, 0, size.width, size.height);
		}
		g2d.dispose();
	}

	private void drawEndTurnButton() {
		fEndTurnButtonShown = false;
		ClientData clientData = getSideBar().getClient().getClientData();
		if ((fFinished == null) && (fTurnMode != null) && fHomePlaying && !fWaitingForOpponent
				&& !clientData.isEndTurnButtonHidden()) {
			switch (fTurnMode) {
			case START_GAME:
				break;
			case SETUP:
			case PERFECT_DEFENCE:
			case QUICK_SNAP:
			case HIGH_KICK:
				drawButton(_LABEL_END_SETUP);
				fEndTurnButtonShown = true;
				buttonEnabled = true;
				break;
			case KICKOFF_RETURN:
			case SWARMING:
			case PASS_BLOCK:
			case ILLEGAL_SUBSTITUTION:
				drawButton(_LABEL_CONTINUE);
				fEndTurnButtonShown = true;
				buttonEnabled = true;
				break;
			case KICKOFF:
				drawButton(_LABEL_KICKOFF);
				fEndTurnButtonShown = true;
				buttonEnabled = true;
				break;
			default:
				drawButton(_LABEL_END_TURN);
				fEndTurnButtonShown = true;
				buttonEnabled = true;
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
			BufferedImage buttonImage = iconCache.getIconByProperty(
					fButtonSelected ? IIconProperty.SIDEBAR_TURN_BUTTON_SELECTED : IIconProperty.SIDEBAR_TURN_BUTTON, dimensionProvider);
			g2d.drawImage(buttonImage, buttonArea.x, buttonArea.y, buttonArea.width, buttonArea.height, null);
			g2d.setFont(buttonFont);
			g2d.setColor(Color.BLACK);
			FontMetrics metrics = g2d.getFontMetrics();
			Rectangle2D bounds = metrics.getStringBounds(pButtonText, g2d);
			int x = ((size.width - (int) bounds.getWidth()) / 2);
			int y = ((buttonArea.height + metrics.getHeight()) / 2) - metrics.getDescent();
			g2d.drawString(pButtonText, x, y);
			g2d.dispose();
		}
	}

	private void drawPlaying() {
		if ((fTurnMode != null) && (fTurnMode != TurnMode.START_GAME) && (fFinished == null)) {
			Graphics2D g2d = fImage.createGraphics();
			IconCache iconCache = getSideBar().getClient().getUserInterface().getIconCache();
			BufferedImage playingImage = iconCache.getIconByProperty(IIconProperty.SIDEBAR_STATUS_PLAYING, dimensionProvider);
			g2d.drawImage(playingImage, buttonArea.x, buttonArea.y, buttonArea.width, buttonArea.height, null);
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
				BufferedImage statusImage = iconCache.getIconByProperty(imageProperty, dimensionProvider);
				g2d.drawImage(statusImage, buttonArea.x, buttonArea.y, buttonArea.width, size.height, null);
			}
			g2d.setColor(Color.BLACK);
			g2d.setFont(statusTitleFont);
			FontMetrics fontMetrics = g2d.getFontMetrics();
			int x = 4;
			int y = fontMetrics.getHeight();
			g2d.drawString(fStatusTitle, x, y);
			g2d.setFont(statusMessageFont);
			fontMetrics = g2d.getFontMetrics();
			y += fontMetrics.getHeight();
			final AttributedString attStr = new AttributedString(fStatusMessage);
			attStr.addAttribute(TextAttribute.FONT, g2d.getFont());
			final LineBreakMeasurer measurer = new LineBreakMeasurer(attStr.getIterator(),
				new FontRenderContext(null, false, false));
			TextLayout layoutLine = measurer.nextLayout(statusTextWidth());
			while (layoutLine != null) {
				layoutLine.draw(g2d, x, y);
				y += fontMetrics.getHeight();
				if (y <= 3 * fontMetrics.getHeight()) {
					layoutLine = measurer.nextLayout(statusTextWidth());
				} else {
					layoutLine = measurer.nextLayout(statusTextWidth() - dimensionProvider.scale(20)); // hourglass icon
				}
			}
			g2d.dispose();
		}
	}

	private void drawBlockDice() {
		int lineHeight = dimensionProvider.scale(38);
		int x, y = blockRolls.size() > 1 ? 0 : lineHeight;
		for (BlockRoll blockRoll : blockRolls) {
			Graphics2D g2d = fImage.createGraphics();
			Composite oldComposite = g2d.getComposite();
			IconCache iconCache = getSideBar().getClient().getUserInterface().getIconCache();
			int length = blockRoll.getBlockRoll().length;
			for (int i = 0; i < length; i++) {
				g2d.setComposite(oldComposite);
				BufferedImage diceIcon = iconCache.getDiceIcon(blockRoll.getBlockRoll()[i], dimensionProvider);
				if (!blockRoll.needsSelection() && (blockRoll.getSelectedIndex() != i)) {
					g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
				}
				int dieWidth = dimensionProvider.scale(39);
				if (length > 2) {
					x = dimensionProvider.scale(15) + (dieWidth * i);
				} else if (length > 1) {
					x = dimensionProvider.scale(34) + (dieWidth * i);
				} else {
					x = dimensionProvider.scale(53);
				}
				g2d.drawImage(diceIcon, x, y, null);
			}
			if (!blockRoll.isOwnChoice()) {
				g2d.setFont(diceFont);
				g2d.setComposite(oldComposite);
				FontMetrics fontMetrics = g2d.getFontMetrics();
				String opponentsChoice = "Opponent's choice";
				y += lineHeight + fontMetrics.getAscent();
				x = UtilClientGraphics.findCenteredX(g2d, opponentsChoice, size.width);
				UtilClientGraphics.drawShadowedText(g2d, opponentsChoice, x, y, styleProvider);
			} else {
				y += lineHeight;
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

		FontCache fontCache = client.getUserInterface().getFontCache();

		buttonFont = fontCache.font(Font.BOLD, 14, dimensionProvider);

		diceFont = fontCache.font(Font.BOLD, 11, dimensionProvider);
		statusTitleFont = fontCache.font(Font.BOLD, 12, dimensionProvider);
		statusMessageFont = fontCache.font(Font.PLAIN, 12, dimensionProvider);

		if (!fRefreshNecessary) {
			fRefreshNecessary = (!StringTool.isEqual(fStatusTitle, clientData.getStatusTitle())
				|| !StringTool.isEqual(fStatusMessage, clientData.getStatusMessage()));
		}
		if (!fRefreshNecessary) {
			fRefreshNecessary = ((fTurnMode == null) || (fTurnMode != game.getTurnMode())
				|| (fHomePlaying != game.isHomePlaying()));
		}
		if (!fRefreshNecessary) {
			fRefreshNecessary = (fEndTurnButtonHidden != clientData.isEndTurnButtonHidden());
		}
		if (!fRefreshNecessary) {
			fRefreshNecessary = !blockRolls.equals(clientData.getBlockRolls());
		}
		if (!fRefreshNecessary) {
			fRefreshNecessary = ((fWaitingForOpponent != game.isWaitingForOpponent())
					|| (fTimeoutPossible != game.isTimeoutPossible()) || (fTimeoutEnforced != game.isTimeoutEnforced())
					|| !DateTool.isEqual(fFinished, game.getFinished()));
		}
		if (fRefreshNecessary) {
			fButtonSelected = false;
			fTurnMode = game.getTurnMode();
			fHomePlaying = game.isHomePlaying();
			blockRolls.clear();
			blockRolls.addAll(clientData.getBlockRolls());
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
		if ((fEndTurnButtonShown || fTimeoutButtonShown) && getSideBar().isHomeSide()
			&& buttonArea.contains(pMouseEvent.getPoint()) && buttonEnabled) {
			if (userInterface.getDialogManager().isEndTurnAllowed()) {
				buttonEnabled = false;
				fButtonSelected = false;
				if (fHomePlaying) {
					if (fTurnMode != null && fTurnMode.isCheckForActivePlayers()
						&& UtilPlayer.testPlayersAbleToAct(game, game.getTeamHome())) {
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
		if ((fEndTurnButtonShown || fTimeoutButtonShown) && !fButtonSelected
			&& buttonArea.contains(pMouseEvent.getPoint())) {
			fButtonSelected = true;
			if (fEndTurnButtonShown) {
				drawEndTurnButton();
			}
			if (fTimeoutButtonShown) {
				drawTimeoutButton();
			}
			repaint(buttonArea);
		}
		if ((fEndTurnButtonShown || fTimeoutButtonShown) && fButtonSelected
			&& !buttonArea.contains(pMouseEvent.getPoint())) {
			fButtonSelected = false;
			if (fEndTurnButtonShown) {
				drawEndTurnButton();
			}
			if (fTimeoutButtonShown) {
				drawTimeoutButton();
			}
			repaint(buttonArea);
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
			} else {
				buttonEnabled = true;
			}
		}
	}

	public void enableButton() {
		buttonEnabled = true;
		repaint(buttonArea);
	}
}
