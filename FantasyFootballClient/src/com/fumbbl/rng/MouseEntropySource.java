package com.fumbbl.rng;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

/**
 * 
 * @author Christer Kaivo-oja
 * @author Kalimar
 */
public class MouseEntropySource implements EntropySource {

  public static long MIN_UPDATE_PERIOD_MS = 10;
	
	private byte fData;
	private long fLastPositionUpdate;
	private int fBits;
	private int fLastX;
	private int fLastY;
	private Component fTargetComponent;
	
	public MouseEntropySource(Component pTargetComponent) {
    fTargetComponent = pTargetComponent;
  }
	
	public Component getTargetComponent() {
    return fTargetComponent;
  }
	
	private void reportMousePosition(int x, int y) {
		if ((fLastX==x && fLastY == y) || System.currentTimeMillis() - fLastPositionUpdate < MIN_UPDATE_PERIOD_MS) {
		  return;
		}
    // System.out.println("MousePosition("+ x + "," + y + ")");
		int b = (x & 0x3) ^ (y & 0x3);
		fData = (byte) ((fData << 2) | b);
		fBits += 2;
		fLastPositionUpdate = System.currentTimeMillis();
		fLastX = x;
		fLastY = y;
	}
	
	public synchronized void reportMousePosition(MouseEvent pMouseEvent) {
	  if (pMouseEvent != null) {
  	  Point convertedMousePoint = SwingUtilities.convertPoint((Component) pMouseEvent.getSource(), pMouseEvent.getPoint(), fTargetComponent);
  	  reportMousePosition(convertedMousePoint.x, convertedMousePoint.y);
	  }
	}
	
	public boolean hasEnoughEntropy() {
		return fBits >= 8;
	}
	
	public byte getEntropy() {
		fBits = 0;
		return fData;
	}
}
