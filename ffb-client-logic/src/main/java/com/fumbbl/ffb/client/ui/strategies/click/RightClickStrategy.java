package com.fumbbl.ffb.client.ui.strategies.click;

import java.awt.event.MouseEvent;

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
		return 4; // Right click is last
	}
}
