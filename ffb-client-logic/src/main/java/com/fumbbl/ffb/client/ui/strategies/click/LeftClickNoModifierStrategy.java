package com.fumbbl.ffb.client.ui.strategies.click;

import java.awt.event.MouseEvent;

import com.fumbbl.ffb.IClientPropertyValue;

@SuppressWarnings("unused")
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

	@Override
	public int getOrder() {
		return 0; // Left click is first
	}

	@Override
	public String getKey() {
		return IClientPropertyValue.SETTING_CLICK_LEFT;
	}
}
