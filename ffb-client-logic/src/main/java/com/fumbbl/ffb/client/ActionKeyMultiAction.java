package com.fumbbl.ffb.client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Kalimar
 */
public class ActionKeyMultiAction extends AbstractAction {

	private final List<ActionKeyAction> fActionKeyActions;

	public ActionKeyMultiAction() {
		fActionKeyActions = new ArrayList<>();
	}

	public void actionPerformed(ActionEvent pE) {
		for (ActionKeyAction actionKeyAction : fActionKeyActions) {
			actionKeyAction.actionPerformed(pE);
		}
	}

	public void add(ActionKeyAction pActionKeyAction) {
		fActionKeyActions.add(pActionKeyAction);
	}

	public ActionKeyAction[] getActions() {
		return fActionKeyActions.toArray(new ActionKeyAction[0]);
	}

}
