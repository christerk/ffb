package com.fumbbl.ffb.client.ui;

import com.fumbbl.ffb.BoxType;
import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IClientPropertyValue;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.client.*;
import com.fumbbl.ffb.model.FieldModel;

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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Kalimar
 */
public class BoxButtonComponent extends JPanel implements MouseListener, MouseMotionListener {

	private final FontCache fontCache;
	private final Map<BoxType, Rectangle> fButtonLocations;

	private final SideBarComponent fSideBar;
	private BufferedImage fImage;
	private BoxType fOpenBox;
	private BoxType fSelectedBox;
	private final DimensionProvider dimensionProvider;
	private final StyleProvider styleProvider;
	private Font buttonFont;
	private Dimension size;

	public BoxButtonComponent(SideBarComponent pSideBar, DimensionProvider dimensionProvider, StyleProvider styleProvider, FontCache fontCache) {
		fSideBar = pSideBar;
		this.fontCache = fontCache;
		fButtonLocations = new HashMap<>();
		fOpenBox = null;
		addMouseListener(this);
		addMouseMotionListener(this);
		this.dimensionProvider = dimensionProvider;
		this.styleProvider = styleProvider;
		ToolTipManager.sharedInstance().registerComponent(this);
	}

	public void initLayout() {
		size = dimensionProvider.dimension(Component.BUTTON_BOX, RenderContext.UI);
		fImage = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
		Dimension button = dimensionProvider.dimension(Component.BOX_BUTTON, RenderContext.UI);
		if (getSideBar().isHomeSide()) {
			fButtonLocations.put(BoxType.RESERVES, new Rectangle(1, 0, button.width, button.height));
			fButtonLocations.put(BoxType.OUT,
				new Rectangle((size.width / 2) + 1, 0, button.width, button.height));
		} else {
			fButtonLocations.put(BoxType.OUT, new Rectangle(1, 0, button.width, button.height));
			fButtonLocations.put(BoxType.RESERVES,
				new Rectangle((size.width / 2) + 1, 0, button.width, button.height));
		}

		setLayout(null);
		setMinimumSize(size);
		setPreferredSize(size);
		setMaximumSize(size);
	}

	public void closeBox() {
		openBox(null);
	}

	public void openBox(BoxType pBox) {
		fOpenBox = pBox;
		refresh();
	}

