package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;

public class DialogKickOffResult extends DialogThreeWayChoice {
	public DialogKickOffResult(FantasyFootballClient pClient) {
		super(pClient, "Choose kick-off result", new String[]{"Choose the kick-off result"}, null, "Blitz!", 'B', "Solid Defence", 'S');
	}

	public DialogId getId() {
		return DialogId.KICK_OFF_RESULT;
	}


}
