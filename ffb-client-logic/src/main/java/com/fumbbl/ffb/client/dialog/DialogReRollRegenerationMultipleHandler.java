package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.StatusType;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogReRollRegenerationMultipleParameter;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

public class DialogReRollRegenerationMultipleHandler extends AbstractReRollDialogHandler {

	public DialogReRollRegenerationMultipleHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	private DialogReRollRegenerationMultipleParameter parameter;

	public void showDialog() {

		Game game = getClient().getGame();

		parameter = (DialogReRollRegenerationMultipleParameter) game.getDialogParameter();

		Player<?> player = game.getPlayerById(parameter.getPlayerIds().get(0));

		if ((ClientMode.PLAYER == getClient().getMode()) && game.getTeamHome().hasPlayer(player)) {
			setDialog(new DialogReRollRegenerationMultiple(getClient(), parameter));

			getDialog().showDialog(this);

		} else {
			showStatus("Re-Roll Regeneration", "Waiting for opponent to re-roll failed Regeneration rolls",
				StatusType.WAITING);
		}
	}

	@Override
	protected void reRollDialogClosed(IDialog dialog) {
		hideDialog();
		if (testDialogHasId(dialog, DialogId.RE_ROLL_REGENERATION_MULTIPLE)) {
			DialogReRollRegenerationMultiple reRollDialog = (DialogReRollRegenerationMultiple) dialog;
			String target = reRollDialog.getSelectedTarget();
			int targetIndex = parameter.getPlayerIds().indexOf(target);
			if (!parameter.getReRollOptions().isEmpty() && reRollDialog.isTeamReRollSelected() && targetIndex >= 0) {
				selectTeamReRoll(parameter.getReRollOptions().get(targetIndex), reRollDialog.getReRollSource(),
					source -> getClient().getCommunication().sendUseReRollForTarget(ReRolledActions.REGENERATION, source,
						target));
			} else if (!parameter.getReRollOptions().isEmpty()) {
				getClient().getCommunication().sendUseReRollForTarget(ReRolledActions.REGENERATION,
					reRollDialog.getReRollSource(), target);
			} else {
				getClient().getCommunication().sendUseInducement(parameter.getInducementType(),
					reRollDialog.getSelectedTarget());
			}
		}
	}
}
