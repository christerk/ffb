package com.fumbbl.ffb.client.ui.strategies.click;

import java.awt.event.MouseEvent;

public class DoubleClickStrategy implements ClickStrategy {
	@Override
	public boolean applies(MouseEvent event) {
		return event.getClickCount() == 2;
	}

	@Override
	public String getMenuLabel() {
		return "Double Left Click";
	}

	@Override
	public int getOrder() {
		return 4;
	}
}

