package com.fumbbl.ffb.client.ui.strategies.click;

import java.awt.event.ActionEvent;

public interface ClickStrategy {
	boolean applies(ActionEvent event);

	/**
	 * Returns the text to be used as a menu item label for this strategy.
	 */
	String getMenuLabel();
}
