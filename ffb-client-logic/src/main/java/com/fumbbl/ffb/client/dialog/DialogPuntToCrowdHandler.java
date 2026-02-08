package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogPuntToCrowdParameter;
import com.fumbbl.ffb.model.Game;

public class DialogPuntToCrowdHandler extends DialogHandler {

	public DialogPuntToCrowdHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {

		Game game = getClient().getGame();

		DialogPuntToCrowdParameter dialogParameter = (DialogPuntToCrowdParameter) game.getDialogParameter();
		if (dialogParameter != null) {

			if ((ClientMode.PLAYER == getClient().getMode()) && game.isHomePlaying()) {

				setDialog(new DialogPuntToCrowd(getClient()));

				getDialog().showDialog(this);
			}
		}
	}

	public void dialogClosed(IDialog pDialog) {
		hideDialog();
		DialogPuntToCrowd dialog = (DialogPuntToCrowd) pDialog;
		getClient().getCommunication().sendPuntToCrowd(dialog.isChoiceYes());
	}

}
