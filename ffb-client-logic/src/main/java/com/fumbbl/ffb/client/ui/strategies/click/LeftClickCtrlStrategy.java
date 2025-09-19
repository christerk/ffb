package com.fumbbl.ffb.client.ui.strategies.click;

import java.awt.event.MouseEvent;

public class LeftClickCtrlStrategy implements ClickStrategy {
    @Override
    public boolean applies(MouseEvent event) {
        return event.getButton() == MouseEvent.BUTTON1 && event.isControlDown();
    }

    @Override
    public String getMenuLabel() {
        return "Left Click + Ctrl";
    }
}
