package com.balancedbytes.games.ffb.client;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;

/**
 * 
 * @author Kalimar
 */
@SuppressWarnings("serial")
public class ActionKeyMultiAction extends AbstractAction {

	private ActionKey fActionKey;
	private List<ActionKeyAction> fActionKeyActions;

	public ActionKeyMultiAction(ActionKey pActionKey) {
		fActionKeyActions = new ArrayList<>();
		fActionKey = pActionKey;
	}

	public ActionKey getActionKey() {
		return fActionKey;
	}

	public void actionPerformed(ActionEvent pE) {
		Iterator<ActionKeyAction> actionKeyActionIterator = fActionKeyActions.iterator();
		while (actionKeyActionIterator.hasNext()) {
			ActionKeyAction actionKeyAction = actionKeyActionIterator.next();
			actionKeyAction.actionPerformed(pE);
		}
	}

	public void add(ActionKeyAction pActionKeyAction) {
		fActionKeyActions.add(pActionKeyAction);
	}

	public ActionKeyAction[] getActions() {
		return fActionKeyActions.toArray(new ActionKeyAction[fActionKeyActions.size()]);
	}

}
