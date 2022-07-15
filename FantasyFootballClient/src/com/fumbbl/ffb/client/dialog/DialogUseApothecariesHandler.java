package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.StatusType;
import com.fumbbl.ffb.bb2020.InjuryDescription;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogUseApothecariesParameter;
import com.fumbbl.ffb.dialog.DialogUseApothecaryParameter;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.util.StringTool;

import java.util.Collections;
import java.util.List;

public class DialogUseApothecariesHandler extends DialogHandler {

	public DialogUseApothecariesHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {

		Game game = getClient().getGame();

		DialogUseApothecariesParameter dialogParameter = (DialogUseApothecariesParameter) game.getDialogParameter();
		if (dialogParameter != null) {

			if ((ClientMode.PLAYER == getClient().getMode())
					&& game.getTeamHome().getId().equals(dialogParameter.getTeamId())) {

				if (dialogParameter.getInjuryDescriptions().size() == 1) {
					InjuryDescription description = dialogParameter.getInjuryDescriptions().get(0);
					setDialog(new DialogUseApothecary(getClient(), new DialogUseApothecaryParameter(description.getPlayerId(), description.getPlayerState(), description.getSeriousInjury(), description.getApothecaryType())));

				} else {
					setDialog(new DialogUseApothecaries(getClient(), dialogParameter));
				}

				getDialog().showDialog(this);

			} else {
				showStatus("Apothecary", "Waiting for coach to use Apothecaries.", StatusType.WAITING);
			}
		}
	}

	public void dialogClosed(IDialog pDialog) {
		hideDialog();

		Game game = getClient().getGame();
		DialogUseApothecariesParameter dialogParameter = (DialogUseApothecariesParameter) game.getDialogParameter();
		List<InjuryDescription> allInjuries = dialogParameter.getInjuryDescriptions();

		String playerId = null;

		if (testDialogHasId(pDialog, DialogId.USE_APOTHECARY)) {
			DialogUseApothecary dialog = (DialogUseApothecary) pDialog;
			if (dialog.isChoiceYes()) {
				playerId = allInjuries.get(0).getPlayerId();
			}
		} else if (testDialogHasId(pDialog, DialogId.USE_APOTHECARIES)) {
			DialogUseApothecaries useApothecaries = (DialogUseApothecaries) pDialog;
			playerId = useApothecaries.getSelectedPlayer();
		}
		if (StringTool.isProvided(playerId)) {
			getClient().getCommunication().sendUseApothecary(playerId, true);
		} else {
			getClient().getCommunication().sendUseApothecaries(Collections.emptyList());
		}
	}

}
