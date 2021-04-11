package com.balancedbytes.games.ffb.client.dialog;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.StatusType;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.dialog.DialogId;
import com.balancedbytes.games.ffb.dialog.DialogReRollBlockForTargetsParameter;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;

public class DialogReRollBlockForTargetsHandler extends DialogHandler {

	public DialogReRollBlockForTargetsHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {

		Game game = getClient().getGame();
		DialogReRollBlockForTargetsParameter dialogReRollParameter = (DialogReRollBlockForTargetsParameter) game.getDialogParameter();

		if (dialogReRollParameter != null) {

			Player<?> player = game.getPlayerById(dialogReRollParameter.getPlayerId());

			if ((ClientMode.PLAYER == getClient().getMode()) && game.getTeamHome().hasPlayer(player)) {
				setDialog(new DialogReRollBlockForTargets(getClient(), dialogReRollParameter));
				getDialog().showDialog(this);

			} else {
				showStatus("Re-roll", "Waiting to re-roll blocks.", StatusType.WAITING);
			}
		}
	}

	public void dialogClosed(IDialog pDialog) {
		hideDialog();
		if (testDialogHasId(pDialog, DialogId.RE_ROLL_BLOCK_FOR_TARGETS)) {
			DialogReRollBlockForTargets reRollDialog = (DialogReRollBlockForTargets) pDialog;
			getClient().getCommunication().sendBlockOrReRollChoiceForTarget(reRollDialog.getSelectedTarget(), reRollDialog.getSelectedIndex(), reRollDialog.getReRollSource());
		}
	}
}
