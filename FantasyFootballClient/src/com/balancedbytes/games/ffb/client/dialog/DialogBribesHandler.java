package com.balancedbytes.games.ffb.client.dialog;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.InducementType;
import com.balancedbytes.games.ffb.StatusType;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.dialog.DialogBribesParameter;
import com.balancedbytes.games.ffb.dialog.DialogId;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.util.ArrayTool;

/**
 *
 * @author Kalimar
 */
public class DialogBribesHandler extends DialogHandler {

	public DialogBribesHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {

		Game game = getClient().getGame();

		DialogBribesParameter dialogBribesParameter = (DialogBribesParameter) game.getDialogParameter();
		if (dialogBribesParameter != null) {

			if ((ClientMode.PLAYER == getClient().getMode())
					&& game.getTeamHome().getId().equals(dialogBribesParameter.getTeamId())) {

				if (ArrayTool.isProvided(dialogBribesParameter.getPlayerIds())
						&& dialogBribesParameter.getPlayerIds().length == 1) {
					Player<?> player = game.getPlayerById(dialogBribesParameter.getPlayerIds()[0]);
					setDialog(new DialogBribes(getClient(), player));

				} else {
					StringBuilder header = new StringBuilder();
					if (dialogBribesParameter.getMaxNrOfBribes() > 1) {
						header.append("Select max. ").append(dialogBribesParameter.getMaxNrOfBribes())
								.append(" players to use a Bribe for");
					} else {
						header.append("Select a player to use a Bribe for");
					}
					setDialog(new DialogPlayerChoice(getClient(), header.toString(), dialogBribesParameter.getPlayerIds(), null,
							0, dialogBribesParameter.getMaxNrOfBribes(), null, false));
				}

				getDialog().showDialog(this);

			} else {
				showStatus("Use a bribe", "Waiting for coach to bribe the ref.", StatusType.WAITING);
			}

		}

	}

	public void dialogClosed(IDialog pDialog) {
		hideDialog();
		if (testDialogHasId(pDialog, DialogId.BRIBES)) {
			DialogBribes bribesDialog = (DialogBribes) pDialog;
			Game game = getClient().getGame();
			DialogBribesParameter dialogBribesParameter = (DialogBribesParameter) game.getDialogParameter();
			String playerId = bribesDialog.isChoiceYes() ? dialogBribesParameter.getPlayerIds()[0] : null;
			getClient().getCommunication().sendUseInducement(InducementType.BRIBES, playerId);
		}
		if (testDialogHasId(pDialog, DialogId.PLAYER_CHOICE)) {
			DialogPlayerChoice playerChoiceDialog = (DialogPlayerChoice) pDialog;
			Player<?>[] selectedPlayers = playerChoiceDialog.getSelectedPlayers();
			String[] selectedPlayerIds = new String[selectedPlayers.length];
			for (int i = 0; i < selectedPlayerIds.length; i++) {
				selectedPlayerIds[i] = selectedPlayers[i].getId();
			}
			getClient().getCommunication().sendUseInducement(InducementType.BRIBES, selectedPlayerIds);
		}
	}

}
