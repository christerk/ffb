package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.client.FantasyFootballClient;

/**
 * 
 * @author Kalimar
 */
public class DialogGameStatisticsHandler extends DialogHandler {

	// used only when showing the GameStatistics upon ending the game

	public DialogGameStatisticsHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {
		if (ClientMode.PLAYER == getClient().getMode()) {
			setDialog(new DialogGameStatistics(getClient()));
			getDialog().showDialog(this);
		}
	}

	public void dialogClosed(IDialog pDialog) {
		hideDialog();
	}

}
