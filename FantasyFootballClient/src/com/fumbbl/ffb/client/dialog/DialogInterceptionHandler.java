package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.StatusType;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogInterceptionParameter;
import com.fumbbl.ffb.mechanics.AgilityMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.Wording;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.util.UtilPassing;

/**
 *
 * @author Kalimar
 */
public class DialogInterceptionHandler extends DialogHandler {

	public DialogInterceptionHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {

		Game game = getClient().getGame();
		DialogInterceptionParameter dialogParameter = (DialogInterceptionParameter) game.getDialogParameter();
		Player<?> thrower = game.getPlayerById(dialogParameter.getThrowerId());

		if ((ClientMode.PLAYER != getClient().getMode()) || game.getTeamHome().hasPlayer(thrower)) {
			Wording wording = ((AgilityMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.AGILITY.name())).interceptionWording();

			showStatus(wording.getNoun(), "Waiting for coach to choose an " + wording.getPlayerCharacterization() + ".", StatusType.WAITING);

		} else {
			setDialog(new DialogInterception(getClient()));
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
			if (!interceptionDialog.isChoiceYes()) {
				getClient().getCommunication().sendInterceptorChoice(null);
			} else {
				// auto-choose lone interceptor
				Player<?>[] interceptors = UtilPassing.findInterceptors(game, game.getThrower(), game.getPassCoordinate());
				if (interceptors.length == 1) {
					getClient().getCommunication().sendInterceptorChoice(interceptors[0]);
				}
			}
		}
		game.setWaitingForOpponent(false);
		getClient().updateClientState();
	}

}
