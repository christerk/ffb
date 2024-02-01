package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.StatusType;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogUseChainsawParameter;
import com.fumbbl.ffb.model.Game;

public class DialogUseChainsawHandler extends DialogHandler {

	public DialogUseChainsawHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {

		Game game = getClient().getGame();
		DialogUseChainsawParameter dialogParameter = (DialogUseChainsawParameter) game
				.getDialogParameter();

		if (dialogParameter != null) {

			if ((ClientMode.PLAYER == getClient().getMode()) && game.getTeamHome().getId().equals(dialogParameter.getTeamId())) {
				setDialog(new DialogUseChainsaw(getClient()));
				getDialog().showDialog(this);

			} else {
				showStatus("Chainsaw", "Waiting for coach to decide on using chainsaw.", StatusType.WAITING);
			}
		}
	}

	public void dialogClosed(IDialog pDialog) {
		hideDialog();
		if (testDialogHasId(pDialog, DialogId.USE_CHAINSAW)) {
			DialogUseChainsaw dialog = (DialogUseChainsaw) pDialog;
			getClient().getCommunication().sendUseChainsaw(dialog.isChoiceYes());
		}
	}

}
