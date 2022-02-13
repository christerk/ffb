package com.fumbbl.ffb.client.dialog;

import java.awt.Color;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldMarker;
import com.fumbbl.ffb.StatusType;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogKickSkillParameter;
import com.fumbbl.ffb.dialog.DialogSkillUseParameter;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

/**
 *
 * @author Kalimar
 */
public class DialogKickSkillHandler extends DialogHandler {

	private static final Color _MARKED_FIELDS_COLOR = new Color(0.8f, 0.8f, 0.8f, 0.4f);

	public DialogKickSkillHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	private FieldMarker kickMarker;

	public void showDialog() {

		Game game = getClient().getGame();
		UserInterface userInterface = getClient().getUserInterface();
		DialogKickSkillParameter dialogKickSkillParameter = (DialogKickSkillParameter) game.getDialogParameter();

		if (dialogKickSkillParameter != null) {

			Player<?> player = game.getPlayerById(dialogKickSkillParameter.getPlayerId());

			Skill skillReduceKickDistance = player.getSkillWithProperty(NamedProperties.canReduceKickDistance);

			if ((ClientMode.PLAYER == getClient().getMode()) && game.getTeamHome().hasPlayer(player)
					&& skillReduceKickDistance != null) {

				userInterface.getFieldComponent().getLayerRangeRuler().markCoordinates(new FieldCoordinate[] {
					dialogKickSkillParameter.getBallCoordinateWithKick(), dialogKickSkillParameter.getBallCoordinate() },
						_MARKED_FIELDS_COLOR);
				kickMarker = new FieldMarker(dialogKickSkillParameter.getBallCoordinateWithKick(), "K", "");
				userInterface.getFieldComponent().getLayerMarker().drawFieldMarker(kickMarker, true);
				userInterface.getFieldComponent().refresh();

				setDialog(
						new DialogSkillUse(getClient(), new DialogSkillUseParameter(player.getId(), skillReduceKickDistance, 0)));
				getDialog().showDialog(this);

			} else if (skillReduceKickDistance != null) {

				showStatus("Skill Use", "Waiting for coach to use " + skillReduceKickDistance.getName() + ".", StatusType.WAITING);

			}

		}

	}

	public void dialogClosed(IDialog pDialog) {
		hideDialog();
		if (testDialogHasId(pDialog, DialogId.SKILL_USE)) {
			UserInterface userInterface = getClient().getUserInterface();
			userInterface.getFieldComponent().getLayerRangeRuler().clearMarkedCoordinates();
			if (kickMarker != null) {
				userInterface.getFieldComponent().getLayerMarker().removeFieldMarker(kickMarker);
			}
			userInterface.getFieldComponent().refresh();
			DialogSkillUse skillUseDialog = (DialogSkillUse) pDialog;
			String playerId = ((DialogKickSkillParameter) getClient().getGame().getDialogParameter()).getPlayerId();
			getClient().getCommunication().sendUseSkill(skillUseDialog.getSkill(), skillUseDialog.isChoiceYes(), playerId);
		}
	}

}
