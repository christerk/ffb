package com.fumbbl.ffb.client.ui.strategies.click;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

public class RightClickStrategy implements ClickStrategy {
	@Override
	public boolean applies(ActionEvent event) {
		if (event.getSource() instanceof MouseEvent) {
			MouseEvent me = (MouseEvent) event.getSource();
			return me.getButton() == MouseEvent.BUTTON3;
		}
		return false;
	}

	@Override
	public String getMenuLabel() {
		return "Right Click";
	}
}
