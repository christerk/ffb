package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;

/**
 * 
 * @author Kalimar
 */
public class DialogReceiveChoice extends DialogYesOrNoQuestion {

	public DialogReceiveChoice(FantasyFootballClient pClient) {
		super(pClient, "Kick or receive", new String[] { "Do you want to receive the kickoff ?" }, IIconProperty.GAME_REF);
	}

	public DialogId getId() {
		return DialogId.RECEIVE_CHOICE;
	}

}
