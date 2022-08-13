package com.fumbbl.ffb.client.ui;

import com.fumbbl.ffb.BoxType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.IconCache;
import com.fumbbl.ffb.client.PlayerIconFactory;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.util.UtilClientGraphics;
import com.fumbbl.ffb.client.util.UtilClientMarker;
import com.fumbbl.ffb.client.util.UtilClientPlayerDrag;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

import javax.swing.JPanel;
import javax.swing.ToolTipManager;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kalimar
 */
public class BoxComponent extends JPanel implements MouseListener, MouseMotionListener {

	public static final int WIDTH = 145;
	public static final int HEIGHT = 430;

	public static final int MAX_BOX_ELEMENTS = 30;
	public static final int FIELD_SQUARE_SIZE = 39;

	private static final Font _BOX_FONT = new Font("Sans Serif", Font.BOLD, 12);

	private final SideBarComponent fSideBar;
	private final BufferedImage fImage;
	private BoxType fOpenBox;
	private final List<BoxSlot> fBoxSlots;
	private int fMaxTitleOffset;

	public BoxComponent(SideBarComponent pSideBar) {
		fSideBar = pSideBar;
		fImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
		fBoxSlots = new ArrayList<>();
		setLayout(null);
		Dimension size = new Dimension(WIDTH, HEIGHT);
		setMinimumSize(size);
		setPreferredSize(size);
		setMaximumSize(size);
		fOpenBox = null;
		addMouseListener(this);
		addMouseMotionListener(this);
		ToolTipManager.sharedInstance().registerComponent(this);
	}

	public void closeBox() {
		openBox(null);
	}

	public void openBox(BoxType pBox) {
		fOpenBox = pBox;
		refresh();
	}

	private void drawBackground() {
		Graphics2D g2d = fImage.createGraphics();
		IconCache iconCache = getSideBar().getClient().getUserInterface().getIconCache();
		BufferedImage background = iconCache.getIconByProperty(IIconProperty.SIDEBAR_BACKGROUND_BOX);
		g2d.drawImage(background, 0, 0, null);
		g2d.dispose();
	}

	public void refresh() {
		drawBackground();
		drawPlayers();
		repaint();
	}

	private void drawBoxSlot(BoxSlot pBoxSlot) {
		if ((pBoxSlot != null) && (pBoxSlot.getPlayer() != null)) {
			PlayerIconFactory playerIconFactory = getSideBar().getClient().getUserInterface().getPlayerIconFactory();
			FieldModel fieldModel = getSideBar().getClient().getGame().getFieldModel();
			FieldCoordinate playerCoordinate = fieldModel.getPlayerCoordinate(pBoxSlot.getPlayer());
			if (playerCoordinate != null) {
				BufferedImage icon = playerIconFactory.getIcon(getSideBar().getClient(), pBoxSlot.getPlayer());
				if (icon != null) {
					Graphics2D g2d = fImage.createGraphics();
					int x = pBoxSlot.getLocation().x + ((pBoxSlot.getLocation().width - icon.getWidth()) / 2);
					int y = pBoxSlot.getLocation().y + ((pBoxSlot.getLocation().height - icon.getHeight()) / 2);
					g2d.drawImage(icon, x, y, null);
					g2d.dispose();
				}
			}
		}
	}

	private synchronized void drawPlayers() {
		fBoxSlots.clear();
		int yPosition = 0;
		if (BoxType.RESERVES == fOpenBox) {
			yPosition = drawPlayersInBox(getSideBar().isHomeSide() ? FieldCoordinate.RSV_HOME_X : FieldCoordinate.RSV_AWAY_X,
				yPosition);
		}
		if (BoxType.OUT == fOpenBox) {
			yPosition = drawPlayersInBox(getSideBar().isHomeSide() ? FieldCoordinate.KO_HOME_X : FieldCoordinate.KO_AWAY_X,
				yPosition);
			yPosition = drawPlayersInBox(getSideBar().isHomeSide() ? FieldCoordinate.BH_HOME_X : FieldCoordinate.BH_AWAY_X,
				yPosition);
			yPosition = drawPlayersInBox(getSideBar().isHomeSide() ? FieldCoordinate.SI_HOME_X : FieldCoordinate.SI_AWAY_X,
				yPosition);
			yPosition = drawPlayersInBox(getSideBar().isHomeSide() ? FieldCoordinate.RIP_HOME_X : FieldCoordinate.RIP_AWAY_X,
				yPosition);
			drawPlayersInBox(getSideBar().isHomeSide() ? FieldCoordinate.BAN_HOME_X : FieldCoordinate.BAN_AWAY_X,
				yPosition);
			// yPosition = drawPlayersInBox(getSideBar().isHomeSide() ?
			// FieldCoordinate.MNG_HOME_X : FieldCoordinate.MNG_AWAY_X, yPosition);
		}
	}

