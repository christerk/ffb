package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogConfirmEndActionParameter;
import com.fumbbl.ffb.model.Game;

public class DialogConfirmEndActionHandler extends DialogHandler {

	public DialogConfirmEndActionHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {

		Game game = getClient().getGame();

		DialogConfirmEndActionParameter dialogParameter = (DialogConfirmEndActionParameter) game.getDialogParameter();
		if (dialogParameter != null) {

			if ((ClientMode.PLAYER == getClient().getMode())
				&& game.getTeamHome().getId().equals(dialogParameter.getTeamId())) {

				setDialog(new DialogConfirmEndAction(getClient(), dialogParameter.getPlayerAction()));

				getDialog().showDialog(this);
			}
		}
	}

	public void dialogClosed(IDialog pDialog) {
		hideDialog();
		DialogConfirmEndAction dialog = (DialogConfirmEndAction) pDialog;
		if (dialog.isChoiceYes()) {
			getClient().getCommunication().sendConfirm();
		}
	}

}
