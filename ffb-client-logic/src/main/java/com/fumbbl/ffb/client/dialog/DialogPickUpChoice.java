package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;

public class DialogPickUpChoice extends DialogThreeWayChoice {

	public DialogPickUpChoice(FantasyFootballClient client) {
		super(client, "Pick Up Choice", "Attempt to pick up the ball?");
	}

	@Override
	public DialogId getId() {
		return DialogId.PICK_UP_CHOICE;
	}
}
