package com.fumbbl.ffb.client.ui;

import com.fumbbl.ffb.BoxType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.client.*;
import com.fumbbl.ffb.client.util.UtilClientGraphics;
import com.fumbbl.ffb.client.util.MarkerService;
import com.fumbbl.ffb.client.util.UtilClientPlayerDrag;
import com.fumbbl.ffb.marking.PlayerMarker;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.change.IModelChangeObserver;
import com.fumbbl.ffb.model.change.ModelChange;
import com.fumbbl.ffb.model.change.ModelChangeId;

import javax.swing.JPanel;
import javax.swing.ToolTipManager;
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
public class BoxComponent extends JPanel implements MouseListener, MouseMotionListener, IModelChangeObserver {

	public static final int MAX_BOX_ELEMENTS = 30;

	private final FontCache fontCache;

	private final SideBarComponent fSideBar;
	private BufferedImage fImage;
	private Dimension size;
	private BoxType fOpenBox;
	private final List<BoxSlot> fBoxSlots;
	private int fMaxTitleOffset;
	private final UiDimensionProvider uiDimensionProvider;
	private final DugoutDimensionProvider dugoutDimensionProvider;
	private final StyleProvider styleProvider;
	private Font boxFont;
	private final MarkerService markerService;

	public BoxComponent(SideBarComponent pSideBar, UiDimensionProvider uiDimensionProvider, DugoutDimensionProvider dugoutDimensionProvider,
											StyleProvider styleProvider, FontCache fontCache, MarkerService markerService) {
		fSideBar = pSideBar;
		this.fontCache = fontCache;
		this.markerService = markerService;
		fBoxSlots = new ArrayList<>();
		fOpenBox = null;
		addMouseListener(this);
		addMouseMotionListener(this);
		this.uiDimensionProvider = uiDimensionProvider;
		this.dugoutDimensionProvider = dugoutDimensionProvider;
		this.styleProvider = styleProvider;
		ToolTipManager.sharedInstance().registerComponent(this);
	}

	public void initLayout() {
		size = uiDimensionProvider.dimension(Component.BOX);
		fImage = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
		setLayout(null);
		setMinimumSize(size);
		setPreferredSize(size);
		setMaximumSize(size);
	}

	public void initObserver() {
		fSideBar.getClient().getGame().addObserver(this);
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
		if (styleProvider.getFrameBackground() == null) {
			IconCache iconCache = getSideBar().getClient().getUserInterface().getIconCache();
			BufferedImage background = iconCache.getIconByProperty(IIconProperty.SIDEBAR_BACKGROUND_BOX, uiDimensionProvider);
			g2d.drawImage(background, 0, 0, size.width, size.height, null);
		} else {
			g2d.setColor(styleProvider.getFrameBackground());
			g2d.fillRect(0, 0, size.width, size.height);
		}
		g2d.dispose();
	}

	public void refresh() {
		boxFont = fontCache.font(Font.BOLD, 12, uiDimensionProvider);

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
				BufferedImage icon = playerIconFactory.getIcon(getSideBar().getClient(), pBoxSlot.getPlayer(), dugoutDimensionProvider);
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
		Dimension dimension = uiDimensionProvider.dimension(Component.BOX_SQUARE);
		int yPos = drawTitle(boxState, pYPosition);
		int row = -1;
		for (int y = 0; y < MAX_BOX_ELEMENTS; y++) {
			Player<?> player = fieldModel.getPlayer(new FieldCoordinate(pXCoordinate, y));
			if ((player != null) || (pXCoordinate == FieldCoordinate.RSV_HOME_X)) {
				row = y / 3;
				int locationX = (y % 3) * dimension.width;
				int locationY = yPos + (row * dimension.height);
				BoxSlot boxSlot = new BoxSlot(new Rectangle(locationX, locationY, dimension.width, dimension.height)
				);
				boxSlot.setPlayer(player);
				fBoxSlots.add(boxSlot);
				drawBoxSlot(boxSlot);
			}
		}
		if (row >= 0) {
			yPos += (row + 1) * dimension.height;
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
					Dimension dimension =  uiDimensionProvider.dimension(Component.BOX_SQUARE);
					markerService.showMarkerPopup(getSideBar().getClient(), getSideBar(), boxSlot.getPlayer(), pMouseEvent.getX() + dimension.width / 2, pMouseEvent.getY() + dimension.height / 2);
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
				g2d.setFont(boxFont);
				FontMetrics metrics = g2d.getFontMetrics();
				Rectangle2D bounds = metrics.getStringBounds(title, g2d);
				int x = ((size.width - (int) bounds.getWidth()) / 2);
				int y = pYPosition + metrics.getAscent() + 2;
				UtilClientGraphics.drawShadowedText(g2d, title, x, y, styleProvider);
				y = pYPosition + ((int) bounds.getHeight() / 2) + 3;
				g2d.setColor(styleProvider.getFrame());
				g2d.drawLine(2, y, x - 4, y);
				g2d.drawLine(x + (int) bounds.getWidth() + 4, y, size.width - 3, y);
				g2d.setColor(styleProvider.getFrameShadow());
				g2d.drawLine(2, y + 1, x - 4, y + 1);
				g2d.drawLine(x + (int) bounds.getWidth() + 4, y + 1, size.width - 3, y + 1);
				height = (int) bounds.getHeight() + 4;
				if (height > fMaxTitleOffset) {
					fMaxTitleOffset = height;
				}
				g2d.dispose();
			}
		}
		return pYPosition + height;
	}

	@Override
	public void update(ModelChange pModelChange) {
		if ((pModelChange == null) || (pModelChange.getChangeId() == null)) {
			return;
		}

		if (pModelChange.getChangeId() == ModelChangeId.FIELD_MODEL_ADD_PLAYER_MARKER) {
			PlayerMarker marker = (PlayerMarker) pModelChange.getValue();
			Game game = fSideBar.getClient().getGame();
			Player<?> player = game.getPlayerById(marker.getPlayerId());
			if (player != null) {
				FieldCoordinate coordinate = game.getFieldModel().getPlayerCoordinate(player);
				if (coordinate != null && coordinate.isBoxCoordinate()
					&& fSideBar.isHomeSide() == game.getTeamHome().hasPlayer(player)) {
					drawPlayers();
				}
			}
		}
	}
}
