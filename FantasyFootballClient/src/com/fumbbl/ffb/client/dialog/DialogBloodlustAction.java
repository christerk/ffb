package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kalimar
 */
public class DialogBloodlustAction extends DialogThreeWayChoice {

	public DialogBloodlustAction(FantasyFootballClient pClient, boolean changeToMove, boolean hasReceiver) {
		super(pClient, "Bloodlust", messages(changeToMove, hasReceiver), null);
	}

	public DialogId getId() {
		return DialogId.BLOODLUST_ACTION;
	}


	private static String[] messages(boolean changeToMove, boolean hasReceiver) {
		List<String> messages = new ArrayList<>();
		messages.add(changeToMove ? "Do you want to perform a move action instead?" : "Do you want to move before performing the action?");
		if (hasReceiver) {
			messages.add("If you bite the receiver the ball will bounce causing a turnover (unless caught by another team member)");
		}

		return messages.toArray(new String[0]);
	}
}
