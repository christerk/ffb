package com.balancedbytes.games.ffb.client.dialog;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.StatusType;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.dialog.DialogId;
import com.balancedbytes.games.ffb.dialog.DialogUseApothecaryParameter;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;

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
