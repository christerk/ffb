package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.StatusType;
import com.fumbbl.ffb.client.ClientData;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogOpponentBlockSelectionParameter;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Team;

public class DialogOpponentBlockSelectionHandler extends DialogHandler {

	public DialogOpponentBlockSelectionHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {

		Game game = getClient().getGame();
		DialogOpponentBlockSelectionParameter dialogParameter = (DialogOpponentBlockSelectionParameter) game.getDialogParameter();

		if (dialogParameter != null) {

			Team team = game.getTeamById(dialogParameter.getTeamId());
			ClientData clientData = getClient().getClientData();

			if ((ClientMode.PLAYER == getClient().getMode()) && game.getTeamHome() == team) {
				clientData.clearBlockDiceResult();

				setDialog(new DialogOpponentBlockSelection(getClient(), dialogParameter));
				getDialog().showDialog(this);
				playSound(SoundId.QUESTION);

			} else {
				clientData.setBlockDiceResult(dialogParameter.getBlockRolls());
				showStatus("Select Block Results", "Waiting for coach to select block results.", StatusType.WAITING);
			}
		}
	}

	public void dialogClosed(IDialog pDialog) {
		getClient().getClientData().clearBlockDiceResult();
		hideDialog();
		if (testDialogHasId(pDialog, DialogId.OPPONENT_BLOCK_SELECTION)) {
			DialogOpponentBlockSelection reRollDialog = (DialogOpponentBlockSelection) pDialog;
			getClient().getCommunication().sendBlockOrReRollChoiceForTarget(reRollDialog.getSelectedTarget(), reRollDialog.getSelectedIndex(), null, 0);
		}
	}
}
