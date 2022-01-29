package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;

public class DialogConfirmEndBlitzAction extends DialogYesOrNoQuestion {

	public DialogConfirmEndBlitzAction(FantasyFootballClient pClient) {
		super(pClient, "End Blitz?", createMessages(), null);
	}

	private static String[] createMessages() {
		String[] messages = new String[2];
		messages[0] = "Do you want to end the player action?";
		messages[1] = "Your blitz action will be lost for this turn.";
		return messages;
	}

	public DialogId getId() {
		return DialogId.CONFIRM_END_BLITZ_ACTION;
	}

}
