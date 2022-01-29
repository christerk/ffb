package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogConfirmEndBlitzActionParameter;
import com.fumbbl.ffb.model.Game;

public class DialogConfirmEndBlitzActionHandler extends DialogHandler {

	public DialogConfirmEndBlitzActionHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {

		Game game = getClient().getGame();

		DialogConfirmEndBlitzActionParameter dialogParameter = (DialogConfirmEndBlitzActionParameter) game.getDialogParameter();
		if (dialogParameter != null) {

			if ((ClientMode.PLAYER == getClient().getMode())
				&& game.getTeamHome().getId().equals(dialogParameter.getTeamId())) {

				setDialog(new DialogConfirmEndBlitzAction(getClient()));

				getDialog().showDialog(this);
			}
		}
	}

	public void dialogClosed(IDialog pDialog) {
		hideDialog();
		DialogConfirmEndBlitzAction dialog = (DialogConfirmEndBlitzAction) pDialog;
		if (dialog.isChoiceYes()) {
			getClient().getCommunication().sendConfirm();
		}
	}

}
