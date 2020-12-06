package com.balancedbytes.games.ffb.client.layer;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.FieldCoordinateBounds;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;

/**
 * 
 * @author Kalimar
 */
public abstract class FieldLayer {

	public static final int FIELD_SQUARE_SIZE = 30;

	public static final int FIELD_IMAGE_WIDTH = 782;
	public static final int FIELD_IMAGE_HEIGHT = 452;

	public static final int FIELD_IMAGE_OFFSET_CENTER_X = 15;
	public static final int FIELD_IMAGE_OFFSET_CENTER_Y = 15;

	private FantasyFootballClient fClient;
	private BufferedImage fImage;
	private Rectangle fUpdatedArea;

	public FieldLayer(FantasyFootballClient pClient) {
		fClient = pClient;
		fImage = new BufferedImage(FIELD_IMAGE_WIDTH, FIELD_IMAGE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
		addUpdatedArea(new Rectangle(0, 0, fImage.getWidth(), fImage.getHeight()));
	}

	public BufferedImage getImage() {
		return fImage;
	}

	public Rectangle fetchUpdatedArea() {
		Rectangle updatedArea = fUpdatedArea;
		fUpdatedArea = null;
		return updatedArea;
	}

	protected Rectangle draw(BufferedImage pImage, int pX, int pY, float pAlpha) {
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
		return (FIELD_IMAGE_OFFSET_CENTER_X + (pCoordinate.getX() * FIELD_SQUARE_SIZE) - (pImage.getWidth() / 2));
	}

	protected int findCenteredIconUpperLeftY(BufferedImage pImage, FieldCoordinate pCoordinate) {
		return (FIELD_IMAGE_OFFSET_CENTER_Y + (pCoordinate.getY() * FIELD_SQUARE_SIZE) - (pImage.getHeight() / 2));
	}

	public void clear(boolean pUpdateArea) {
		clear(0, 0, getImage().getWidth(), getImage().getHeight(), pUpdateArea);
	}

	public void clear(FieldCoordinate pCoordinate, boolean pUpdateArea) {
		if ((pCoordinate != null) && FieldCoordinateBounds.FIELD.isInBounds(pCoordinate)) {
			int fieldX = pCoordinate.getX() * FIELD_SQUARE_SIZE;
			int fieldY = pCoordinate.getY() * FIELD_SQUARE_SIZE;
			clear(fieldX, fieldY, FIELD_SQUARE_SIZE, FIELD_SQUARE_SIZE, pUpdateArea);
		}
	}

	protected void addUpdatedArea(Rectangle pRectangle) {
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
