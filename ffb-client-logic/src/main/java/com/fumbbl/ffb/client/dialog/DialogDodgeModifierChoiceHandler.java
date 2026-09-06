package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.StatusType;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogDodgeModifierChoiceParameter;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

/**
 * Handles the dialog offering optional dodge modifiers and re-rolls.
 */
public class DialogDodgeModifierChoiceHandler extends DialogHandler {

	public DialogDodgeModifierChoiceHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {

		Game game = getClient().getGame();
		DialogDodgeModifierChoiceParameter dialogParameter =
			(DialogDodgeModifierChoiceParameter) game.getDialogParameter();

		if (dialogParameter != null) {

			Player<?> player = game.getPlayerById(dialogParameter.getPlayerId());

			if ((ClientMode.PLAYER == getClient().getMode()) && game.getTeamHome().hasPlayer(player)) {
				setDialog(new DialogDodgeModifierChoice(getClient(), dialogParameter));
				getDialog().showDialog(this);

			} else {
				StringBuilder message = new StringBuilder();
				String reRolledActionName = (dialogParameter.getReRolledAction() != null)
					? dialogParameter.getReRolledAction().getName(game.getRules().getFactory(FactoryType.Factory.SKILL))
					: null;
				message.append("Waiting to re-roll ").append(reRolledActionName);
				if (dialogParameter.getMinimumRoll() > 0) {
					message.append(" (").append(dialogParameter.getMinimumRoll()).append("+ to succeed)");
				}
				message.append(".");
				showStatus("Re-roll", message.toString(), StatusType.WAITING);
			}

		}

	}

	public void dialogClosed(IDialog pDialog) {
		hideDialog();
		if (testDialogHasId(pDialog, DialogId.DODGE_MODIFIER_CHOICE)) {
			DialogDodgeModifierChoice modifierChoiceDialog = (DialogDodgeModifierChoice) pDialog;
			String playerId = modifierChoiceDialog.getDialogParameter().getPlayerId();
			if (modifierChoiceDialog.isUseModifiers()) {
				getClient().getCommunication().sendDodgeModifierChoice(playerId,
					modifierChoiceDialog.getSelectedOption().getSkills(), modifierChoiceDialog.getReRolledAction());
			} else if (modifierChoiceDialog.isUseSkill()) {
				getClient().getCommunication().sendUseSkill(modifierChoiceDialog.getUsedSkill(), true, playerId,
					modifierChoiceDialog.getReRolledAction());
			} else {
				getClient().getCommunication().sendUseReRoll(modifierChoiceDialog.getReRolledAction(),
					modifierChoiceDialog.getReRollSource());
			}
		}
	}

}
