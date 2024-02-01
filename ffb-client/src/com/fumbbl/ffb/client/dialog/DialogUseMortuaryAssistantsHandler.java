package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.StatusType;
import com.fumbbl.ffb.bb2020.InjuryDescription;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogUseMortuaryAssistantParameter;
import com.fumbbl.ffb.dialog.DialogUseMortuaryAssistantsParameter;
import com.fumbbl.ffb.model.Game;

import java.util.ArrayList;
import java.util.List;

public class DialogUseMortuaryAssistantsHandler extends DialogHandler {

	public DialogUseMortuaryAssistantsHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {

		Game game = getClient().getGame();

		DialogUseMortuaryAssistantsParameter dialogParameter = (DialogUseMortuaryAssistantsParameter) game.getDialogParameter();
		if (dialogParameter != null) {

			if ((ClientMode.PLAYER == getClient().getMode())
				&& game.getTeamHome().getId().equals(dialogParameter.getTeamId())) {

				if (dialogParameter.getInjuryDescriptions().size() == 1) {
					InjuryDescription description = dialogParameter.getInjuryDescriptions().get(0);
					setDialog(new DialogUseMortuaryAssistant(getClient(), new DialogUseMortuaryAssistantParameter(description.getPlayerId())));

				} else {
					List<String> playerIds = new ArrayList<>();
					List<String> descriptions = new ArrayList<>();
					dialogParameter.getInjuryDescriptions().forEach(injuryDescription -> {
						playerIds.add(injuryDescription.getPlayerId());
						descriptions.add(injuryDescription.getSeriousInjury() != null ? injuryDescription.getSeriousInjury().getDescription() : injuryDescription.getPlayerState().getDescription());
					});
					setDialog(new DialogPlayerChoice(getClient(), "Select players to use a Mortuary Assistant (or Plague Doctor) for",
						playerIds.toArray(new String[0]), descriptions.toArray(new String[0]), 0, dialogParameter.getMaxMortuaryAssistants(), null, false));
				}

				getDialog().showDialog(this);

			} else {
				showStatus("Mortuary Assistant", "Waiting for coach to use Mortuary Assistants (or Plague Doctors).", StatusType.WAITING);
			}

		}

	}

	public void dialogClosed(IDialog pDialog) {
		hideDialog();
		List<Integer> selectedIndexes = new ArrayList<>();
		if (testDialogHasId(pDialog, DialogId.USE_MORTUARY_ASSISTANT)) {
			DialogUseMortuaryAssistant dialog = (DialogUseMortuaryAssistant) pDialog;
			if (dialog.isChoiceYes()) {
				selectedIndexes.add(0);
			}
		}
		if (testDialogHasId(pDialog, DialogId.PLAYER_CHOICE)) {
			DialogPlayerChoice playerChoiceDialog = (DialogPlayerChoice) pDialog;
			selectedIndexes.addAll(playerChoiceDialog.getSelectedIndexes());
		}

		Game game = getClient().getGame();
		DialogUseMortuaryAssistantsParameter dialogParameter = (DialogUseMortuaryAssistantsParameter) game.getDialogParameter();

		List<InjuryDescription> allInjuries = dialogParameter.getInjuryDescriptions();
		List<InjuryDescription> selectedInjuries = new ArrayList<>();
		selectedIndexes.forEach(index -> selectedInjuries.add(allInjuries.get(index)));

		getClient().getCommunication().sendUseIgors(selectedInjuries);
	}

}
