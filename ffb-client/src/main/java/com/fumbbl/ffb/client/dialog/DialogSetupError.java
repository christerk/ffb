package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;

public class DialogSetupError extends DialogInformation {

	public DialogSetupError(FantasyFootballClient pClient, String[] pSetupErrors) {
		super(pClient, "Setup-Error", pSetupErrors, DialogInformation.OK_DIALOG, IIconProperty.GAME_REF);
	}

	public DialogId getId() {
		return DialogId.SETUP_ERROR;
	}

}
