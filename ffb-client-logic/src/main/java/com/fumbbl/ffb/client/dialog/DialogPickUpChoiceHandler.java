package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;


public class DialogPickUpChoiceHandler extends DialogHandler {

	public DialogPickUpChoiceHandler(FantasyFootballClient client) {
		super(client);
	}

	@Override
	public void showDialog() {
		Game game = getClient().getGame();

		Player<?> target = game.getDefender();

		if (getClient().getMode() == ClientMode.PLAYER && game.getTeamHome().hasPlayer(target)) {
			setDialog(new DialogPickUpChoice(getClient()));
			getDialog().showDialog(this);
		}
	}

	@Override
	public void dialogClosed(IDialog dialog) {
		hideDialog();
		if (testDialogHasId(dialog, DialogId.PICK_UP_CHOICE)) {
			DialogPickUpChoice pickUpDialog = (DialogPickUpChoice) dialog;
			getClient().getCommunication().sendPickUpChoice(pickUpDialog.isChoiceYes());
		}
	}
}
