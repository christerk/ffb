package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;

public class DialogConfirmSkipPlayerChoice extends DialogThreeWayChoice {

	public DialogConfirmSkipPlayerChoice(FantasyFootballClient client, String header) {
		super(client, "Skip Player Choice?", new String[]{"Do you want to skip this choice?", header}, null);
	}

	public DialogId getId() {
		return DialogId.YES_OR_NO_QUESTION;
	}
}
