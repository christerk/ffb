package com.fumbbl.ffb.client.dialog.inducements;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.StatusType;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.dialog.DialogHandler;
import com.fumbbl.ffb.client.dialog.IDialog;
import com.fumbbl.ffb.dialog.DialogBuyInducementsParameter;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.model.Game;

public class DialogBuyInducementsHandler extends DialogHandler {

	public DialogBuyInducementsHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {

		Game game = getClient().getGame();
		DialogBuyInducementsParameter dialogParameter = (DialogBuyInducementsParameter) game.getDialogParameter();

		if (dialogParameter != null) {
			if ((ClientMode.PLAYER == getClient().getMode())
					&& (game.getTeamHome().getId().equals(dialogParameter.getTeamId()))) {
				setDialog(
						new DialogBuyInducements(getClient(), dialogParameter.getTeamId(), dialogParameter.getAvailableGold()));
				getDialog().showDialog(this);
			} else {
				showStatus("Buy Inducements", "Waiting for coach to buy Inducements.", StatusType.WAITING);
			}
		}

	}

	public void dialogClosed(IDialog pDialog) {
		hideDialog();
		if (testDialogHasId(pDialog, DialogId.BUY_INDUCEMENTS)) {
			DialogBuyInducements buyInducementsDialog = (DialogBuyInducements) pDialog;
			getClient().getCommunication().sendBuyInducements(buyInducementsDialog.getTeamId(),
				buyInducementsDialog.getAvailableGold(), buyInducementsDialog.getSelectedInducements(),
				buyInducementsDialog.getSelectedStarPlayerIds(), buyInducementsDialog.getSelectedMercenaryIds(),
				buyInducementsDialog.getSelectedMercenarySkills(), new String[0]);
		}
	}

}