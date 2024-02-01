package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.StatusType;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogBriberyAndCorruptionParameter;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Team;

public class DialogBriberyAndCorruptionHandler extends DialogHandler {
	public DialogBriberyAndCorruptionHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	@Override
	public void showDialog() {
		Game game = getClient().getGame();
		DialogBriberyAndCorruptionParameter dialogParameter = (DialogBriberyAndCorruptionParameter) game.getDialogParameter();

		if (dialogParameter != null) {
			Team team = game.getTeamById(dialogParameter.getTeamId());

			if (team == game.getTeamHome() && getClient().getMode() == ClientMode.PLAYER) {
				setDialog(new DialogBriberyAndCorruption(getClient()));
				getDialog().showDialog(this);
			} else {
				showStatus("Bribery and Corruption", "Waiting for coach to decide to use Bribery and Corruption", StatusType.WAITING);
			}
		}
	}

	@Override
	public void dialogClosed(IDialog pDialog) {
		hideDialog();
		if (testDialogHasId(pDialog, DialogId.BRIBERY_AND_CORRUPTION_RE_ROLL)) {
			boolean useBriberyAndCorruption = ((DialogBriberyAndCorruption) pDialog).isChoiceYes();
			getClient().getCommunication().sendUseReRoll(ReRolledActions.ARGUE_THE_CALL, useBriberyAndCorruption ? ReRollSources.BRIBERY_AND_CORRUPTION : null);
		}
	}
}
