package com.fumbbl.ffb.client.ui.strategies.click;

import java.awt.event.MouseEvent;

import com.fumbbl.ffb.IClientPropertyValue;

@SuppressWarnings("unused")
public class LeftClickAltStrategy implements ClickStrategy {
	@Override
	public boolean applies(MouseEvent event) {
		return event.getButton() == MouseEvent.BUTTON1 && event.isAltDown();
	}

	@Override
	public String getMenuLabel() {
		return "Left Click + Alt";
	}

	@Override
	public int getOrder() {
		return 1; // Alt is first among modifiers
	}

	@Override
	public String getKey() {
		return IClientPropertyValue.SETTING_CLICK_LEFT_ALT;
	}
}
