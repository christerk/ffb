package com.fumbbl.ffb.client.ui.strategies.click;

import java.awt.event.MouseEvent;

import com.fumbbl.ffb.IClientPropertyValue;

@SuppressWarnings("unused")
 public class RightClickStrategy implements ClickStrategy {
	@Override
	public boolean applies(MouseEvent event) {
		return event.getButton() == MouseEvent.BUTTON3;
	}

	@Override
	public String getMenuLabel() {
		return "Right Click";
	}

	@Override
	public int getOrder() {
		return 5; // Right click is last
	}

	@Override
	public String getKey() {
		return IClientPropertyValue.SETTING_CLICK_RIGHT;
	}
}
