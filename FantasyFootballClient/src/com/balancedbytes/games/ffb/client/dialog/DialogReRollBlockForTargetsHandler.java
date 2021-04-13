package com.balancedbytes.games.ffb.client.dialog;

import com.balancedbytes.games.ffb.ClientMode;
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
		DialogReRollBlockForTargetsParameter dialogParameter = (DialogReRollBlockForTargetsParameter) game.getDialogParameter();

		if (dialogParameter != null) {

			Player<?> player = game.getPlayerById(dialogParameter.getPlayerId());

			if ((ClientMode.PLAYER == getClient().getMode()) && game.getTeamHome().hasPlayer(player)) {
				setDialog(new DialogReRollBlockForTargets(getClient(), dialogParameter));
				getDialog().showDialog(this);

			} else {
				getClient().getClientData().setBlockDiceResult(dialogParameter.getBlockRolls());
			}
		}
	}

	public void dialogClosed(IDialog pDialog) {
		getClient().getClientData().clearBlockDiceResult();
		hideDialog();
		if (testDialogHasId(pDialog, DialogId.RE_ROLL_BLOCK_FOR_TARGETS)) {
			DialogReRollBlockForTargets reRollDialog = (DialogReRollBlockForTargets) pDialog;
			getClient().getCommunication().sendBlockOrReRollChoiceForTarget(reRollDialog.getSelectedTarget(), reRollDialog.getSelectedIndex(), reRollDialog.getReRollSource());
		}
	}
}
