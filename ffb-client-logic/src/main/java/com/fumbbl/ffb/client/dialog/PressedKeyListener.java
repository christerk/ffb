package com.fumbbl.ffb.client.dialog;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

abstract class PressedKeyListener implements KeyListener {

	private final int keyCode;

	protected PressedKeyListener(int keyCode) {
		this.keyCode = keyCode;
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == keyCode) {
			handleKey();
		}
	}

	protected abstract void handleKey();

	@Override
	public void keyReleased(KeyEvent e) {

	}
}
