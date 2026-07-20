package com.fumbbl.ffb.client.dialog.inducements;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.dialog.DialogThreeWayChoice;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.util.StringTool;

/**
 * Confirmation dialog shown when the coach tries to close the buy inducements dialog while still having enough petty
 * cash left to buy another inducement.
 */
public class DialogUnspentPettyCash extends DialogThreeWayChoice {

	public DialogUnspentPettyCash(FantasyFootballClient pClient, int remainingPettyCash) {
		super(pClient, "Unspent Petty Cash", createMessages(remainingPettyCash), null);
	}

	private static String[] createMessages(int remainingPettyCash) {
		return new String[]{
			"You still have " + StringTool.formatThousands(remainingPettyCash) + " gp of petty cash left to spend on inducements.",
			"Are you sure you don't want to spend it?"
		};
	}

	public DialogId getId() {
		return DialogId.YES_OR_NO_QUESTION;
	}
}
