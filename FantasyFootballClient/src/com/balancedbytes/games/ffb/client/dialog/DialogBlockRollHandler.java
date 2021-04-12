package com.balancedbytes.games.ffb.client.dialog;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.ReRolledActions;
import com.balancedbytes.games.ffb.SoundId;
import com.balancedbytes.games.ffb.StatusType;
import com.balancedbytes.games.ffb.client.ClientData;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.UserInterface;
import com.balancedbytes.games.ffb.client.net.ClientCommunication;
import com.balancedbytes.games.ffb.dialog.DialogBlockRollParameter;
import com.balancedbytes.games.ffb.dialog.DialogId;
import com.balancedbytes.games.ffb.model.BlockRoll;
import com.balancedbytes.games.ffb.model.Game;

import java.util.Collections;

/**
 * 
 * @author Kalimar
 */
public class DialogBlockRollHandler extends DialogHandler {

	private DialogBlockRollParameter fDialogParameter;

	public DialogBlockRollHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {

		Game game = getClient().getGame();
		ClientData clientData = getClient().getClientData();
		UserInterface userInterface = getClient().getUserInterface();
		fDialogParameter = (DialogBlockRollParameter) game.getDialogParameter();

		if (fDialogParameter != null) {

			if ((ClientMode.PLAYER == getClient().getMode())
					&& game.getTeamHome().getId().equals(fDialogParameter.getChoosingTeamId())) {

				clientData.clearBlockDiceResult();
				setDialog(new DialogBlockRoll(getClient(), fDialogParameter));
				getDialog().showDialog(this);
				if (!game.isHomePlaying()) {
					playSound(SoundId.QUESTION);
				}

			} else {
				BlockRoll blockRoll = new BlockRoll();
				blockRoll.setBlockRoll(fDialogParameter.getBlockRoll());
				blockRoll.setNrOfDice(Math.abs(fDialogParameter.getNrOfDice()));
				blockRoll.setOwnChoice(fDialogParameter.getNrOfDice() >= 0);
				clientData.setBlockDiceResult(Collections.singletonList(blockRoll));
				if ((fDialogParameter.getNrOfDice() < 0) && game.isHomePlaying()) {
					showStatus("Block Roll", "Waiting for coach to choose Block Dice.", StatusType.WAITING);
				}

			}

			userInterface.refreshSideBars();

		}

	}

	public void dialogClosed(IDialog pDialog) {
		hideDialog();
		Game game = getClient().getGame();
		ClientData clientData = getClient().getClientData();
		if (testDialogHasId(pDialog, DialogId.BLOCK_ROLL)) {
			UserInterface userInterface = getClient().getUserInterface();
			DialogBlockRoll blockRollDialog = (DialogBlockRoll) pDialog;
			ClientCommunication communication = getClient().getCommunication();
			if (game.getTeamHome().getId().equals(fDialogParameter.getChoosingTeamId())) {
				BlockRoll blockRoll = new BlockRoll();
				blockRoll.setBlockRoll(fDialogParameter.getBlockRoll());
				blockRoll.setNrOfDice(Math.abs(fDialogParameter.getNrOfDice()));
				blockRoll.setOwnChoice(fDialogParameter.getNrOfDice() >= 0);
				blockRoll.setSelectedIndex(blockRollDialog.getDiceIndex());
				clientData.setBlockDiceResult(Collections.singletonList(blockRoll));
				if (blockRoll.needsSelection()) {
					communication.sendUseReRoll(ReRolledActions.BLOCK, blockRollDialog.getReRollSource());
				} else {
					communication.sendBlockChoice(blockRollDialog.getDiceIndex());
				}
				userInterface.refreshSideBars();
			}
		}
	}

}
