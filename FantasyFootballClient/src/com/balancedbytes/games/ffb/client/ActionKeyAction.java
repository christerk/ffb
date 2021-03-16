package com.balancedbytes.games.ffb.client;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

/**
 * 
 * @author Kalimar
 */
public class ActionKeyAction extends AbstractAction {

	private FantasyFootballClient fClient;
	private ActionKey fActionKey;
	private KeyStroke fKeyStroke;

	public ActionKeyAction(FantasyFootballClient pClient, KeyStroke pKeyStroke, ActionKey pActionKey) {
		fClient = pClient;
		fKeyStroke = pKeyStroke;
		fActionKey = pActionKey;
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

}
