package com.fumbbl.ffb.client.layer;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.client.*;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * 
 * @author Kalimar
 */
public abstract class FieldLayer {

	private final FantasyFootballClient fClient;
	protected BufferedImage fImage;
	private Rectangle fUpdatedArea;

	protected Dimension size;

	protected final UiDimensionProvider uiDimensionProvider;
	protected final PitchDimensionProvider pitchDimensionProvider;

	protected final FontCache fontCache;

	public FieldLayer(FantasyFootballClient pClient, UiDimensionProvider uiDimensionProvider, PitchDimensionProvider pitchDimensionProvider, FontCache fontCache) {
		fClient = pClient;
		this.uiDimensionProvider = uiDimensionProvider;
		this.pitchDimensionProvider = pitchDimensionProvider;
		this.fontCache = fontCache;
	}

	public void initLayout() {
		size = uiDimensionProvider.dimension(Component.FIELD);
		fImage = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
		addUpdatedArea(new Rectangle(0, 0, fImage.getWidth(), fImage.getHeight()));
	}

	public BufferedImage getImage() {
		return fImage;
	}

	public synchronized Rectangle fetchUpdatedArea() {
		Rectangle updatedArea = fUpdatedArea;
		fUpdatedArea = null;
		return updatedArea;
	}

	public Rectangle draw(BufferedImage pImage, int pX, int pY, float pAlpha) {
		Graphics2D g2d = fImage.createGraphics();
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, pAlpha));
		g2d.drawImage(pImage, pX, pY, null);
		g2d.dispose();
		Rectangle updatedArea = new Rectangle(pX, pY, pImage.getWidth(), pImage.getHeight());
		addUpdatedArea(updatedArea);
		return updatedArea;
	}

	public Rectangle drawCenteredAndScaled(BufferedImage pImage, int pX, int pY, float pAlpha, double pScaleX,
			double pScaleY) {
		if (pImage != null) {
			int width = (int) (pImage.getWidth() * pScaleX);
			int height = (int) (pImage.getHeight() * pScaleY);
			if ((width > 0) && (height > 0)) {
				BufferedImage scaledIcon = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2d = scaledIcon.createGraphics();
				AffineTransform transformation = AffineTransform.getScaleInstance(pScaleX, pScaleY);
				g2d.drawRenderedImage(pImage, transformation);
				g2d.dispose();
				int x = pX - (width / 2);
				int y = pY - (height / 2);
				return draw(scaledIcon, x, y, pAlpha);
			}
		}
		return null;
	}

	public Rectangle draw(BufferedImage pImage, FieldCoordinate pCoordinate, float pAlpha) {
		if ((pImage != null) && (pCoordinate != null)) {

			return draw(pImage, findCenteredIconUpperLeftX(pImage, pCoordinate),
				findCenteredIconUpperLeftY(pImage, pCoordinate), pAlpha);
		}
		return null;
	}

	protected void clear(int pX, int pY, int pWidth, int pHeight, boolean pUpdateArea) {
		Graphics2D g2d = fImage.createGraphics();
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
		g2d.fillRect(pX, pY, pWidth, pHeight);
		g2d.dispose();
		if (pUpdateArea) {
			addUpdatedArea(new Rectangle(pX, pY, pWidth, pHeight));
		}
	}

	public void clear(Rectangle pRectangle, boolean pUpdateArea) {
		if (pRectangle != null) {
			clear(pRectangle.x, pRectangle.y, pRectangle.width, pRectangle.height, pUpdateArea);
		}
	}

	public void clear(BufferedImage pImage, FieldCoordinate pCoordinate, boolean pUpdateArea) {
		if ((pImage != null) && (pCoordinate != null)) {

			clear(findCenteredIconUpperLeftX(pImage, pCoordinate), findCenteredIconUpperLeftY(pImage, pCoordinate),
				pImage.getWidth(), pImage.getHeight(), pUpdateArea);
		}
	}

	protected int findCenteredIconUpperLeftX(BufferedImage pImage, FieldCoordinate pCoordinate) {
		Dimension dimension = pitchDimensionProvider.mapToLocal(pCoordinate, true);
		return dimension.width - (pImage.getWidth() / 2);
	}

	protected int findCenteredIconUpperLeftY(BufferedImage pImage, FieldCoordinate pCoordinate) {
		Dimension dimension = pitchDimensionProvider.mapToLocal(pCoordinate, true);
		return dimension.height - (pImage.getHeight() / 2);
	}

	public void clear(boolean pUpdateArea) {
		clear(0, 0, getImage().getWidth(), getImage().getHeight(), pUpdateArea);
	}

	public void clear(FieldCoordinate pCoordinate, boolean pUpdateArea) {
		if ((pCoordinate != null) && FieldCoordinateBounds.FIELD.isInBounds(pCoordinate)) {
			Dimension dimension = pitchDimensionProvider.mapToLocal(pCoordinate);
			int fieldX = dimension.width;
			int fieldY = dimension.height;
			clear(fieldX, fieldY, pitchDimensionProvider.fieldSquareSize(), pitchDimensionProvider.fieldSquareSize(), pUpdateArea);
		}
	}

	protected synchronized void addUpdatedArea(Rectangle pRectangle) {
		if (fUpdatedArea != null) {
			fUpdatedArea.add(pRectangle);
		} else {
			fUpdatedArea = pRectangle;
		}
	}

	public FantasyFootballClient getClient() {
		return fClient;
	}

	public void init() {
	}
}
