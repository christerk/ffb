package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.StatusType;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogApothecaryChoiceParameter;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

/**
 *
 * @author Kalimar
 */
public class DialogApothecaryChoiceHandler extends DialogHandler {

	private DialogApothecaryChoiceParameter fDialogParameter;

	public DialogApothecaryChoiceHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {

		Game game = getClient().getGame();
		fDialogParameter = (DialogApothecaryChoiceParameter) game.getDialogParameter();

		if (fDialogParameter != null) {

			Player<?> player = game.getPlayerById(fDialogParameter.getPlayerId());

			if ((ClientMode.PLAYER == getClient().getMode()) && getClient().getGame().getTeamHome().hasPlayer(player)) {
				setDialog(new DialogApothecaryChoice(getClient(), fDialogParameter));
				getDialog().showDialog(this);

			} else {
				showStatus("Apothecary", "Waiting for coach to choose Apothecary Result.", StatusType.WAITING);
			}

		}

	}

	public void dialogClosed(IDialog pDialog) {
		hideDialog();
		if (testDialogHasId(pDialog, DialogId.APOTHECARY_CHOICE)) {
			DialogApothecaryChoice apothecaryChoiceDialog = (DialogApothecaryChoice) pDialog;
			if (apothecaryChoiceDialog.isChoiceNewInjury()) {
				getClient().getCommunication().sendApothecaryChoice(fDialogParameter.getPlayerId(),
						fDialogParameter.getPlayerStateNew(), fDialogParameter.getSeriousInjuryNew(), fDialogParameter.getPlayerStateOld());
			} else {
				getClient().getCommunication().sendApothecaryChoice(fDialogParameter.getPlayerId(),
						fDialogParameter.getPlayerStateOld(), fDialogParameter.getSeriousInjuryOld(), fDialogParameter.getPlayerStateOld());
			}
		}
	}

}