	private BoxType findBoxTypeForLocation(Point pLocation) {
		BoxType boxType = null;
		if (pLocation != null) {
			Iterator<BoxType> boxTabIterator = fButtonLocations.keySet().iterator();
			while ((boxType == null) && boxTabIterator.hasNext()) {
				BoxType testedTab = boxTabIterator.next();
				Rectangle boxLocation = fButtonLocations.get(testedTab);
				if (boxLocation.contains(pLocation)) {
					boxType = testedTab;
				}
			}
		}
		return boxType;
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
				background = iconCache.getIconByProperty(IIconProperty.SIDEBAR_BACKGROUND_BOX_BUTTONS_RED, RenderContext.UI);
			} else {
				background = iconCache.getIconByProperty(IIconProperty.SIDEBAR_BACKGROUND_BOX_BUTTONS_BLUE, RenderContext.UI);
			}
			g2d.drawImage(background, 0, 0, size.width, size.height, null);
		} else {
			g2d.setColor(styleProvider.getFrameBackground());
			g2d.fillRect(0, 0, size.width, size.height);
		}
		g2d.dispose();
	}

	private void drawButton(BoxType pBox) {
		if (pBox != null) {
			Graphics2D g2d = fImage.createGraphics();
			IconCache iconCache = getSideBar().getClient().getUserInterface().getIconCache();
			Rectangle buttonLocation = fButtonLocations.get(pBox);
			BufferedImage buttonImage;
			if ((pBox == fOpenBox) || (pBox == fSelectedBox)) {
				buttonImage = iconCache.getIconByProperty(IIconProperty.SIDEBAR_BOX_BUTTON_SELECTED, RenderContext.UI);
			} else {
				buttonImage = iconCache.getIconByProperty(IIconProperty.SIDEBAR_BOX_BUTTON, RenderContext.UI);
			}
			g2d.drawImage(buttonImage, buttonLocation.x, buttonLocation.y, buttonLocation.width, buttonLocation.height, null);
			g2d.setFont(buttonFont);
			g2d.setColor(Color.BLACK);
			FontMetrics metrics = g2d.getFontMetrics();
			String buttonText = countBoxElements(pBox, getSideBar().isHomeSide()) + " " +
				pBox.getShortcut();
			int x = buttonLocation.x + findCenteredX(g2d, buttonText, buttonLocation.width);
			int y = buttonLocation.y + (buttonLocation.height + metrics.getHeight()) / 2 - metrics.getDescent();
			g2d.drawString(buttonText, x, y);
			g2d.dispose();
		}
	}

	private int findCenteredX(Graphics2D pG2d, String pText, int pWidth) {
		FontMetrics metrics = pG2d.getFontMetrics();
		Rectangle2D bounds = metrics.getStringBounds(pText, pG2d);
		return ((pWidth - (int) bounds.getWidth()) / 2);
	}

	public void refresh() {
		buttonFont = fontCache.font(Font.BOLD, 11, RenderContext.UI);

		drawBackground();
		drawButton(BoxType.RESERVES);
		drawButton(BoxType.OUT);
		repaint();
	}

	protected void paintComponent(Graphics pGraphics) {
		pGraphics.drawImage(fImage, 0, 0, null);
	}

	public SideBarComponent getSideBar() {
		return fSideBar;
	}

	public String getToolTipText(MouseEvent pEvent) {
		BoxType box = findBoxTypeForLocation(pEvent.getPoint());
		if (box != null) {
			int players = countBoxElements(box, getSideBar().isHomeSide());
			StringBuilder toolTip = new StringBuilder();
			if (players > 0) {
				toolTip.append(players).append(" ");
			} else {
				toolTip.append("No ");
			}
			if (players == 1) {
				toolTip.append(box.getToolTipSingle());
			} else {
				toolTip.append(box.getToolTipMultiple());
			}
			return toolTip.toString();
		} else {
			return null;
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
		BoxType newBox = findBoxTypeForLocation(pMouseEvent.getPoint());
		if (newBox != null) {
			if (newBox != fOpenBox) {
				getSideBar().openBox(newBox);
			} else {
				getSideBar().closeBox();
			}
		}
	}

	public void mouseReleased(MouseEvent pMouseEvent) {
	}

	public void mouseMoved(MouseEvent pMouseEvent) {
		BoxType newBox = findBoxTypeForLocation(pMouseEvent.getPoint());
		BoxType oldSelectedBox = fSelectedBox;
		if (newBox != null) {
			if (newBox != fSelectedBox) {
				fSelectedBox = newBox;
				drawButton(fSelectedBox);
				if (oldSelectedBox != null) {
					drawButton(oldSelectedBox);
				}
				repaint();
			}
		} else {
			if (oldSelectedBox != null) {
				fSelectedBox = null;
				drawButton(oldSelectedBox);
				repaint();
			}
		}
	}

	public void mouseDragged(MouseEvent pMouseEvent) {
	}

	public int countBoxElements(BoxType pBox, boolean pHomeSide) {
		int elements = 0;
		if (BoxType.RESERVES == pBox) {
			elements += countBoxElements(pHomeSide ? FieldCoordinate.RSV_HOME_X : FieldCoordinate.RSV_AWAY_X);
		}
		if (BoxType.OUT == pBox) {
			elements += countBoxElements(pHomeSide ? FieldCoordinate.KO_HOME_X : FieldCoordinate.KO_AWAY_X);
			elements += countBoxElements(pHomeSide ? FieldCoordinate.BH_HOME_X : FieldCoordinate.BH_AWAY_X);
			elements += countBoxElements(pHomeSide ? FieldCoordinate.SI_HOME_X : FieldCoordinate.SI_AWAY_X);
			elements += countBoxElements(pHomeSide ? FieldCoordinate.RIP_HOME_X : FieldCoordinate.RIP_AWAY_X);
			elements += countBoxElements(pHomeSide ? FieldCoordinate.BAN_HOME_X : FieldCoordinate.BAN_AWAY_X);
			// elements += countBoxElements(pHomeSide ? FieldCoordinate.MNG_HOME_X:
			// FieldCoordinate.MNG_AWAY_X);
		}
		return elements;
	}

	private int countBoxElements(int pXCoordinate) {
		int elements = 0;
		FieldModel fieldModel = getSideBar().getClient().getGame().getFieldModel();
		for (int y = 0; y < BoxComponent.MAX_BOX_ELEMENTS; y++) {
			if (fieldModel.getPlayer(new FieldCoordinate(pXCoordinate, y)) != null) {
				elements++;
			}
		}
		return elements;
	}

}
