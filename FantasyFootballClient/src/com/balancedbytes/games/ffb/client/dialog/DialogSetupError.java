package com.balancedbytes.games.ffb.client.dialog;

import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.IIconProperty;
import com.balancedbytes.games.ffb.dialog.DialogId;

@SuppressWarnings("serial")
public class DialogSetupError extends DialogInformation {

	public DialogSetupError(FantasyFootballClient pClient, String[] pSetupErrors) {
		super(pClient, "Setup-Error", pSetupErrors, DialogInformation.OK_DIALOG, IIconProperty.GAME_REF);
	}

	public DialogId getId() {
		return DialogId.SETUP_ERROR;
	}

}
