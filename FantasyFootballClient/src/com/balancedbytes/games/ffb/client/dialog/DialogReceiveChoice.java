package com.balancedbytes.games.ffb.client.dialog;

import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.IIconProperty;
import com.balancedbytes.games.ffb.dialog.DialogId;

/**
 * 
 * @author Kalimar
 */
@SuppressWarnings("serial")
public class DialogReceiveChoice extends DialogYesOrNoQuestion {

	public DialogReceiveChoice(FantasyFootballClient pClient) {
		super(pClient, "Kick or receive", new String[] { "Do you want to receive the kickoff ?" }, IIconProperty.GAME_REF);
	}

	public DialogId getId() {
		return DialogId.RECEIVE_CHOICE;
	}

}
