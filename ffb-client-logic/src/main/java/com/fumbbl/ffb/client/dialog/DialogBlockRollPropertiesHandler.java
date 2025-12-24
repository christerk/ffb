package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.ReRolledAction;
import com.fumbbl.ffb.ReRolledActions;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.StatusType;
import com.fumbbl.ffb.client.ClientData;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.net.ClientCommunication;
import com.fumbbl.ffb.dialog.DialogBlockRollPropertiesParameter;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.factory.ReRollSourceFactory;
import com.fumbbl.ffb.factory.ReRolledActionFactory;
import com.fumbbl.ffb.model.BlockRoll;
import com.fumbbl.ffb.model.Game;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class DialogBlockRollPropertiesHandler extends DialogHandler {

	private DialogBlockRollPropertiesParameter dialogParameter;

	@SuppressWarnings("unused")
	public DialogBlockRollPropertiesHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {

		Game game = getClient().getGame();
		ClientData clientData = getClient().getClientData();
		UserInterface userInterface = getClient().getUserInterface();
		dialogParameter = (DialogBlockRollPropertiesParameter) game.getDialogParameter();

		if (dialogParameter != null) {

			if ((ClientMode.PLAYER == getClient().getMode())
				&& game.getTeamHome().getId().equals(dialogParameter.getChoosingTeamId())) {

				clientData.clearBlockDiceResult();

				ReRolledActionFactory actionFactory = game.getFactory(FactoryType.Factory.RE_ROLLED_ACTION);
				ReRollSourceFactory sourceFactory = game.getFactory(FactoryType.Factory.RE_ROLL_SOURCE);

				setDialog(new DialogBlockRollProperties(getClient(), dialogParameter, convertToActionMap(dialogParameter.getRrActionToSource(), actionFactory, sourceFactory)));

				getDialog().showDialog(this);
				if (!game.isHomePlaying()) {
					playSound(SoundId.QUESTION);
				}

			} else {
				BlockRoll blockRoll = new BlockRoll();
				blockRoll.setBlockRoll(dialogParameter.getBlockRoll());
				blockRoll.setNrOfDice(Math.abs(dialogParameter.getNrOfDice()));
				blockRoll.setOwnChoice(dialogParameter.getNrOfDice() >= 0);
				clientData.setBlockDiceResult(Collections.singletonList(blockRoll));
				if ((dialogParameter.getNrOfDice() < 0) && game.isHomePlaying()) {
					showStatus("Block Roll", "Waiting for coach to choose Block Dice.", StatusType.WAITING);
				}

			}

			userInterface.refreshSideBars();

		}

	}

	private Map<ReRolledAction, ReRollSource> convertToActionMap(Map<String, String> input, ReRolledActionFactory actionFactory, ReRollSourceFactory sourceFactory) {
		return input.entrySet().stream().collect(Collectors.toMap(entry -> actionFactory.forName(entry.getKey()), entry -> sourceFactory.forName(entry.getValue())));
	}

	public void dialogClosed(IDialog pDialog) {
		hideDialog();
		Game game = getClient().getGame();
		ClientData clientData = getClient().getClientData();
		if (testDialogHasId(pDialog, DialogId.BLOCK_ROLL_PROPERTIES)) {
			UserInterface userInterface = getClient().getUserInterface();
			DialogBlockRollProperties blockRollDialog = (DialogBlockRollProperties) pDialog;
			ClientCommunication communication = getClient().getCommunication();
			if (game.getTeamHome().getId().equals(dialogParameter.getChoosingTeamId())) {
				BlockRoll blockRoll = new BlockRoll();
				blockRoll.setBlockRoll(dialogParameter.getBlockRoll());
				blockRoll.setNrOfDice(Math.abs(dialogParameter.getNrOfDice()));
				blockRoll.setOwnChoice(dialogParameter.getNrOfDice() >= 0);
				blockRoll.setSelectedIndex(blockRollDialog.getDiceIndex());
				clientData.setBlockDiceResult(Collections.singletonList(blockRoll));
				if (blockRoll.needsSelection()) {
					if (blockRollDialog.getReRollSource() == ReRollSources.BRAWLER) {
						communication.sendUseBrawler(null);
					} else if (blockRollDialog.getReRollSource() == ReRollSources.PRO) {
						communication.sendUseProReRollForBlock(blockRollDialog.getReRollIndexes().get(0));
					} else if (blockRollDialog.getReRollSource() != null && blockRollDialog.getReRollSource() == blockRollDialog.getSingleDieReRollSource()) {
						communication.sendUseConsummateReRollForBlock(blockRollDialog.getReRollIndexes().get(0));
					} else if (blockRollDialog.getReRollSource() != null && blockRollDialog.getReRollSource() == blockRollDialog.getSingleBlockDieReRollSource()) {
						communication.sendUseSingleBlockDieReRollForBlock(blockRollDialog.getReRollIndexes().get(0));
					} else if (blockRollDialog.getReRollSource() != null && blockRollDialog.getReRollSource() == blockRollDialog.getAnyBlockDiceReRollSource()) {
						communication.sendUseMultiBlockDiceReRoll(blockRollDialog.getReRollIndexes().stream().mapToInt(i -> i).toArray());
					} else {
						communication.sendUseReRoll(ReRolledActions.BLOCK, blockRollDialog.getReRollSource());
					}
				} else {
					communication.sendBlockChoice(blockRollDialog.getDiceIndex());
				}
				userInterface.refreshSideBars();
			}
		}
	}

}
