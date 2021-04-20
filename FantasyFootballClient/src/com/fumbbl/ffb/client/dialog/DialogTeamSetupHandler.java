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

	public DialogTeamSetupHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {

		Game game = getClient().getGame();
		DialogTeamSetupParameter dialogTeamSetupParameter = (DialogTeamSetupParameter) game.getDialogParameter();

		if (dialogTeamSetupParameter != null) {

			String[] setupNames = dialogTeamSetupParameter.getSetupNames();
			if (ArrayTool.isProvided(setupNames) || !dialogTeamSetupParameter.isLoadDialog()) {
				setDialog(new DialogTeamSetup(getClient(), dialogTeamSetupParameter.isLoadDialog(), setupNames));

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
			DialogTeamSetup teamSetupDialog = (DialogTeamSetup) pDialog;
			if (teamSetupDialog.getUserChoice() == DialogTeamSetup.CHOICE_LOAD) {
				getClient().getCommunication().sendTeamSetupLoad(teamSetupDialog.getSetupName());
			}
			if (teamSetupDialog.getUserChoice() == DialogTeamSetup.CHOICE_DELETE) {
				getClient().getCommunication().sendTeamSetupDelete(teamSetupDialog.getSetupName());
			}
			if (teamSetupDialog.getUserChoice() == DialogTeamSetup.CHOICE_SAVE) {
				Game game = getClient().getGame();
				TeamSetup teamSetup = new TeamSetup();
				teamSetup.setName(teamSetupDialog.getSetupName());
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
			Game game = getClient().getGame();
			game.setDialogParameter(null);
		}
//    if (testDialogHasId(pDialog, DialogId.INFORMATION)) {
//      UserInterface userInterface = getClient().getUserInterface();
//      userInterface.getSideBarHome().showEndTurnButton();
//    }
	}

}
