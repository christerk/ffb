package com.balancedbytes.games.ffb.client.dialog;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.StatusType;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.dialog.DialogId;
import com.balancedbytes.games.ffb.dialog.DialogOpponentBlockSelectionParameter;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Team;

public class DialogOpponentBlockSelectionHandler extends DialogHandler {

	public DialogOpponentBlockSelectionHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {

		Game game = getClient().getGame();
		DialogOpponentBlockSelectionParameter dialogReRollParameter = (DialogOpponentBlockSelectionParameter) game.getDialogParameter();

		if (dialogReRollParameter != null) {

			Team team = game.getTeamById(dialogReRollParameter.getTeamId());

			if ((ClientMode.PLAYER == getClient().getMode()) && game.getTeamHome() == team) {
				setDialog(new DialogOpponentBlockSelection(getClient(), dialogReRollParameter));
				getDialog().showDialog(this);

			} else {
				showStatus("Select Block Results", "Waiting for opponent to select block results.", StatusType.WAITING);
			}
		}
	}

	public void dialogClosed(IDialog pDialog) {
		hideDialog();
		if (testDialogHasId(pDialog, DialogId.OPPONENT_BLOCK_SELECTION)) {
			DialogOpponentBlockSelection reRollDialog = (DialogOpponentBlockSelection) pDialog;
			getClient().getCommunication().sendBlockOrReRollChoiceForTarget(reRollDialog.getSelectedTarget(), reRollDialog.getSelectedIndex(), null);
		}
	}
}
