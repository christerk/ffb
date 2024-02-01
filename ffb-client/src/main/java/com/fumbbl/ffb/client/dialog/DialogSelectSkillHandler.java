package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.StatusType;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogSelectSkillParameter;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.skill.Skill;

import java.util.List;

public class DialogSelectSkillHandler extends DialogHandler {

	private DialogSelectSkillParameter dialogParameter;

	public DialogSelectSkillHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {

		Game game = getClient().getGame();
		dialogParameter = (DialogSelectSkillParameter) game.getDialogParameter();

		if (dialogParameter != null) {

			Player<?> player = game.getPlayerById(dialogParameter.getPlayerId());
			if ((ClientMode.PLAYER == getClient().getMode())
				&& game.getTeamHome().hasPlayer(player)) {
				String dialogHeader = dialogParameter.getSkillChoiceMode().getDialogHeader(player.getName());
				List<Skill> skills = dialogParameter.getSkills();

				setDialog(new DialogSelectSkill(getClient(), dialogHeader, skills,
					1, 1, false));
				getDialog().showDialog(this);

			} else {
				showStatus(dialogParameter.getSkillChoiceMode().getStatusTitle(),
					dialogParameter.getSkillChoiceMode().getStatusMessage(), StatusType.WAITING);
			}

		}

	}

	public void dialogClosed(IDialog pDialog) {
		hideDialog();
		if (testDialogHasId(pDialog, DialogId.PLAYER_CHOICE)) {
			DialogSelectSkill skillChoiceDialog = (DialogSelectSkill) pDialog;
			getClient().getCommunication().sendSkillSelection(dialogParameter.getPlayerId(), skillChoiceDialog.getSelectedSkills()[0]);
		}
	}

}
