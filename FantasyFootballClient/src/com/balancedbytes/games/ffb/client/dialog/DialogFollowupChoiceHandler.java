package com.balancedbytes.games.ffb.client.dialog;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.dialog.DialogId;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;

/**
 *
 * @author Kalimar
 */
public class DialogFollowupChoiceHandler extends DialogHandler {

	public DialogFollowupChoiceHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {

		Game game = getClient().getGame();
		Player<?> player = game.getActingPlayer().getPlayer();

		if ((getClient().getMode() == ClientMode.PLAYER) && game.getTeamHome().hasPlayer(player)) {
			setDialog(new DialogFollowupChoice(getClient()));
			getDialog().showDialog(this);
		}

	}

	public void dialogClosed(IDialog pDialog) {
		hideDialog();
		if (testDialogHasId(pDialog, DialogId.FOLLOWUP_CHOICE)) {
			DialogFollowupChoice followUpDialog = (DialogFollowupChoice) pDialog;
			getClient().getCommunication().sendFollowupChoice(followUpDialog.isChoiceYes());
		}
	}

}
