package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledAction;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogReRollBlockForTargetsPropertiesParameter;
import com.fumbbl.ffb.factory.ReRollSourceFactory;
import com.fumbbl.ffb.factory.ReRolledActionFactory;
import com.fumbbl.ffb.model.BlockRoll;
import com.fumbbl.ffb.model.BlockRollProperties;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

import java.util.Map;
import java.util.stream.Collectors;

public class DialogReRollBlockForTargetsPropertiesHandler extends DialogHandler {

	public DialogReRollBlockForTargetsPropertiesHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {

		Game game = getClient().getGame();
		DialogReRollBlockForTargetsPropertiesParameter dialogParameter =
			(DialogReRollBlockForTargetsPropertiesParameter) game.getDialogParameter();

		if (dialogParameter != null) {

			Player<?> player = game.getPlayerById(dialogParameter.getPlayerId());

			if ((ClientMode.PLAYER == getClient().getMode()) && game.getTeamHome().hasPlayer(player)) {

				ReRolledActionFactory actionFactory = game.getFactory(FactoryType.Factory.RE_ROLLED_ACTION);
				ReRollSourceFactory sourceFactory = game.getFactory(FactoryType.Factory.RE_ROLL_SOURCE);

				Map<String, Map<ReRolledAction, ReRollSource>> actionToSourceMaps = dialogParameter.getBlockRolls().stream()
					.collect(Collectors.toMap(BlockRollProperties::getTargetId,
						roll -> convertToActionMap(roll.getRrActionToSource(), actionFactory, sourceFactory)));

				setDialog(new DialogReRollBlockForTargetsProperties(getClient(), dialogParameter, actionToSourceMaps));
				getDialog().showDialog(this);

			} else {
				getClient().getClientData().setBlockDiceResult(dialogParameter.getBlockRolls().stream().map(this::map).collect(
					Collectors.toList()));
			}
		}
	}

	public void dialogClosed(IDialog pDialog) {
		getClient().getClientData().clearBlockDiceResult();
		hideDialog();
		if (testDialogHasId(pDialog, DialogId.RE_ROLL_BLOCK_FOR_TARGETS_PROPERTIES)) {
			DialogReRollBlockForTargetsProperties reRollDialog = (DialogReRollBlockForTargetsProperties) pDialog;
			if (reRollDialog.getReRollSource() == ReRollSources.BRAWLER) {
				getClient().getCommunication().sendUseBrawler(reRollDialog.getSelectedTarget());
			} else {
				getClient().getCommunication()
					.sendBlockOrReRollChoiceForTarget(reRollDialog.getSelectedTarget(), reRollDialog.getSelectedIndex(),
						reRollDialog.getReRollSource(), reRollDialog.getProIndex(),
						reRollDialog.getAnyDiceIndexes().stream().mapToInt(i -> i).toArray());
			}
		}
	}


	private Map<ReRolledAction, ReRollSource> convertToActionMap(Map<String, String> input, ReRolledActionFactory actionFactory, ReRollSourceFactory sourceFactory) {
		return input.entrySet().stream().collect(Collectors.toMap(entry -> actionFactory.forName(entry.getKey()),
			entry -> sourceFactory.forName(entry.getValue())));
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
