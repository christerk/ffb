package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.StatusType;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogReRollParameter;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

/**
 *
 * @author Kalimar
 */
public class DialogReRollHandler extends DialogHandler {

	public DialogReRollHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {

		Game game = getClient().getGame();
		DialogReRollParameter dialogReRollParameter = (DialogReRollParameter) game.getDialogParameter();

		if (dialogReRollParameter != null) {

			Player<?> player = game.getPlayerById(dialogReRollParameter.getPlayerId());

			if ((ClientMode.PLAYER == getClient().getMode()) && game.getTeamHome().hasPlayer(player)) {
				setDialog(new DialogReRoll(getClient(), dialogReRollParameter));
				getDialog().showDialog(this);

			} else {
				StringBuilder message = new StringBuilder();
				String reRolledActionName = (dialogReRollParameter.getReRolledAction() != null)
					? dialogReRollParameter.getReRolledAction().getName(game.getRules().getFactory(FactoryType.Factory.SKILL))
					: null;
				message.append("Waiting to re-roll ").append(reRolledActionName);
				if (dialogReRollParameter.getMinimumRoll() > 0) {
					message.append(" (").append(dialogReRollParameter.getMinimumRoll()).append("+ to succeed)");
				}
				message.append(".");
				showStatus("Re-roll", message.toString(), StatusType.WAITING);
			}

		}

	}

	public void dialogClosed(IDialog pDialog) {
		hideDialog();
		if (testDialogHasId(pDialog, DialogId.RE_ROLL)) {
			DialogReRoll reRollDialog = (DialogReRoll) pDialog;
			if (reRollDialog.isUseSkill()) {
				getClient().getCommunication().sendUseSkill(reRollDialog.getDialogParameter().getReRollSkill(), true, reRollDialog.getDialogParameter().getPlayerId());
			} else {
				getClient().getCommunication().sendUseReRoll(reRollDialog.getReRolledAction(), reRollDialog.getReRollSource());
			}
		}
	}

}
