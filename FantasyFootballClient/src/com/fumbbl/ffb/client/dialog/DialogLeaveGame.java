package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;

/**
 * 
 * @author Kalimar
 */
public class DialogLeaveGame extends DialogYesOrNoQuestion {

	public DialogLeaveGame(FantasyFootballClient pClient) {
		super(pClient, "Leave Game", new String[] { "Do you really want to leave the game?" }, null);
	}

	public DialogId getId() {
		return DialogId.LEAVE_GAME;
	}

}
