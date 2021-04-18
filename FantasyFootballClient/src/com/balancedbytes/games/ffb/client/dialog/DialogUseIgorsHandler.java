package com.balancedbytes.games.ffb.client.dialog;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.StatusType;
import com.balancedbytes.games.ffb.bb2020.InjuryDescription;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.dialog.DialogId;
import com.balancedbytes.games.ffb.dialog.DialogUseIgorParameter;
import com.balancedbytes.games.ffb.dialog.DialogUseIgorsParameter;
import com.balancedbytes.games.ffb.model.Game;

import java.util.ArrayList;
import java.util.List;

public class DialogUseIgorsHandler extends DialogHandler {

	public DialogUseIgorsHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {

		Game game = getClient().getGame();

		DialogUseIgorsParameter dialogParameter = (DialogUseIgorsParameter) game.getDialogParameter();
		if (dialogParameter != null) {

			if ((ClientMode.PLAYER == getClient().getMode())
					&& game.getTeamHome().getId().equals(dialogParameter.getTeamId())) {

				if (dialogParameter.getInjuryDescriptions().size() == 1) {
					InjuryDescription description = dialogParameter.getInjuryDescriptions().get(0);
					setDialog(new DialogUseIgor(getClient(), new DialogUseIgorParameter(description.getPlayerId())));

				} else {
					List<String> playerIds = new ArrayList<>();
					List<String> descriptions = new ArrayList<>();
					dialogParameter.getInjuryDescriptions().forEach(injuryDescription -> {
						playerIds.add(injuryDescription.getPlayerId());
						descriptions.add(injuryDescription.getSeriousInjury() != null ? injuryDescription.getSeriousInjury().getDescription() : injuryDescription.getPlayerState().getDescription());
					});
					setDialog(new DialogPlayerChoice(getClient(), "Select players to use igor for",
							playerIds.toArray(new String[0]), descriptions.toArray(new String[0]), 0, dialogParameter.getMaxIgors(), null, false));
				}

				getDialog().showDialog(this);

			} else {
				showStatus("Igor", "Waiting for coach to use Igors.", StatusType.WAITING);
			}

		}

	}

	public void dialogClosed(IDialog pDialog) {
		hideDialog();
		List<Integer> selectedIndexes = new ArrayList<>();
		if (testDialogHasId(pDialog, DialogId.USE_IGOR)) {
			DialogUseIgor dialog = (DialogUseIgor) pDialog;
			if (dialog.isChoiceYes()) {
				selectedIndexes.add(0);
			}
		}
		if (testDialogHasId(pDialog, DialogId.PLAYER_CHOICE)) {
			DialogPlayerChoice playerChoiceDialog = (DialogPlayerChoice) pDialog;
			selectedIndexes.addAll(playerChoiceDialog.getSelectedIndexes());
		}

		Game game = getClient().getGame();
		DialogUseIgorsParameter dialogParameter = (DialogUseIgorsParameter) game.getDialogParameter();

		List<InjuryDescription> allInjuries = dialogParameter.getInjuryDescriptions();
		List<InjuryDescription> selectedInjuries = new ArrayList<>();
		selectedIndexes.forEach(index -> selectedInjuries.add(allInjuries.get(index)));

		getClient().getCommunication().sendUseIgors(selectedInjuries);
	}

}
