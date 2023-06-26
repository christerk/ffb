package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.StatusType;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogKickOffResultParameter;
import com.fumbbl.ffb.kickoff.bb2020.KickoffResult;
import com.fumbbl.ffb.model.Game;

/**
 * @author Kalimar
 */
public class DialogKickOffResultHandler extends DialogHandler {

	public DialogKickOffResultHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {

		Game game = getClient().getGame();

		DialogKickOffResultParameter dialogKickOffResultParameter = (DialogKickOffResultParameter) game.getDialogParameter();
		if (dialogKickOffResultParameter != null) {

			if ((ClientMode.PLAYER == getClient().getMode())
				&& game.getTeamHome().getId().equals(dialogKickOffResultParameter.getTeamId())) {

				setDialog(new DialogKickOffResult(getClient()));

				getDialog().showDialog(this);

			} else {
				showStatus("Choose kick-off result", "Waiting for coach to choose between Blitz! and Solid Defence.", StatusType.WAITING);
			}

		}

	}

	public void dialogClosed(IDialog pDialog) {
		hideDialog();
		DialogKickOffResult dialogKickOffResult = (DialogKickOffResult) pDialog;
		getClient().getCommunication().sendKickOffResultChoice(
			dialogKickOffResult.isChoiceYes() ? KickoffResult.BLITZ : KickoffResult.SOLID_DEFENCE);
	}

}
