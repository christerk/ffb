package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;

/**
 * @author Kalimar
 */
public class DialogBloodlustAction extends DialogThreeWayChoice {

	public DialogBloodlustAction(FantasyFootballClient pClient, boolean changeToMove) {
		super(pClient, "Bloodlust", changeToMove ? "Do you want to perform a move action instead?" : "Do you want to move before performing the action?");
	}

	public DialogId getId() {
		return DialogId.BLOODLUST_ACTION;
	}

}
