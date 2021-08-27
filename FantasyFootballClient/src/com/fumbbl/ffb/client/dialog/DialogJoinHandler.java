package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.StatusType;
import com.fumbbl.ffb.client.FantasyFootballClient;

/**
 * 
 * @author Kalimar
 */
public class DialogJoinHandler extends DialogHandler {

	private static final String _STATUS_TITLE = "Game start";

	public DialogJoinHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {
		if (ClientMode.PLAYER == getClient().getMode()) {
			showStatus(_STATUS_TITLE, "Waiting for coach to join the game.", StatusType.WAITING);
		} else {
			showStatus(_STATUS_TITLE, "Waiting for game to start.", StatusType.WAITING);
		}
	}

	public void dialogClosed(IDialog pDialog) {
		hideDialog();
	}

}
