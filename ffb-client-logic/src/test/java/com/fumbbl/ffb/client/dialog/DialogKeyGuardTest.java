package com.fumbbl.ffb.client.dialog;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.Component;
import java.awt.event.KeyEvent;

class DialogKeyGuardTest {

	private static final long SHOWN_AT = 10_000L;
	private static final long DELAY = 250L;

	private JPanel dialog;
	private JButton button;
	private JButton otherComponent;
	private DialogKeyGuard guard;

	@BeforeEach
	void setUp() {
		dialog = new JPanel();
		button = new JButton();
		dialog.add(button);
		otherComponent = new JButton();
		guard = new DialogKeyGuard(dialog, SHOWN_AT, DELAY);
	}

	@Test
	void allowsPairedPressAndReleaseAfterDelay() {
		Assertions.assertTrue(guard.isAllowed(keyEvent(button, KeyEvent.KEY_PRESSED, SHOWN_AT + DELAY, KeyEvent.VK_N)));
		Assertions.assertTrue(guard.isAllowed(keyEvent(button, KeyEvent.KEY_RELEASED, SHOWN_AT + DELAY + 50, KeyEvent.VK_N)));
	}

	@Test
	void blocksReleaseWithoutPress() {
		Assertions.assertFalse(guard.isAllowed(keyEvent(button, KeyEvent.KEY_RELEASED, SHOWN_AT + DELAY + 50, KeyEvent.VK_N)));
	}

	@Test
	void blocksEventsDuringDelay() {
		Assertions.assertFalse(guard.isAllowed(keyEvent(button, KeyEvent.KEY_PRESSED, SHOWN_AT + DELAY - 1, KeyEvent.VK_N)));
		Assertions.assertFalse(guard.isAllowed(keyEvent(button, KeyEvent.KEY_RELEASED, SHOWN_AT + DELAY + 50, KeyEvent.VK_N)));
	}

	@Test
	void blocksReleaseOfKeyPressedBeforeDialogWasShown() {
		Assertions.assertFalse(guard.isAllowed(keyEvent(button, KeyEvent.KEY_RELEASED, SHOWN_AT - 10, KeyEvent.VK_N)));
	}

	@Test
	void blocksTypedEventWithoutPress() {
		Assertions.assertFalse(guard.isAllowed(keyEvent(button, KeyEvent.KEY_TYPED, SHOWN_AT + DELAY + 50, KeyEvent.VK_UNDEFINED)));
	}

	@Test
	void allowsTypedEventWhileKeyIsPressed() {
		Assertions.assertTrue(guard.isAllowed(keyEvent(button, KeyEvent.KEY_PRESSED, SHOWN_AT + DELAY, KeyEvent.VK_N)));
		Assertions.assertTrue(guard.isAllowed(keyEvent(button, KeyEvent.KEY_TYPED, SHOWN_AT + DELAY + 10, KeyEvent.VK_UNDEFINED)));
	}

	@Test
	void ignoresEventsOfOtherComponents() {
		Assertions.assertTrue(guard.isAllowed(keyEvent(otherComponent, KeyEvent.KEY_RELEASED, SHOWN_AT, KeyEvent.VK_N)));
		Assertions.assertTrue(guard.isAllowed(keyEvent(otherComponent, KeyEvent.KEY_PRESSED, SHOWN_AT - 100, KeyEvent.VK_N)));
	}

	@Test
	void dispatchKeyEventConsumesBlockedEvents() {
		Assertions.assertTrue(guard.dispatchKeyEvent(keyEvent(button, KeyEvent.KEY_RELEASED, SHOWN_AT + DELAY + 50, KeyEvent.VK_N)));
		Assertions.assertFalse(guard.dispatchKeyEvent(keyEvent(button, KeyEvent.KEY_PRESSED, SHOWN_AT + DELAY + 50, KeyEvent.VK_N)));
	}

	@Test
	void zeroDelayOnlyPairsPressAndRelease() {
		DialogKeyGuard noDelayGuard = new DialogKeyGuard(dialog, SHOWN_AT, 0);
		Assertions.assertFalse(noDelayGuard.isAllowed(keyEvent(button, KeyEvent.KEY_RELEASED, SHOWN_AT, KeyEvent.VK_N)));
		Assertions.assertTrue(noDelayGuard.isAllowed(keyEvent(button, KeyEvent.KEY_PRESSED, SHOWN_AT, KeyEvent.VK_N)));
		Assertions.assertTrue(noDelayGuard.isAllowed(keyEvent(button, KeyEvent.KEY_RELEASED, SHOWN_AT, KeyEvent.VK_N)));
	}

	private KeyEvent keyEvent(Component source, int id, long when, int keyCode) {
		char keyChar = id == KeyEvent.KEY_TYPED ? 'n' : KeyEvent.CHAR_UNDEFINED;
		return new KeyEvent(source, id, when, 0, id == KeyEvent.KEY_TYPED ? KeyEvent.VK_UNDEFINED : keyCode, keyChar);
	}

}
