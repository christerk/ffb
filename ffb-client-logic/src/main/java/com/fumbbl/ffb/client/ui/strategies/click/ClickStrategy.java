package com.fumbbl.ffb.client.ui.strategies.click;

import com.fumbbl.ffb.IKeyedItem;

import java.awt.event.MouseEvent;

public interface ClickStrategy extends IKeyedItem {
	boolean applies(MouseEvent event);

	/**
	 * Returns the text to be used as a menu item label for this strategy.
	 */
	String getMenuLabel();

	/**
	 * Returns the order for menu sorting. Left click is first (0), right click last (4),
	 * modifier-based left clicks are ordered alphabetically by modifier: Alt (1), Ctrl (2), Shift (3).
	 */
	int getOrder();

}
