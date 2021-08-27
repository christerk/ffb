package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.StatusType;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogJourneymenParameter;
import com.fumbbl.ffb.model.Game;

/**
 * 
 * @author Kalimar
 */
public class DialogJourneymenHandler extends DialogHandler {

	public DialogJourneymenHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {

		Game game = getClient().getGame();
		DialogJourneymenParameter dialogJourneymenParameter = (DialogJourneymenParameter) game.getDialogParameter();

		if (dialogJourneymenParameter != null) {

			if ((ClientMode.PLAYER == getClient().getMode())
					&& game.getTeamHome().getId().equals(dialogJourneymenParameter.getTeamId())) {
				setDialog(new DialogJourneymen(getClient(), dialogJourneymenParameter.getNrOfSlots(),
						dialogJourneymenParameter.getPositionIds()));
				getDialog().showDialog(this);

			} else {
				showStatus("Journeymen",
						"Waiting for coach to hire up to " + dialogJourneymenParameter.getNrOfSlots() + " Journeymen.",
						StatusType.WAITING);
			}

		}

	}

	public void dialogClosed(IDialog pDialog) {
		hideDialog();
		if (testDialogHasId(pDialog, DialogId.JOURNEYMEN)) {
			DialogJourneymen journeymenDialog = (DialogJourneymen) pDialog;
			getClient().getCommunication().sendJourneymen(journeymenDialog.getPositionIds(),
					journeymenDialog.getSlotsSelected());
		}
	}

}
