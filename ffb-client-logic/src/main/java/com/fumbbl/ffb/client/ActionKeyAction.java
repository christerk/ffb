package com.fumbbl.ffb.client;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import java.awt.event.ActionEvent;

/**
 * @author Kalimar
 */
public class ActionKeyAction extends AbstractAction {

	private final FantasyFootballClient fClient;
	private final ActionKey fActionKey;
	private final KeyStroke fKeyStroke;
	private final int inputMap;

	public ActionKeyAction(FantasyFootballClient pClient, KeyStroke pKeyStroke, ActionKey pActionKey) {
		this(pClient, pKeyStroke, pActionKey, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
	}

	public ActionKeyAction(FantasyFootballClient pClient, KeyStroke pKeyStroke, ActionKey pActionKey, int inputMap) {
		fClient = pClient;
		fKeyStroke = pKeyStroke;
		fActionKey = pActionKey;
		this.inputMap = inputMap;
	}

	public void actionPerformed(ActionEvent pE) {
		getClient().getClientState().actionKeyPressed(getActionKey());
	}

	public FantasyFootballClient getClient() {
		return fClient;
	}

	public ActionKey getActionKey() {
		return fActionKey;
	}

	public KeyStroke getKeyStroke() {
		return fKeyStroke;
	}

	public int getInputMap() {
		return inputMap;
	}
}
