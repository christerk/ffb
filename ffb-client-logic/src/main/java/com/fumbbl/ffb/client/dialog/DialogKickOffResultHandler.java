package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.StatusType;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogKickOffResultParameter;
import com.fumbbl.ffb.factory.KickoffResultFactory;
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

		DialogKickOffResultParameter dialogKickOffResultParameter =
			(DialogKickOffResultParameter) game.getDialogParameter();
		if (dialogKickOffResultParameter != null) {
			KickoffResultFactory factory = game.getFactory(FactoryType.Factory.KICKOFF_RESULT);

			String name = factory.forRoll(10).getName();
			if ((ClientMode.PLAYER == getClient().getMode())
				&& game.getTeamHome().getId().equals(dialogKickOffResultParameter.getTeamId())) {

				setDialog(new DialogKickOffResult(getClient(), name));

				getDialog().showDialog(this);

			} else {
				showStatus("Choose kick-off result",
					"Waiting for coach to choose between " + name + " and Solid Defence.",
					StatusType.WAITING);
			}

		}

	}

	public void dialogClosed(IDialog pDialog) {
		hideDialog();
		KickoffResultFactory factory = getClient().getGame().getFactory(FactoryType.Factory.KICKOFF_RESULT);
		DialogKickOffResult dialogKickOffResult = (DialogKickOffResult) pDialog;
		getClient().getCommunication().sendKickOffResultChoice(
			dialogKickOffResult.isChoiceYes() ? factory.forRoll(10) : factory.forRoll(4));
	}

}
