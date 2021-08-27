package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.StatusType;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogReceiveChoiceParameter;
import com.fumbbl.ffb.model.Game;

/**
 * 
 * @author Kalimar
 */
public class DialogReceiveChoiceHandler extends DialogHandler {

	public DialogReceiveChoiceHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {

		Game game = getClient().getGame();
		DialogReceiveChoiceParameter dialogReceiveChoiceParameter = (DialogReceiveChoiceParameter) game
				.getDialogParameter();

		if (dialogReceiveChoiceParameter != null) {

			if ((ClientMode.PLAYER == getClient().getMode())
					&& game.getTeamHome().getId().equals(dialogReceiveChoiceParameter.getChoosingTeamId())) {
				setDialog(new DialogReceiveChoice(getClient()));
				getDialog().showDialog(this);

			} else {
				showStatus("Receive Choice", "Waiting for coach to choose to kick or receive.", StatusType.WAITING);
			}

		}

	}

	public void dialogClosed(IDialog pDialog) {
		hideDialog();
		if (testDialogHasId(pDialog, DialogId.RECEIVE_CHOICE)) {
			DialogReceiveChoice receiveDialog = (DialogReceiveChoice) pDialog;
			getClient().getCommunication().sendReceiveChoice(receiveDialog.isChoiceYes());
		}
	}

}
