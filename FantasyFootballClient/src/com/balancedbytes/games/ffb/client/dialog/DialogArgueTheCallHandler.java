package com.balancedbytes.games.ffb.client.dialog;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.StatusType;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.dialog.DialogArgueTheCallParameter;
import com.balancedbytes.games.ffb.dialog.DialogId;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.util.ArrayTool;

/**
 * 
 * @author Kalimar
 */
public class DialogArgueTheCallHandler extends DialogHandler {

	public DialogArgueTheCallHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {

		Game game = getClient().getGame();

		DialogArgueTheCallParameter dialogParameter = (DialogArgueTheCallParameter) game.getDialogParameter();
		if (dialogParameter != null) {

			if ((ClientMode.PLAYER == getClient().getMode())
					&& game.getTeamHome().getId().equals(dialogParameter.getTeamId())) {

				if (ArrayTool.isProvided(dialogParameter.getPlayerIds()) && dialogParameter.getPlayerIds().length == 1) {
					Player player = game.getPlayerById(dialogParameter.getPlayerIds()[0]);
					setDialog(new DialogArgueTheCall(getClient(), player));

				} else {
					setDialog(new DialogPlayerChoice(getClient(), "Select players to argue the call for",
							dialogParameter.getPlayerIds(), null, 0, dialogParameter.getPlayerIds().length, null, true));
				}

				getDialog().showDialog(this);

			} else {
				showStatus("Argue the call", "Waiting for coach to argue the call.", StatusType.WAITING);
			}

		}

	}

	public void dialogClosed(IDialog pDialog) {
		hideDialog();
		if (testDialogHasId(pDialog, DialogId.ARGUE_THE_CALL)) {
			DialogArgueTheCall argueTheCallDialog = (DialogArgueTheCall) pDialog;
			Game game = getClient().getGame();
			DialogArgueTheCallParameter dialogArgueTheCallParameter = (DialogArgueTheCallParameter) game.getDialogParameter();
			String playerId = argueTheCallDialog.isChoiceYes() ? dialogArgueTheCallParameter.getPlayerIds()[0] : null;
			getClient().getCommunication().sendArgueTheCall(playerId);
		}
		if (testDialogHasId(pDialog, DialogId.PLAYER_CHOICE)) {
			DialogPlayerChoice playerChoiceDialog = (DialogPlayerChoice) pDialog;
			Player[] selectedPlayers = playerChoiceDialog.getSelectedPlayers();
			String[] selectedPlayerIds = new String[selectedPlayers.length];
			for (int i = 0; i < selectedPlayerIds.length; i++) {
				selectedPlayerIds[i] = selectedPlayers[i].getId();
			}
			getClient().getCommunication().sendArgueTheCall(selectedPlayerIds);
		}
	}

}
