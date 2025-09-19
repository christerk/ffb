package com.fumbbl.ffb.client.ui.strategies.click;

import java.awt.event.MouseEvent;

public class LeftClickNoModifierStrategy implements ClickStrategy {
	@Override
	public boolean applies(MouseEvent event) {
		return event.getButton() == MouseEvent.BUTTON1
			&& !event.isAltDown() && !event.isControlDown() && !event.isShiftDown();
	}

	@Override
	public String getMenuLabel() {
		return "Left Click";
	}
}
