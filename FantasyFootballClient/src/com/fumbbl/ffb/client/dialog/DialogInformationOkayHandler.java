package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogInformationOkayParameter;
import com.fumbbl.ffb.model.Game;

public class DialogInformationOkayHandler extends DialogHandler {

	public DialogInformationOkayHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {

		Game game = getClient().getGame();

		DialogInformationOkayParameter parameter = (DialogInformationOkayParameter) game.getDialogParameter();
		if ((ClientMode.PLAYER == getClient().getMode()) && game.isHomePlaying()) {
			setDialog(new DialogInformation(getClient(), parameter.getTitle(), parameter.getMessages(), DialogInformation.OK_DIALOG, true));
			getDialog().showDialog(this);

		}

	}

	public void dialogClosed(IDialog pDialog) {
		hideDialog();
	}

}
