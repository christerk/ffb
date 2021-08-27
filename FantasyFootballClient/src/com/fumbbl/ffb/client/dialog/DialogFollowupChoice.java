package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;

/**
 * 
 * @author Kalimar
 */
public class DialogFollowupChoice extends DialogYesOrNoQuestion {

	public DialogFollowupChoice(FantasyFootballClient pClient) {
		super(pClient, "Followup Choice", "Follow up the block?");
	}

	public DialogId getId() {
		return DialogId.FOLLOWUP_CHOICE;
	}

}
