package com.fumbbl.ffb.client.animation;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.client.IconCache;
import com.fumbbl.ffb.client.RenderContext;
import com.fumbbl.ffb.client.layer.FieldLayer;
import com.fumbbl.ffb.util.StringTool;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author Kalimar
 */
public class AnimationFrame {

	private final String fIconProperty1;
	private final float fAlpha1;
	private final double fScaleX1;
	private final double fScaleY1;
	private BufferedImage fIcon1;
	private String fIconProperty2;
	private final float fAlpha2;
	private final double fScaleX2;
	private final double fScaleY2;
	private BufferedImage fIcon2;
	private final int fTime;
	private final SoundId fSound;

	private FieldLayer fFieldLayer;
	private Rectangle fUpdatedArea;

	public AnimationFrame(String pIconProperty, float pAlpha, int pTime) {
		this(pIconProperty, pAlpha, 1.0, 1.0, (String) null, 1.0f, 1.0, 1.0, pTime, null);
	}

	public AnimationFrame(String pIconProperty, float pAlpha, int pTime, SoundId pSound) {
		this(pIconProperty, pAlpha, 1.0, 1.0, (String) null, 1.0f, 1.0, 1.0, pTime, pSound);
	}

	public AnimationFrame(String pIconProperty, float pAlpha, double pScale, int pTime) {
		this(pIconProperty, pAlpha, pScale, pScale, (String) null, 1.0f, 1.0, 1.0, pTime, null);
	}

	public AnimationFrame(String pIconProperty, float pAlpha, double pScale, int pTime, SoundId pSound) {
		this(pIconProperty, pAlpha, pScale, pScale, (String) null, 1.0f, 1.0, 1.0, pTime, pSound);
	}

	public AnimationFrame(String pIconProperty, float pAlpha, double pScaleX, double pScaleY, int pTime) {
		this(pIconProperty, pAlpha, pScaleX, pScaleY, (String) null, 1.0f, 1.0, 1.0, pTime, null);
	}

	public AnimationFrame(BufferedImage pIcon, float pAlpha, double pScaleX, double pScaleY, int pTime) {
		fIcon1 = pIcon;
		fIconProperty1 = null;
		fAlpha1 = pAlpha;
		fScaleX1 = pScaleX;
		fScaleY1 = pScaleY;
		fIconProperty2 = null;
		fAlpha2 = 1.0f;
		fScaleX2 = 1.0;
		fScaleY2 = 1.0;
		fTime = pTime;
		fSound = null;
	}

	public AnimationFrame(String pIconProperty1, float pAlpha1, String pIconProperty2, float pAlpha2, int pTime) {
		this(pIconProperty1, pAlpha1, 1.0, 1.0, pIconProperty2, pAlpha2, 1.0, 1.0, pTime, null);
	}

	public AnimationFrame(String pIconProperty1, float pAlpha1, double pScaleX1, double pScaleY1, String pIconProperty2,
												float pAlpha2, double pScaleX2, double pScaleY2, int pTime, SoundId pSound) {
		fIconProperty1 = pIconProperty1;
		fAlpha1 = pAlpha1;
		fScaleX1 = pScaleX1;
		fScaleY1 = pScaleY1;
		fIconProperty2 = pIconProperty2;
		fAlpha2 = pAlpha2;
		fScaleX2 = pScaleX2;
		fScaleY2 = pScaleY2;
		fTime = pTime;
		fSound = pSound;
	}

	public AnimationFrame(String pIconProperty1, float pAlpha1, double scale1, BufferedImage icon2,
												float pAlpha2, double scale2, int pTime) {
		this(pIconProperty1, pAlpha1, scale1, scale1, icon2, pAlpha2, scale2, scale2, pTime, null);
	}

	public AnimationFrame(String pIconProperty1, float pAlpha1, double scale1, BufferedImage icon2,
												float pAlpha2, double scale2, int pTime, SoundId pSound) {
		this(pIconProperty1, pAlpha1, scale1, scale1, icon2, pAlpha2, scale2, scale2, pTime, pSound);
	}

	public AnimationFrame(String pIconProperty1, float pAlpha1, double pScaleX1, double pScaleY1, BufferedImage icon2,
												float pAlpha2, double pScaleX2, double pScaleY2, int pTime, SoundId pSound) {
		fIconProperty1 = pIconProperty1;
		fAlpha1 = pAlpha1;
		fScaleX1 = pScaleX1;
		fScaleY1 = pScaleY1;
		fIcon2 = icon2;
		fAlpha2 = pAlpha2;
		fScaleX2 = pScaleX2;
		fScaleY2 = pScaleY2;
		fTime = pTime;
		fSound = pSound;
	}

	public int getTime() {
		return fTime;
	}

	public SoundId getSound() {
		return fSound;
	}

	public void drawCenteredAndScaled(FieldLayer pFieldLayer, int pX, int pY) {
		fFieldLayer = pFieldLayer;
		getIcons();
		if (fIcon1 != null) {
			addUpdatedArea(fFieldLayer.drawCenteredAndScaled(fIcon1, pX, pY, fAlpha1, fScaleX1, fScaleY1));
		}
		if (fIcon2 != null) {
			addUpdatedArea(fFieldLayer.drawCenteredAndScaled(fIcon2, pX, pY, fAlpha2, fScaleX2, fScaleY2));
		}
	}

	public void draw(FieldLayer pFieldLayer, FieldCoordinate pCoordinate) {
		fFieldLayer = pFieldLayer;
		getIcons();
		if (fIcon1 != null) {
			addUpdatedArea(fFieldLayer.draw(fIcon1, pCoordinate, fAlpha1));
		}
		if (fIcon2 != null) {
			addUpdatedArea(fFieldLayer.draw(fIcon2, pCoordinate, fAlpha2));
		}
	}

	public void draw(FieldLayer pFieldLayer, Dimension dimension, double scale) {
		fFieldLayer = pFieldLayer;
		getIcons();
		if (fIcon1 != null) {
			addUpdatedArea(fFieldLayer.drawCenteredAndScaled(fIcon1, dimension.width, dimension.height, fAlpha1, scale, scale));
		}
		if (fIcon2 != null) {
			addUpdatedArea(fFieldLayer.drawCenteredAndScaled(fIcon2, dimension.width, dimension.height, fAlpha2, scale, scale));
		}
		fFieldLayer.getClient().getUserInterface().getFieldComponent().refresh();
	}

	public void clear() {
		if ((fFieldLayer != null) && (fUpdatedArea != null)) {
			fFieldLayer.clear(fUpdatedArea, true);
			fUpdatedArea = null;
		}
	}

	private void getIcons() {
		IconCache iconCache = fFieldLayer.getClient().getUserInterface().getIconCache();
		if (StringTool.isProvided(fIconProperty1)) {
			fIcon1 = iconCache.getIconByProperty(fIconProperty1, RenderContext.ON_PITCH);
		}
		if (StringTool.isProvided(fIconProperty2)) {
			fIcon2 = iconCache.getIconByProperty(fIconProperty2, RenderContext.ON_PITCH);
		}
	}

	private void addUpdatedArea(Rectangle pRectangle) {
		if (fUpdatedArea != null) {
			fUpdatedArea.add(pRectangle);
		} else {
			fUpdatedArea = pRectangle;
		}
	}

}
