package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.StatusType;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogPettyCashParameter;
import com.fumbbl.ffb.model.Game;

/**
 * 
 * @author Kalimar
 */
public class DialogPettyCashHandler extends DialogHandler {

	public DialogPettyCashHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {

		Game game = getClient().getGame();
		DialogPettyCashParameter dialogPettyCashParameter = (DialogPettyCashParameter) game.getDialogParameter();

		if (dialogPettyCashParameter != null) {

			if ((ClientMode.PLAYER == getClient().getMode())
					&& dialogPettyCashParameter.getTeamId().equals(game.getTeamHome().getId())) {
				setDialog(new DialogPettyCash(getClient(), dialogPettyCashParameter.getTeamValue(),
						dialogPettyCashParameter.getTreasury(), dialogPettyCashParameter.getOpponentTeamValue()));
				getDialog().showDialog(this);

			} else {
				showStatus("Petty Cash", "Waiting for coach to transfer gold to petty cash.", StatusType.WAITING);
			}

		}

	}

	public void dialogClosed(IDialog pDialog) {
		hideDialog();
		if (testDialogHasId(pDialog, DialogId.PETTY_CASH)) {
			DialogPettyCash pettyCashDialog = (DialogPettyCash) pDialog;
			getClient().getCommunication().sendPettyCash(pettyCashDialog.getPettyCash());
		}
	}

}
