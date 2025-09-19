package com.fumbbl.ffb.client.ui.strategies.click;

import java.awt.event.MouseEvent;

public interface ClickStrategy {
	boolean applies(MouseEvent event);

	/**
	 * Returns the text to be used as a menu item label for this strategy.
	 */
	String getMenuLabel();
}
