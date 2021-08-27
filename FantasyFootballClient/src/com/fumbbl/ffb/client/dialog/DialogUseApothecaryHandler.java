package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.StatusType;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogUseApothecaryParameter;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

/**
 *
 * @author Kalimar
 */
public class DialogUseApothecaryHandler extends DialogHandler {

	public DialogUseApothecaryHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {

		Game game = getClient().getGame();
		DialogUseApothecaryParameter dialogUseApothecaryParameter = (DialogUseApothecaryParameter) game
				.getDialogParameter();

		if (dialogUseApothecaryParameter != null) {

			Player<?> player = game.getPlayerById(dialogUseApothecaryParameter.getPlayerId());

			if ((ClientMode.PLAYER == getClient().getMode()) && getClient().getGame().getTeamHome().hasPlayer(player)) {
				setDialog(new DialogUseApothecary(getClient(), dialogUseApothecaryParameter));
				getDialog().showDialog(this);

			} else {
				showStatus("Apothecary", "Waiting for coach to use Apothecary.", StatusType.WAITING);
			}

		}

	}

	public void dialogClosed(IDialog pDialog) {
		hideDialog();
		if (testDialogHasId(pDialog, DialogId.USE_APOTHECARY)) {
			DialogUseApothecary apothecaryDialog = (DialogUseApothecary) pDialog;
			getClient().getCommunication().sendUseApothecary(apothecaryDialog.getPlayerId(), apothecaryDialog.isChoiceYes());
		}
	}

}
