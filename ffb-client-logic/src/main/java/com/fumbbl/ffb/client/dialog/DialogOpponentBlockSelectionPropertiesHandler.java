package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.StatusType;
import com.fumbbl.ffb.client.ClientData;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogOpponentBlockSelectionPropertiesParameter;
import com.fumbbl.ffb.model.BlockRollProperties;
import com.fumbbl.ffb.model.BlockRoll;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Team;

import java.util.stream.Collectors;

public class DialogOpponentBlockSelectionPropertiesHandler extends DialogHandler {

	public DialogOpponentBlockSelectionPropertiesHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {

		Game game = getClient().getGame();
		DialogOpponentBlockSelectionPropertiesParameter dialogParameter = (DialogOpponentBlockSelectionPropertiesParameter) game.getDialogParameter();

		if (dialogParameter != null) {

			Team team = game.getTeamById(dialogParameter.getTeamId());
			ClientData clientData = getClient().getClientData();

			if ((ClientMode.PLAYER == getClient().getMode()) && game.getTeamHome() == team) {
				clientData.clearBlockDiceResult();

				setDialog(new DialogOpponentBlockSelectionProperties(getClient(), dialogParameter));
				getDialog().showDialog(this);
				playSound(SoundId.QUESTION);

			} else {
				clientData.setBlockDiceResult(dialogParameter.getBlockRolls().stream().map(this::map).collect(Collectors.toList()));
				showStatus("Select Block Results", "Waiting for coach to select block results.", StatusType.WAITING);
			}
		}
	}

	public void dialogClosed(IDialog pDialog) {
		getClient().getClientData().clearBlockDiceResult();
		hideDialog();
		if (testDialogHasId(pDialog, DialogId.OPPONENT_BLOCK_SELECTION_PROPERTIES)) {
			DialogOpponentBlockSelectionProperties reRollDialog = (DialogOpponentBlockSelectionProperties) pDialog;
			getClient().getCommunication().sendBlockOrReRollChoiceForTarget(reRollDialog.getSelectedTarget(), reRollDialog.getSelectedIndex(), null, 0);
		}
	}

	private BlockRoll map(BlockRollProperties input) {
		BlockRoll output = new BlockRoll();
		output.setBlockRoll(input.getBlockRoll());
		output.setSelectedIndex(input.getSelectedIndex());
		output.setOwnChoice(input.isOwnChoice());
		output.setNrOfDice(input.getNrOfDice());
		output.setDoubleTargetStrength(input.isDoubleTargetStrength());
		output.setProIndex(input.getProIndex());
		output.setSuccessFulDauntless(input.isSuccessFulDauntless());
		// reRollDiceIndexes are not mapped as they are used differently in the two classes
		// only the first 4 properties should be relevant anyway for the client side handling
		return output;
	}
}
