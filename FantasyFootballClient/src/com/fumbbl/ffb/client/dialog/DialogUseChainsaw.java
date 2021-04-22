package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;

public class DialogUseChainsaw extends DialogYesOrNoQuestion {

	public DialogUseChainsaw(FantasyFootballClient pClient) {
		super(pClient, "Use Chainsaw", "Do you want to use your chainsaw on the foul?");
	}

	public DialogId getId() {
		return DialogId.USE_CHAINSAW;
	}
}
