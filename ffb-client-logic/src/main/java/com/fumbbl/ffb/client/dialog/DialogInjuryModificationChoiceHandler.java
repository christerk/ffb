package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.StatusType;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogInjuryModificationChoiceParameter;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.InjuryModificationOption;

import java.util.Collections;

/**
 * Handles the dialog offering injury modification options.
 */
public class DialogInjuryModificationChoiceHandler extends DialogHandler {

	public DialogInjuryModificationChoiceHandler(FantasyFootballClient client) {
		super(client);
	}

	public void showDialog() {
		Game game = getClient().getGame();
		DialogInjuryModificationChoiceParameter dialogParameter =
			(DialogInjuryModificationChoiceParameter) game.getDialogParameter();
		if (dialogParameter == null) {
			return;
		}
		if ((ClientMode.PLAYER == getClient().getMode()) && game.getTeamHome().getId().equals(dialogParameter.getTeamId())) {
			setDialog(new DialogInjuryModificationChoice(getClient(), dialogParameter));
			getDialog().showDialog(this);
			if (!game.isHomePlaying()) {
				playSound(SoundId.QUESTION);
			}
		} else {
			showStatus("Skill Use", "Waiting for coach to modify the injury.", StatusType.WAITING);
		}
	}

	public void dialogClosed(IDialog dialog) {
		hideDialog();
		if (testDialogHasId(dialog, DialogId.INJURY_MODIFICATION_CHOICE)) {
			InjuryModificationOption option = ((DialogInjuryModificationChoice) dialog).getSelectedOption();
			getClient().getCommunication().sendInjuryModificationChoice(
				option != null ? option.getSkills() : Collections.emptyList());
		}
	}
}
