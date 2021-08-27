package com.fumbbl.ffb.client.ui;

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneLayout;

/**
 * 
 * @author Kalimar
 */
public class ChatLogScrollPane extends JScrollPane implements AdjustmentListener, ComponentListener {

	private int fOldVisibleMaximum;

	public ChatLogScrollPane(ChatLogTextPane pTextPane) {
		super(pTextPane);
		setHorizontalScrollBarPolicy(ScrollPaneLayout.HORIZONTAL_SCROLLBAR_NEVER);
		setVerticalScrollBarPolicy(ScrollPaneLayout.VERTICAL_SCROLLBAR_AS_NEEDED);
		getVerticalScrollBar().addAdjustmentListener(this);
		addComponentListener(this);
	}

	public void adjustmentValueChanged(AdjustmentEvent pE) {
		JScrollBar scrollBar = (JScrollBar) pE.getSource();
		if (!pE.getValueIsAdjusting()) {
			int visibleMaximum = findVisibleMaximum(scrollBar);
			if (visibleMaximum > fOldVisibleMaximum) {
				if ((scrollBar.getValue() - fOldVisibleMaximum) < 2) {
					scrollBar.setValue(visibleMaximum);
				}
				fOldVisibleMaximum = visibleMaximum;
			}
		}
	}

	private int findVisibleMaximum(JScrollBar pScrollBar) {
		return (pScrollBar.getMaximum() - pScrollBar.getVisibleAmount());
	}

	public void componentResized(ComponentEvent pE) {
		fOldVisibleMaximum = findVisibleMaximum(getVerticalScrollBar());
	}

	public void componentHidden(ComponentEvent pE) {
	}

	public void componentMoved(ComponentEvent pE) {
	}

	public void componentShown(ComponentEvent pE) {
		fOldVisibleMaximum = findVisibleMaximum(getVerticalScrollBar());
	}

	public void setScrollBarToMaximum() {
		getVerticalScrollBar().setValue(findVisibleMaximum(getVerticalScrollBar()));
	}

	public void setScrollBarToMinimum() {
		getVerticalScrollBar().setValue(getVerticalScrollBar().getMinimum());
	}

}
