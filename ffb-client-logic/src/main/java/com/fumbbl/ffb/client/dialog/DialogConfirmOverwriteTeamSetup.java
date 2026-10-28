package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;

public class DialogConfirmOverwriteTeamSetup extends DialogThreeWayChoice {

	public DialogConfirmOverwriteTeamSetup(FantasyFootballClient client, String setupName) {
		super(client, "Overwrite Team Setup?",
			new String[]{"A setup named \"" + setupName + "\" already exists.", "Do you want to overwrite it?"}, null);
	}

	public DialogId getId() {
		return DialogId.YES_OR_NO_QUESTION;
	}
}
