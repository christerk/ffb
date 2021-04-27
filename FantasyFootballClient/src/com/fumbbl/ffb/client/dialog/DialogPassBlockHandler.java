package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.StatusType;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.OnTheBallMechanic;
import com.fumbbl.ffb.model.Game;

/**
 * 
 * @author Kalimar
 */
public class DialogPassBlockHandler extends DialogHandler {

	public DialogPassBlockHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {

		Game game = getClient().getGame();

		OnTheBallMechanic onTheBallMechanic = (OnTheBallMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.ON_THE_BALL.name());
		String displayName = onTheBallMechanic.displayStringKickOffInterference();

		if ((ClientMode.PLAYER != getClient().getMode()) || !game.isHomePlaying()) {
			showStatus(displayName, onTheBallMechanic.passInterferenceStatusDescription(), StatusType.WAITING);

		} else {

			setDialog(new DialogInformation(getClient(), displayName, onTheBallMechanic.passInterferenceDialogDescription(),
					DialogInformation.OK_DIALOG, IIconProperty.GAME_REF));
			getDialog().showDialog(this);
			playSound(SoundId.QUESTION);

		}

	}

	public void dialogClosed(IDialog pDialog) {
		hideDialog();
	}

}
