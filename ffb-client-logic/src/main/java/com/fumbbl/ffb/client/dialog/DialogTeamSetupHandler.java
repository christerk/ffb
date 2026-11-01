package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.TeamSetup;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogTeamSetupParameter;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.util.ArrayTool;

/**
 *
 * @author Kalimar
 */
public class DialogTeamSetupHandler extends DialogHandler {

	private DialogTeamSetup teamSetupDialog;
	private String[] setupNames;

	public DialogTeamSetupHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {

		Game game = getClient().getGame();
		DialogTeamSetupParameter dialogTeamSetupParameter = (DialogTeamSetupParameter) game.getDialogParameter();

		if (dialogTeamSetupParameter != null) {

			setupNames = dialogTeamSetupParameter.getSetupNames();
			if (ArrayTool.isProvided(setupNames) || !dialogTeamSetupParameter.isLoadDialog()) {
				teamSetupDialog = new DialogTeamSetup(getClient(), dialogTeamSetupParameter.isLoadDialog(), setupNames);
				setDialog(teamSetupDialog);

			} else {
				setDialog(new DialogInformation(getClient(), "No setups", "There are no setups available for this team.",
						DialogInformation.OK_DIALOG));
			}

			getDialog().showDialog(this);

		}

	}

	public void dialogClosed(IDialog pDialog) {
		hideDialog();
		if (testDialogHasId(pDialog, DialogId.TEAM_SETUP)) {
			DialogTeamSetup closedDialog = (DialogTeamSetup) pDialog;
			if (closedDialog.getUserChoice() == DialogTeamSetup.CHOICE_LOAD) {
				getClient().getCommunication().sendTeamSetupLoad(closedDialog.getSetupName());
			}
			if (closedDialog.getUserChoice() == DialogTeamSetup.CHOICE_DELETE) {
				getClient().getCommunication().sendTeamSetupDelete(closedDialog.getSetupName());
			}
			if (closedDialog.getUserChoice() == DialogTeamSetup.CHOICE_SAVE) {
				// saving under an existing name replaces that setup on the server, so ask first
				if (setupExists(closedDialog.getSetupName())) {
					setDialog(new DialogConfirmOverwriteTeamSetup(getClient(), closedDialog.getSetupName()));
					getDialog().showDialog(this);
					return;
				}
				saveTeamSetup(closedDialog.getSetupName());
			}
			getClient().getGame().setDialogParameter(null);
		} else if (testDialogHasId(pDialog, DialogId.YES_OR_NO_QUESTION)) {
			if (((DialogConfirmOverwriteTeamSetup) pDialog).isChoiceYes()) {
				saveTeamSetup(teamSetupDialog.getSetupName());
				getClient().getGame().setDialogParameter(null);
			} else {
				setDialog(teamSetupDialog);
				teamSetupDialog.showDialog(this);
			}
		}
//    if (testDialogHasId(pDialog, DialogId.INFORMATION)) {
//      UserInterface userInterface = getClient().getUserInterface();
//      userInterface.getSideBarHome().showEndTurnButton();
//    }
	}

	private boolean setupExists(String setupName) {
		if (setupName == null || !ArrayTool.isProvided(setupNames)) {
			return false;
		}
		for (String existingName : setupNames) {
			// the database matches setup names case-insensitively
			if (setupName.equalsIgnoreCase(existingName)) {
				return true;
			}
		}
		return false;
	}

	private void saveTeamSetup(String setupName) {
		Game game = getClient().getGame();
		TeamSetup teamSetup = new TeamSetup();
		teamSetup.setName(setupName);
		teamSetup.setTeamId(game.getTeamHome().getId());
		Player<?>[] homePlayers = game.getTeamHome().getPlayers();
		for (int i = 0; i < homePlayers.length; i++) {
			FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(homePlayers[i]);
			if (FieldCoordinateBounds.HALF_HOME.isInBounds(playerCoordinate)) {
				teamSetup.addCoordinate(playerCoordinate, homePlayers[i].getNr());
			}
		}
		getClient().getCommunication().sendTeamSetupSave(teamSetup);
	}

}