	private int drawPlayersInBox(int pXCoordinate, int pYPosition) {
		FieldModel fieldModel = getSideBar().getClient().getGame().getFieldModel();
		PlayerState boxState = findPlayerStateForXCoordinate(pXCoordinate);
		int yPos = drawTitle(boxState, pYPosition);
		int row = -1;
		for (int y = 0; y < MAX_BOX_ELEMENTS; y++) {
			Player<?> player = fieldModel.getPlayer(new FieldCoordinate(pXCoordinate, y));
			if ((player != null) || (pXCoordinate == FieldCoordinate.RSV_HOME_X)) {
				row = y / 3;
				int locationX = (y % 3) * FIELD_SQUARE_SIZE;
				int locationY = yPos + (row * FIELD_SQUARE_SIZE);
				BoxSlot boxSlot = new BoxSlot(new Rectangle(locationX, locationY, FIELD_SQUARE_SIZE, FIELD_SQUARE_SIZE),
					boxState);
				boxSlot.setPlayer(player);
				fBoxSlots.add(boxSlot);
				drawBoxSlot(boxSlot);
			}
		}
		if (row >= 0) {
			yPos += (row + 1) * FIELD_SQUARE_SIZE;
		}
		return yPos;
	}

	public synchronized String getToolTipText(MouseEvent pMouseEvent) {
		Game game = getSideBar().getClient().getGame();
		for (BoxSlot boxSlot : fBoxSlots) {
			if (boxSlot.getLocation().contains(pMouseEvent.getPoint())) {
				return boxSlot.getToolTip(game);
			}
		}
		return null;
	}

	protected void paintComponent(Graphics pGraphics) {
		pGraphics.drawImage(fImage, 0, 0, null);
	}

	public SideBarComponent getSideBar() {
		return fSideBar;
	}

	public BoxType getOpenBox() {
		return fOpenBox;
	}

	public int getMaxTitleOffset() {
		return fMaxTitleOffset;
	}

	public void mousePressed(MouseEvent pMouseEvent) {
		synchronized (getSideBar().getClient()) {
			if (getSideBar().isHomeSide() && (BoxType.RESERVES == fOpenBox)) {
				UtilClientPlayerDrag.mousePressed(getSideBar().getClient(), pMouseEvent, true);
			}
		}
	}

	public void mouseDragged(MouseEvent pMouseEvent) {
		synchronized (getSideBar().getClient()) {
			if (getSideBar().isHomeSide() && (BoxType.RESERVES == fOpenBox)) {
				UtilClientPlayerDrag.mouseDragged(getSideBar().getClient(), pMouseEvent, true);
			}
		}
	}

	public void mouseReleased(MouseEvent pMouseEvent) {
		synchronized (getSideBar().getClient()) {
			if (pMouseEvent.isShiftDown()) {
				BoxSlot boxSlot = findSlot(pMouseEvent.getPoint());
				if (boxSlot != null) {
					int x = getSideBar().isHomeSide() ? 5 : getSideBar().getClient().getUserInterface().getDimensionProvider().dimension(DimensionProvider.Component.FIELD).width - 135;
					int y = boxSlot.getLocation().y + boxSlot.getLocation().height;
					UtilClientMarker.showMarkerPopup(getSideBar().getClient(), boxSlot.getPlayer(), x, y);
				}
			} else {
				if (getSideBar().isHomeSide() && (BoxType.RESERVES == fOpenBox)) {
					UtilClientPlayerDrag.mouseReleased(getSideBar().getClient());
				}
			}
		}
	}

