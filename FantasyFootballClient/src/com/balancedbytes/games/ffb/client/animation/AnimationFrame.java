package com.balancedbytes.games.ffb.client.animation;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.Sound;
import com.balancedbytes.games.ffb.client.IconCache;
import com.balancedbytes.games.ffb.client.layer.FieldLayer;
import com.balancedbytes.games.ffb.util.StringTool;

/**
 * 
 * @author Kalimar
 */
public class AnimationFrame {
	
	private String fIconProperty1;
	private float fAlpha1;
	private double fScaleX1;
  private double fScaleY1;
	private BufferedImage fIcon1;
	private String fIconProperty2;
	private float fAlpha2;
	private double fScaleX2;
  private double fScaleY2;
	private BufferedImage fIcon2;
	private int fTime;
	private Sound fSound;
	
	private FieldLayer fFieldLayer;
	private Rectangle fUpdatedArea;

	public AnimationFrame(String pIconProperty, float pAlpha, int pTime) {
		this(pIconProperty, pAlpha, 1.0, 1.0, null, 1.0f, 1.0, 1.0, pTime, null);
	}

	public AnimationFrame(String pIconProperty, float pAlpha, int pTime, Sound pSound) {
		this(pIconProperty, pAlpha, 1.0, 1.0, null, 1.0f, 1.0, 1.0, pTime, pSound);
	}

	public AnimationFrame(String pIconProperty, float pAlpha, double pScale, int pTime) {
		this(pIconProperty, pAlpha, pScale, pScale, null, 1.0f, 1.0, 1.0, pTime, null);
	}

	public AnimationFrame(String pIconProperty, float pAlpha, double pScale, int pTime, Sound pSound) {
		this(pIconProperty, pAlpha, pScale, pScale, null, 1.0f, 1.0, 1.0, pTime, pSound);
	}

	public AnimationFrame(String pIconProperty1, float pAlpha1, String pIconProperty2, float pAlpha2, int pTime) {
		this(pIconProperty1, pAlpha1, 1.0, 1.0, pIconProperty2, pAlpha2, 1.0, 1.0, pTime, null);
	}

	public AnimationFrame(String pIconProperty1, float pAlpha1, String pIconProperty2, float pAlpha2, int pTime, Sound pSound) {
		this(pIconProperty1, pAlpha1, 1.0, 1.0, pIconProperty2, pAlpha2, 1.0, 1.0, pTime, pSound);
	}

  public AnimationFrame(String pIconProperty1, float pAlpha1, double pScale1, String pIconProperty2, float pAlpha2, double pScale2, int pTime, Sound pSound) {
    this(pIconProperty1, pAlpha1, pScale1, pScale1, pIconProperty2, pAlpha2, pScale2, pScale2, pTime, pSound);
  }

	public AnimationFrame(String pIconProperty1, float pAlpha1, double pScaleX1, double pScaleY1, String pIconProperty2, float pAlpha2, double pScaleX2, double pScaleY2, int pTime, Sound pSound) {
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
	
	public int getTime() {
		return fTime;
	}
	
	public Sound getSound() {
		return fSound;
	}

	public void drawCenteredAndScaled(FieldLayer pFieldLayer, int pX, int pY) {
		fFieldLayer = pFieldLayer;
		getIcons();		
		if (fIcon1 != null) {
			addUpdatedArea(
				fFieldLayer.drawCenteredAndScaled(fIcon1, pX, pY, fAlpha1, fScaleX1, fScaleY1)
			);
		}
		if (fIcon2 != null) {
			addUpdatedArea(
				fFieldLayer.drawCenteredAndScaled(fIcon2, pX, pY, fAlpha2, fScaleX2, fScaleY2)
			);
		}
	}

	public void draw(FieldLayer pFieldLayer, FieldCoordinate pCoordinate) {
		fFieldLayer = pFieldLayer;
		getIcons();		
		if (fIcon1 != null) {
			addUpdatedArea(
				fFieldLayer.draw(fIcon1, pCoordinate, fAlpha1)
			);
		}
		if (fIcon2 != null) {
			addUpdatedArea(
				fFieldLayer.draw(fIcon2, pCoordinate, fAlpha2)
			);
		}
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
			fIcon1 = iconCache.getIconByProperty(fIconProperty1);
		}
		if (StringTool.isProvided(fIconProperty2)) {
			fIcon2 = iconCache.getIconByProperty(fIconProperty2);
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
