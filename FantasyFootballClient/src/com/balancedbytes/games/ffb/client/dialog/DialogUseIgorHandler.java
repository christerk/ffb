package com.balancedbytes.games.ffb.client.dialog;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.InducementType;
import com.balancedbytes.games.ffb.StatusType;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.dialog.DialogId;
import com.balancedbytes.games.ffb.dialog.DialogUseIgorParameter;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;

/**
 *
 * @author Kalimar
 */
public class DialogUseIgorHandler extends DialogHandler {

	public DialogUseIgorHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {

		Game game = getClient().getGame();
		DialogUseIgorParameter dialogUseIgorParameter = (DialogUseIgorParameter) game.getDialogParameter();

		if (dialogUseIgorParameter != null) {

			Player<?> player = game.getPlayerById(dialogUseIgorParameter.getPlayerId());

			if ((ClientMode.PLAYER == getClient().getMode()) && getClient().getGame().getTeamHome().hasPlayer(player)) {
				setDialog(new DialogUseIgor(getClient(), dialogUseIgorParameter));
				getDialog().showDialog(this);

			} else {
				showStatus("Igor", "Waiting for coach to use Igor.", StatusType.WAITING);
			}

		}

	}

	public void dialogClosed(IDialog pDialog) {
		hideDialog();
		if (testDialogHasId(pDialog, DialogId.USE_IGOR)) {
			DialogUseIgor igorDialog = (DialogUseIgor) pDialog;
			if (igorDialog.isChoiceYes()) {
				getClient().getCommunication().sendUseInducement(InducementType.IGOR, igorDialog.getPlayerId());
			} else {
				getClient().getCommunication().sendUseInducement(InducementType.IGOR, (String) null);
			}
		}
	}

}
