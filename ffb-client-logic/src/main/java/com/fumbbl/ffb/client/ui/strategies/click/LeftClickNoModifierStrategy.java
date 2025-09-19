package com.fumbbl.ffb.client.ui.strategies.click;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

public class LeftClickNoModifierStrategy implements ClickStrategy {
	@Override
	public boolean applies(ActionEvent event) {
		if (event.getSource() instanceof MouseEvent) {
			MouseEvent me = (MouseEvent) event.getSource();
			int mods = me.getModifiersEx();
			return me.getButton() == MouseEvent.BUTTON1 &&
				((mods & (InputEvent.SHIFT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK)) == 0);
		}
		return false;
	}

	@Override
	public String getMenuLabel() {
		return "Left Click";
	}
}