	public void mouseClicked(MouseEvent pMouseEvent) {
	}

	public void mouseEntered(MouseEvent pMouseEvent) {
	}

	public void mouseExited(MouseEvent pMouseEvent) {
	}

	public void mouseMoved(MouseEvent pMouseEvent) {
		if (fOpenBox != null) {
			BoxSlot boxSlot = findSlot(pMouseEvent.getPoint());
			if ((boxSlot != null) && (boxSlot.getPlayer() != null)) {
				getSideBar().getClient().getClientData().setSelectedPlayer(boxSlot.getPlayer());
				UserInterface userInterface = getSideBar().getClient().getUserInterface();
				userInterface.refreshSideBars();
			}
		}
	}

	public synchronized BoxSlot findSlot(Point pPoint) {
		for (BoxSlot slot : fBoxSlots) {
			if (slot.getLocation().contains(pPoint)) {
				return slot;
			}
		}
		return null;
	}

	private PlayerState findPlayerStateForXCoordinate(int pXCoordinate) {
		switch (pXCoordinate) {
			case FieldCoordinate.RSV_HOME_X:
			case FieldCoordinate.RSV_AWAY_X:
				return new PlayerState(PlayerState.RESERVE);
			case FieldCoordinate.KO_HOME_X:
			case FieldCoordinate.KO_AWAY_X:
				return new PlayerState(PlayerState.KNOCKED_OUT);
			case FieldCoordinate.BH_HOME_X:
			case FieldCoordinate.BH_AWAY_X:
				return new PlayerState(PlayerState.BADLY_HURT);
			case FieldCoordinate.SI_HOME_X:
			case FieldCoordinate.SI_AWAY_X:
				return new PlayerState(PlayerState.SERIOUS_INJURY);
			case FieldCoordinate.RIP_HOME_X:
			case FieldCoordinate.RIP_AWAY_X:
				return new PlayerState(PlayerState.RIP);
			case FieldCoordinate.BAN_HOME_X:
			case FieldCoordinate.BAN_AWAY_X:
				return new PlayerState(PlayerState.BANNED);
			case FieldCoordinate.MNG_HOME_X:
			case FieldCoordinate.MNG_AWAY_X:
				return new PlayerState(PlayerState.MISSING);
			default:
				break;
		}
		return null;
	}

	private int drawTitle(PlayerState pPlayerState, int pYPosition) {
		int height = 0;
		if (pPlayerState != null) {
			String title = null;
			switch (pPlayerState.getBase()) {
				case PlayerState.RESERVE:
					title = "Reserve";
					break;
				case PlayerState.KNOCKED_OUT:
					title = "Knocked Out";
					break;
				case PlayerState.BADLY_HURT:
					title = "Badly Hurt";
					break;
				case PlayerState.SERIOUS_INJURY:
					title = "Seriously Injured";
					break;
				case PlayerState.RIP:
					title = "Killed";
					break;
				case PlayerState.BANNED:
					title = "Banned";
					break;
				default:
					break;
			}
			if (title != null) {
				Graphics2D g2d = fImage.createGraphics();
				g2d.setFont(_BOX_FONT);
				FontMetrics metrics = g2d.getFontMetrics();
				Rectangle2D bounds = metrics.getStringBounds(title, g2d);
				int x = ((WIDTH - (int) bounds.getWidth()) / 2);
				int y = pYPosition + metrics.getAscent() + 2;
				UtilClientGraphics.drawShadowedText(g2d, title, x, y);
				y = pYPosition + ((int) bounds.getHeight() / 2) + 3;
				g2d.setColor(Color.WHITE);
				g2d.drawLine(2, y, x - 4, y);
				g2d.drawLine(x + (int) bounds.getWidth() + 4, y, WIDTH - 3, y);
				g2d.setColor(Color.BLACK);
				g2d.drawLine(2, y + 1, x - 4, y + 1);
				g2d.drawLine(x + (int) bounds.getWidth() + 4, y + 1, WIDTH - 3, y + 1);
				height = (int) bounds.getHeight() + 4;
				if (height > fMaxTitleOffset) {
					fMaxTitleOffset = height;
				}
				g2d.dispose();
			}
		}
		return pYPosition + height;
	}

}
