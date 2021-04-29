package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.StatusType;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogUseSafePairOfHandsParameter;
import com.fumbbl.ffb.model.Game;

public class DialogUseSafePairOfHandsHandler extends DialogHandler {

	public DialogUseSafePairOfHandsHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {

		Game game = getClient().getGame();

		DialogUseSafePairOfHandsParameter dialogParameter = (DialogUseSafePairOfHandsParameter) game.getDialogParameter();
		if (dialogParameter != null) {

			if ((ClientMode.PLAYER == getClient().getMode())
				&& game.getTeamHome().hasPlayer(game.getPlayerById(dialogParameter.getPlayerId()))) {

				setDialog(new DialogUseSafePairOfHands(getClient(), dialogParameter));
				getDialog().showDialog(this);
			}


		} else {
			showStatus("Safe Pair of Hands", "Waiting for coach to decide whether to use Safe Pair of Hands.", StatusType.WAITING);
		}

	}

	public void dialogClosed(IDialog pDialog) {
		hideDialog();
		boolean use = false;
		if (testDialogHasId(pDialog, DialogId.USE_SAFE_PAIR_OF_HANDS)) {
			DialogUseSafePairOfHands dialog = (DialogUseSafePairOfHands) pDialog;
			use = dialog.isChoiceYes();
		}

		getClient().getCommunication().sendUseSafePairOfHands(use);
	}

}
