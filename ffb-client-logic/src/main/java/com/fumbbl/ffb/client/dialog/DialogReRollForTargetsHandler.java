package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.StatusType;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogReRollForTargetsParameter;
import com.fumbbl.ffb.factory.SkillFactory;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

public class DialogReRollForTargetsHandler extends DialogHandler {

	public DialogReRollForTargetsHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {

		Game game = getClient().getGame();
		DialogReRollForTargetsParameter dialogReRollParameter = (DialogReRollForTargetsParameter) game.getDialogParameter();

		if (dialogReRollParameter != null) {

			Player<?> player = game.getPlayerById(dialogReRollParameter.getPlayerId());

			if ((ClientMode.PLAYER == getClient().getMode()) && game.getTeamHome().hasPlayer(player)) {
				setDialog(new DialogReRollForTargets(getClient(), dialogReRollParameter));
				getDialog().showDialog(this);

			} else {
				StringBuilder message = new StringBuilder();
				String reRolledActionName = (dialogReRollParameter.getReRolledAction() != null)
						? dialogReRollParameter.getReRolledAction().getName(game.getRules().<SkillFactory>getFactory(FactoryType.Factory.SKILL))
						: null;
				message.append("Waiting to re-roll ").append(reRolledActionName).append(".");
				showStatus("Re-roll", message.toString(), StatusType.WAITING);
			}
		}
	}

	public void dialogClosed(IDialog pDialog) {
		hideDialog();
		if (testDialogHasId(pDialog, DialogId.RE_ROLL_FOR_TARGETS)) {
			DialogReRollForTargets reRollDialog = (DialogReRollForTargets) pDialog;
			getClient().getCommunication().sendUseReRollForTarget(reRollDialog.getReRolledAction(), reRollDialog.getReRollSource(), reRollDialog.getSelectedTarget());
		}
	}
}
