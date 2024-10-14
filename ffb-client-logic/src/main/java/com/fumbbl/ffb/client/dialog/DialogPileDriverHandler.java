package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.StatusType;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogPileDriverParameter;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.util.ArrayTool;

public class DialogPileDriverHandler extends DialogHandler {

	public DialogPileDriverHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {

		Game game = getClient().getGame();

		DialogPileDriverParameter dialogParameter = (DialogPileDriverParameter) game.getDialogParameter();
		if (dialogParameter != null) {

			if ((ClientMode.PLAYER == getClient().getMode())
					&& game.getTeamHome().getId().equals(dialogParameter.getTeamId())) {

				if (dialogParameter.getKnockedDownPlayers().size() == 1) {
					setDialog(new DialogPileDriver(getClient(), dialogParameter.getKnockedDownPlayers().get(0)));
				} else {
					setDialog(new DialogPlayerChoice(getClient(), "Select player to foul using Pile Driver",
						dialogParameter.getKnockedDownPlayers().toArray(new String[0]), null, 0, 1, null, false));
				}

				getDialog().showDialog(this);

			} else {
				showStatus("Pile Driver", "Waiting for coach to decide on using Pile Driver.", StatusType.WAITING);
			}

		}

	}

	public void dialogClosed(IDialog pDialog) {
		hideDialog();
		String selectedPlayer = null;
		if (testDialogHasId(pDialog, DialogId.PILE_DRIVER)) {
			DialogPileDriver dialog = (DialogPileDriver) pDialog;
			if (dialog.isChoiceYes()) {
				selectedPlayer = dialog.getPlayerId();
			}
		}
		if (testDialogHasId(pDialog, DialogId.PLAYER_CHOICE) && pDialog instanceof DialogPlayerChoice) {
			DialogPlayerChoice playerChoiceDialog = (DialogPlayerChoice) pDialog;
			if (ArrayTool.isProvided(playerChoiceDialog.getSelectedPlayers())) {
				selectedPlayer = playerChoiceDialog.getSelectedPlayers()[0].getId();
			}
		}

		getClient().getCommunication().sendPileDriver(selectedPlayer);
	}

}
