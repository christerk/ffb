package com.balancedbytes.games.ffb.client.dialog;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.PlayerChoiceMode;
import com.balancedbytes.games.ffb.StatusType;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.dialog.DialogId;
import com.balancedbytes.games.ffb.dialog.DialogPlayerChoiceParameter;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;

/**
 * 
 * @author Kalimar
 */
public class DialogPlayerChoiceHandler extends DialogHandler {

	private DialogPlayerChoiceParameter fDialogParameter;

	public DialogPlayerChoiceHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {

		Game game = getClient().getGame();
		fDialogParameter = (DialogPlayerChoiceParameter) game.getDialogParameter();

		if (fDialogParameter != null) {

			if ((ClientMode.PLAYER == getClient().getMode())
					&& game.getTeamHome().getId().equals(fDialogParameter.getTeamId())) {
				String dialogHeader = fDialogParameter.getPlayerChoiceMode().getDialogHeader(fDialogParameter.getMaxSelects());
				FieldCoordinate dialogCoordinate = null;
				String[] playerIds = fDialogParameter.getPlayerIds();
				int minSelects = 0;
				if (fDialogParameter.getPlayerChoiceMode() == PlayerChoiceMode.MVP) {
					minSelects = fDialogParameter.getMaxSelects();
				} else {
					if (fDialogParameter.getPlayerChoiceMode() != PlayerChoiceMode.CARD) {
						int maxX = 0, maxY = 0;
						for (int i = 0; i < playerIds.length; i++) {
							Player player = game.getPlayerById(playerIds[i]);
							FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(player);
							if (playerCoordinate.getX() > maxX) {
								maxX = playerCoordinate.getX();
							}
							if (playerCoordinate.getY() > maxY) {
								maxY = playerCoordinate.getY();
							}
						}
						dialogCoordinate = new FieldCoordinate(maxX, maxY);
					}
				}
				setDialog(new DialogPlayerChoice(getClient(), dialogHeader, playerIds, fDialogParameter.getDescriptions(),
						minSelects, fDialogParameter.getMaxSelects(), dialogCoordinate, false));
				getDialog().showDialog(this);

			} else {
				showStatus(fDialogParameter.getPlayerChoiceMode().getStatusTitle(),
						fDialogParameter.getPlayerChoiceMode().getStatusMessage(), StatusType.WAITING);
			}

		}

	}

	public void dialogClosed(IDialog pDialog) {
		hideDialog();
		if (testDialogHasId(pDialog, DialogId.PLAYER_CHOICE)) {
			DialogPlayerChoice playerChoiceDialog = (DialogPlayerChoice) pDialog;
			getClient().getCommunication().sendPlayerChoice(fDialogParameter.getPlayerChoiceMode(),
					playerChoiceDialog.getSelectedPlayers());
		}
	}

}
