package com.fumbbl.ffb.client.layer;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.FontCache;
import com.fumbbl.ffb.client.PitchDimensionProvider;
import com.fumbbl.ffb.client.PitchViewport;
import com.fumbbl.ffb.client.UiDimensionProvider;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
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
	protected final PitchViewport pitchViewport;

	protected final FontCache fontCache;

	public FieldLayer(FantasyFootballClient pClient, UiDimensionProvider uiDimensionProvider,
										PitchDimensionProvider pitchDimensionProvider, PitchViewport pitchViewport,
										FontCache fontCache) {
		fClient = pClient;
		this.uiDimensionProvider = uiDimensionProvider;
		this.pitchDimensionProvider = pitchDimensionProvider;
		this.pitchViewport = pitchViewport;
		this.fontCache = fontCache;
	}

	public void initLayout() {
		size = pitchViewport.fieldSize();
		fImage = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
		addUpdatedArea(new Rectangle(0, 0, fImage.getWidth(), fImage.getHeight()));
	}

	public BufferedImage getImage() {
		return fImage;
	}

	/** Draws world geometry directly into the display-sized layer, without rescaling the layer. */
	protected Graphics2D createWorldGraphics() {
		Graphics2D graphics = fImage.createGraphics();
		graphics.transform(pitchViewport.worldToLocalTransform());
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		return graphics;
	}

	/** Uses the same shape path for opaque fills, translucent fills and clearing. */
	protected void fillWorldSquare(Graphics2D graphics, FieldCoordinate coordinate) {
		// Java2D's optimized rectangle pipelines can round differently by composite.
		graphics.fill(new Path2D.Double(pitchViewport.worldSquare(coordinate)));
	}

	/** Insets are screen pixels, as used by the existing movement and range decorations. */
	protected void fillSquare(Graphics2D graphics, FieldCoordinate coordinate, double inset) {
		Rectangle2D bounds = pitchViewport.localSquare(coordinate);
		Graphics2D fill = (Graphics2D) graphics.create();
		fill.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		fill.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		fill.fill(new Path2D.Double(new Rectangle2D.Double(bounds.getX() + inset, bounds.getY() + inset,
			Math.max(0, bounds.getWidth() - 2 * inset), Math.max(0, bounds.getHeight() - 2 * inset))));
		fill.dispose();
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
		Dimension dimension = pitchViewport.toLocal(pCoordinate, true);
		return dimension.width - (pImage.getWidth() / 2);
	}

	protected int findCenteredIconUpperLeftY(BufferedImage pImage, FieldCoordinate pCoordinate) {
		Dimension dimension = pitchViewport.toLocal(pCoordinate, true);
		return dimension.height - (pImage.getHeight() / 2);
	}

	public void clear(boolean pUpdateArea) {
		clear(0, 0, getImage().getWidth(), getImage().getHeight(), pUpdateArea);
	}

	public void clear(FieldCoordinate pCoordinate, boolean pUpdateArea) {
		if ((pCoordinate != null) && FieldCoordinateBounds.FIELD.isInBounds(pCoordinate)) {
			Graphics2D graphics = createWorldGraphics();
			graphics.setComposite(AlphaComposite.Clear);
			fillWorldSquare(graphics, pCoordinate);
			graphics.dispose();
			if (pUpdateArea) {
				// Outward rounding is for repaint coverage only, never for filling or clearing.
				addUpdatedArea(pitchViewport.localSquare(pCoordinate).getBounds());
			}
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
