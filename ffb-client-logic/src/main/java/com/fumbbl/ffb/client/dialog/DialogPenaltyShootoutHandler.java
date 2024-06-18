package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogPenaltyShootoutParameter;
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

		setDialog(new DialogPenaltyShootout(getClient(), (DialogPenaltyShootoutParameter) game.getDialogParameter(), this));
		getDialog().showDialog(this);

	}

	public void dialogClosed(IDialog pDialog) {
		hideDialog();
		if (ClientMode.PLAYER == getClient().getMode()) {
			getClient().getCommunication().sendConfirm();
		}
	}

	@Override
	public boolean preventsExit() {
		return getDialog() != null && getDialog().isVisible() && getClient().getMode() == ClientMode.PLAYER;
	}
}
