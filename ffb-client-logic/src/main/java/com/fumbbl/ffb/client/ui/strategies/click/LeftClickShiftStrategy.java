package com.fumbbl.ffb.client.ui.strategies.click;

import java.awt.event.MouseEvent;

@SuppressWarnings("unused")
public class LeftClickShiftStrategy implements ClickStrategy {
    @Override
    public boolean applies(MouseEvent event) {
        return event.getButton() == MouseEvent.BUTTON1 && event.isShiftDown();
    }

    @Override
    public String getMenuLabel() {
        return "Left Click + Shift";
    }

    @Override
    public int getOrder() {
        return 3; // Shift is third among modifiers
    }
}
