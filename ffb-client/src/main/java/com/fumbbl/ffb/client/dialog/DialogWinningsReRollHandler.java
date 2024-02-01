package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.StatusType;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogWinningsReRollParameter;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Team;

/**
 * 
 * @author Kalimar
 */
public class DialogWinningsReRollHandler extends DialogHandler {

	public DialogWinningsReRollHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {

		Game game = getClient().getGame();
		DialogWinningsReRollParameter dialogParameter = (DialogWinningsReRollParameter) game.getDialogParameter();

		if (dialogParameter != null) {

			Team team = game.getTeamHome().getId().equals(dialogParameter.getTeamId()) ? game.getTeamHome()
					: game.getTeamAway();

			if ((ClientMode.PLAYER == getClient().getMode()) && (game.getTeamHome() == team)) {
				setDialog(new DialogWinningsReRoll(getClient(), dialogParameter.getOldRoll()));
				getDialog().showDialog(this);

			} else {
				showStatus("Winnings", "Waiting for coach to re-roll winnings.", StatusType.WAITING);
			}

		}

	}

	public void dialogClosed(IDialog pDialog) {
		hideDialog();
		if (testDialogHasId(pDialog, DialogId.WINNINGS_RE_ROLL)) {
			DialogWinningsReRoll winningsReRollDialog = (DialogWinningsReRoll) pDialog;
			ReRollSource reRollSource = !winningsReRollDialog.isChoiceYes() ? ReRollSources.WINNINGS : null;
			getClient().getCommunication().sendUseReRoll(ReRolledActions.WINNINGS, reRollSource);
		}
	}

}
