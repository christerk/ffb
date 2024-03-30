package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.StatusType;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.layer.FieldLayerRangeRuler;
import com.fumbbl.ffb.client.state.ClientState;
import com.fumbbl.ffb.client.state.ClientStateInterception;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogInterceptionParameter;
import com.fumbbl.ffb.mechanics.AgilityMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.Wording;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.util.UtilPassing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Kalimar
 */
public class DialogInterceptionHandler extends DialogHandler {

	private Skill interceptionSkill;

	public DialogInterceptionHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {

		Game game = getClient().getGame();
		DialogInterceptionParameter dialogParameter = (DialogInterceptionParameter) game.getDialogParameter();
		Player<?> thrower = game.getPlayerById(dialogParameter.getThrowerId());

		if ((ClientMode.PLAYER != getClient().getMode()) || game.getTeamHome().hasPlayer(thrower)) {
			Wording wording = ((AgilityMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.AGILITY.name())).interceptionWording(false);

			showStatus(wording.getNoun(), "Waiting for coach to choose an " + wording.getPlayerCharacterization() + ".", StatusType.WAITING);

		} else {
			String skillText = null;
			interceptionSkill = dialogParameter.getInterceptionSkill();
			if (interceptionSkill != null) {
				skillText = "Use " + dialogParameter.getInterceptionSkill().getName();
			}
			setDialog(new DialogInterception(getClient(), skillText, dialogParameter.getSkillMnemonic()));
			getDialog().showDialog(this);
			if (!game.isHomePlaying()) {
				playSound(SoundId.QUESTION);
			}
		}

	}

	public void dialogClosed(IDialog pDialog) {
		hideDialog();
		Game game = getClient().getGame();
		if (testDialogHasId(pDialog, DialogId.INTERCEPTION)) {
			DialogInterception interceptionDialog = (DialogInterception) pDialog;
			if (!interceptionDialog.isChoiceYes() && !interceptionDialog.isChoiceTwo()) {
				getClient().getCommunication().sendInterceptorChoice(null, null);
			} else {
				// auto-choose lone interceptor
				Player<?>[] interceptors = UtilPassing.findInterceptors(game, game.getThrower(), game.getPassCoordinate());

				List<Player<?>> interceptorsWithSkill = new ArrayList<>();

				Player<?> singleInterceptor = null;

				if (interceptors.length == 1) {
					singleInterceptor = interceptors[0];
				}

				if (interceptionDialog.isChoiceTwo() && interceptionSkill != null) {
					Arrays.stream(interceptors).filter(player -> player.hasUnused(interceptionSkill)).forEach(interceptorsWithSkill::add);
					if (interceptorsWithSkill.size() == 1) {
						singleInterceptor = interceptorsWithSkill.get(0);
					} else if (interceptorsWithSkill.isEmpty()) {
						interceptionSkill = null;
					} else {
						singleInterceptor = null;
						ClientState clientState = getClient().getClientState();

						if (clientState instanceof ClientStateInterception) {
							((ClientStateInterception) clientState).setInterceptionSkill(interceptionSkill);
						}

						FieldLayerRangeRuler layerRangeRuler = getClient().getUserInterface().getFieldComponent().getLayerRangeRuler();

						layerRangeRuler.clearMarkedCoordinates();
						layerRangeRuler.markPlayers(interceptorsWithSkill.toArray(new Player<?>[0]), FieldLayerRangeRuler.COLOR_INTERCEPTION);
						getClient().getUserInterface().getFieldComponent().refresh();

					}
				} else {
					interceptionSkill = null;
				}

				if (singleInterceptor != null) {
					getClient().getCommunication().sendInterceptorChoice(singleInterceptor, interceptionSkill);
				}
			}
		}
		game.setWaitingForOpponent(false);
		getClient().updateClientState();
	}

}
