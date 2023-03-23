package com.fumbbl.ffb.client.layer;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.FantasyFootballClient;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
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

	protected DimensionProvider dimensionProvider;

	public FieldLayer(FantasyFootballClient pClient, DimensionProvider dimensionProvider) {
		fClient = pClient;
		this.dimensionProvider = dimensionProvider;
	}

	public void initLayout(DimensionProvider dimensionProvider) {
		size = dimensionProvider.dimension(DimensionProvider.Component.FIELD);
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
			int width = (int) (pImage.getWidth() * pScaleX * dimensionProvider.getScale());
			int height = (int) (pImage.getHeight() * pScaleY * dimensionProvider.getScale());
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

			BufferedImage scaledImage = scaleImage(pImage);

			return draw(scaledImage, findCenteredIconUpperLeftX(scaledImage, pCoordinate),
				findCenteredIconUpperLeftY(scaledImage, pCoordinate), pAlpha);
		}
		return null;
	}

	private BufferedImage scaleImage(BufferedImage pImage) {
		BufferedImage scaledImage = new BufferedImage(dimensionProvider.scale(pImage.getWidth()), dimensionProvider.scale(pImage.getHeight()), BufferedImage.TYPE_INT_ARGB);
		AffineTransform at = new AffineTransform();
		at.scale(dimensionProvider.getScale(), dimensionProvider.getScale());
		AffineTransformOp scaleOp =
			new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);

		scaledImage = scaleOp.filter(pImage, scaledImage);
		return scaledImage;
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
			BufferedImage scaledImage = scaleImage(pImage);

			clear(findCenteredIconUpperLeftX(scaledImage, pCoordinate), findCenteredIconUpperLeftY(scaledImage, pCoordinate),
				scaledImage.getWidth(), scaledImage.getHeight(), pUpdateArea);
		}
	}

	protected int findCenteredIconUpperLeftX(BufferedImage pImage, FieldCoordinate pCoordinate) {
		Dimension dimension = dimensionProvider.mapToLocal(pCoordinate, true);
		return dimension.width - (pImage.getWidth() / 2);
	}

	protected int findCenteredIconUpperLeftY(BufferedImage pImage, FieldCoordinate pCoordinate) {
		Dimension dimension = dimensionProvider.mapToLocal(pCoordinate, true);
		return dimension.height - (pImage.getHeight() / 2);
	}

	public void clear(boolean pUpdateArea) {
		clear(0, 0, getImage().getWidth(), getImage().getHeight(), pUpdateArea);
	}

	public void clear(FieldCoordinate pCoordinate, boolean pUpdateArea) {
		if ((pCoordinate != null) && FieldCoordinateBounds.FIELD.isInBounds(pCoordinate)) {
			Dimension dimension = dimensionProvider.mapToLocal(pCoordinate);
			int fieldX = dimension.width;
			int fieldY = dimension.height;
			clear(fieldX, fieldY, dimensionProvider.fieldSquareSize(), dimensionProvider.fieldSquareSize(), pUpdateArea);
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
