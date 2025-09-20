package com.fumbbl.ffb.client.ui.strategies.click;

import com.fumbbl.ffb.IClientPropertyValue;

import java.awt.event.MouseEvent;

@SuppressWarnings("unused")
public class LeftClickCtrlStrategy implements ClickStrategy {
    @Override
    public boolean applies(MouseEvent event) {
        return event.getButton() == MouseEvent.BUTTON1 && event.isControlDown();
    }

    @Override
    public String getMenuLabel() {
        return "Left Click + Ctrl";
    }

    @Override
    public int getOrder() {
        return 2; // Ctrl is second among modifiers
    }

    @Override
    public String getKey() {
        return IClientPropertyValue.SETTING_CLICK_LEFT_CTRL;
    }
}
