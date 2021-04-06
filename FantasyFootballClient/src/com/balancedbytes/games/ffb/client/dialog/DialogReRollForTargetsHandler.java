package com.balancedbytes.games.ffb.client.dialog;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.StatusType;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.dialog.DialogId;
import com.balancedbytes.games.ffb.dialog.DialogReRollForTargetsParameter;
import com.balancedbytes.games.ffb.factory.SkillFactory;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;

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
