package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.StatusType;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogSkillUseParameter;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

/**
 *
 * @author Kalimar
 */
public class DialogSkillUseHandler extends DialogHandler {

	public DialogSkillUseHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {

		Game game = getClient().getGame();
		DialogSkillUseParameter dialogSkillUseParameter = (DialogSkillUseParameter) game.getDialogParameter();

		if (dialogSkillUseParameter != null) {

			Player<?> player = game.getPlayerById(dialogSkillUseParameter.getPlayerId());

			if ((ClientMode.PLAYER == getClient().getMode()) && game.getTeamHome().hasPlayer(player)) {
				setDialog(DialogSkillUse.create(getClient(), dialogSkillUseParameter));
				getDialog().showDialog(this);
				if (!game.isHomePlaying()) {
					playSound(SoundId.QUESTION);
				}

			} else {
				StringBuilder message = new StringBuilder();
				String skillName = (dialogSkillUseParameter.getSkill() != null) ? dialogSkillUseParameter.getSkill().getName()
						: null;
				message.append("Waiting for coach to use ").append(skillName);
				if (dialogSkillUseParameter.getMinimumRoll() > 0) {
					message.append(" (").append(dialogSkillUseParameter.getMinimumRoll()).append("+ to succeed)");
				}
				message.append(".");
				showStatus("Skill Use", message.toString(), StatusType.WAITING);
			}

		}

	}

	public void dialogClosed(IDialog pDialog) {
		hideDialog();
		if (testDialogHasId(pDialog, DialogId.SKILL_USE)) {
			DialogSkillUse skillUseDialog = (DialogSkillUse) pDialog;
			String playerId = ((DialogSkillUseParameter) getClient().getGame().getDialogParameter()).getPlayerId();
			getClient().getCommunication().sendUseSkill(
				skillUseDialog.isChoiceTwo() ? skillUseDialog.getModiyingSkill() : skillUseDialog.getSkill(),
				skillUseDialog.isChoiceOne() || skillUseDialog.isChoiceTwo(), playerId);
		}
	}

}
