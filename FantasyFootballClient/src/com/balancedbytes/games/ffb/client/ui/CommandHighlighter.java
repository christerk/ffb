package com.balancedbytes.games.ffb.client.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Shape;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;

public class CommandHighlighter extends DefaultHighlighter implements Highlighter.HighlightPainter {

	private Object fHighlight;
	private JTextComponent fTextComponent;
	private Rectangle fLastUpdatedArea;

	public void changeHighlight(int pP0, int pP1) throws BadLocationException {
		if (fHighlight == null) {
			try {
				// fHighlight = getHighlighter().addHighlight(0, 0, new
				// DefaultHighlighter.DefaultHighlightPainter(Color.LIGHT_GRAY));
				fHighlight = addHighlight(0, 0, this);
			} catch (BadLocationException ble) {
				ble.printStackTrace();
			}
		}
		super.changeHighlight(fHighlight, pP0, pP1);
		repaintLastUpdatedArea();
	}

	public void paint(Graphics pGraphics, int pP0, int pP1, Shape pBounds, JTextComponent pTextComponent) {
		try {
			fTextComponent = pTextComponent;
			pGraphics.setColor(Color.LIGHT_GRAY);
			Rectangle leftUpperCorner = pTextComponent.modelToView(pP0);
			Rectangle rightLowerCorner = pTextComponent.modelToView(pP1);
			Insets insets = pTextComponent.getInsets();
			// pG.fillRect(insets.left, leftUpperCorner.y + insets.top, pC.getWidth()
			// - insets.left - insets.right, rightLowerCorner.y - leftUpperCorner.y -
			// insets.bottom - insets.top);
			Rectangle updatedArea = new Rectangle(insets.left, leftUpperCorner.y,
					pTextComponent.getWidth() - insets.left - insets.right, rightLowerCorner.y - leftUpperCorner.y);
			pGraphics.fillRect(updatedArea.x, updatedArea.y, updatedArea.width, updatedArea.height);
			if (fLastUpdatedArea == null) {
				fLastUpdatedArea = updatedArea;
			} else {
				fLastUpdatedArea.add(updatedArea);
			}
		} catch (BadLocationException ble) {
			ble.printStackTrace();
		}
	}

	public void repaintLastUpdatedArea() {
		if ((fTextComponent != null) && (fLastUpdatedArea != null)) {
			fTextComponent.repaint(fLastUpdatedArea);
			fLastUpdatedArea = null;
		}
	}

}
