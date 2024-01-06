package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.model.Game;

/**
 * @author Kalimar
 */
public class DialogPenaltyShootoutHandler extends DialogHandler {

	public DialogPenaltyShootoutHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {

		Game game = getClient().getGame();

		setDialog(new DialogPenaltyShootout(getClient()));
		getDialog().showDialog(this);

	}

	public void dialogClosed(IDialog pDialog) {
	}

}
