package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.StatusType;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogPlayerChoiceParameter;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

/**
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

				if (fDialogParameter.getPlayerChoiceMode().isUsePlayerPosition()) {
					int maxX = 0, maxY = 0;
					for (String playerId : playerIds) {
						Player<?> player = game.getPlayerById(playerId);
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

				setDialog(new DialogPlayerChoice(getClient(), dialogHeader, playerIds, fDialogParameter.getDescriptions(),
					fDialogParameter.getMinSelects(), fDialogParameter.getMaxSelects(), dialogCoordinate, false));
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
