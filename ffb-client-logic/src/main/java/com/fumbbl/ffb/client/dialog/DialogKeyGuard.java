package com.fumbbl.ffb.client.dialog;

import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.KeyEventDispatcher;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

/**
 * Prevents dialogs from being confirmed by key events the user did not intend for them.
 * <p>
 * Two guards are applied to key events targeted at the guarded dialog:
 * <ul>
 * <li>key events that happened before the dialog had been shown for the configured delay are dropped,</li>
 * <li>key releases are dropped unless the matching key press was observed by the same dialog.</li>
 * </ul>
 * Key events targeted at any other component (e.g. the chat input) are never affected.
 */
public class DialogKeyGuard implements KeyEventDispatcher {

	private final Component guardedComponent;
	private final long armedAt;
	private final Set<Integer> pressedKeyCodes = new HashSet<>();

	public DialogKeyGuard(Component guardedComponent, long shownAt, long delayInMillis) {
		this.guardedComponent = guardedComponent;
		this.armedAt = shownAt + Math.max(0, delayInMillis);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent keyEvent) {
		return !isAllowed(keyEvent);
	}

	public boolean isAllowed(KeyEvent keyEvent) {

		if (keyEvent == null || !isTargetingGuardedComponent(keyEvent.getComponent())) {
			return true;
		}

		if (keyEvent.getWhen() < armedAt) {
			return false;
		}

		if (keyEvent.getID() == KeyEvent.KEY_PRESSED) {
			pressedKeyCodes.add(keyEvent.getKeyCode());
			return true;
		}

		if (keyEvent.getID() == KeyEvent.KEY_RELEASED) {
			return pressedKeyCodes.remove(keyEvent.getKeyCode());
		}

		// Remaining events are KEY_TYPED. Those report VK_UNDEFINED as key code, so they cannot be matched
		// against the observed presses individually. A KEY_TYPED is always sandwiched between the press and
		// the release of the key that produced it, so it may only pass while at least one press seen by this
		// dialog is still unreleased. Otherwise it stems from a key that was already pressed before the guard
		// was armed and must be dropped like its press and release.
		return !pressedKeyCodes.isEmpty();
	}

	private boolean isTargetingGuardedComponent(Component component) {
		return component != null && guardedComponent != null
			&& (component == guardedComponent || SwingUtilities.isDescendingFrom(component, guardedComponent));
	}

}
