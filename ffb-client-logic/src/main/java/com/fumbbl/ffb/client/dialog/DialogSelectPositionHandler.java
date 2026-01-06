package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.StatusType;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogSelectPositionParameter;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.RosterPosition;
import com.fumbbl.ffb.model.Team;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DialogSelectPositionHandler extends DialogHandler {

	private DialogSelectPositionParameter dialogParameter;

	public DialogSelectPositionHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {

		Game game = getClient().getGame();
		dialogParameter = (DialogSelectPositionParameter) game.getDialogParameter();

		if (dialogParameter != null) {

			Team team = game.getTeamById(dialogParameter.getTeamId());
			if ((ClientMode.PLAYER == getClient().getMode()) && game.getTeamHome() == team) {
				String dialogHeader = dialogParameter.getPositionChoiceMode().getDialogHeader();
				List<RosterPosition> positions =
					dialogParameter.getPositions().stream().map(id -> team.getRoster().getPositionById(id)).collect(
						Collectors.toList());

				setDialog(new DialogSelectPosition(getClient(), dialogHeader, positions,
					dialogParameter.getMinSelect(), dialogParameter.getMaxSelect(), false));
				getDialog().showDialog(this);

			} else {
				showStatus(dialogParameter.getPositionChoiceMode().getStatusTitle(),
					dialogParameter.getPositionChoiceMode().getStatusMessage(), StatusType.WAITING);
			}
		}
	}

	public void dialogClosed(IDialog pDialog) {
		hideDialog();
		if (testDialogHasId(pDialog, DialogId.SELECT_POSITION)) {
			DialogSelectPosition dialog = (DialogSelectPosition) pDialog;
			String[] positions = Arrays.stream(dialog.getSelectedPositions()).map(RosterPosition::getId).toArray(String[]::new);
			getClient().getCommunication()
				.sendPositionSelection(positions, dialogParameter.getTeamId());
		}
	}

}
