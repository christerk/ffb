package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogReRollBlockForTargetsParameter;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

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
			if (reRollDialog.getReRollSource() == ReRollSources.BRAWLER) {
				getClient().getCommunication().sendUseBrawler(reRollDialog.getBrawlerSelection(), reRollDialog.getSelectedTarget());
			} else {
				getClient().getCommunication().sendBlockOrReRollChoiceForTarget(reRollDialog.getSelectedTarget(), reRollDialog.getSelectedIndex(), reRollDialog.getReRollSource());
			}
		}
	}
}
