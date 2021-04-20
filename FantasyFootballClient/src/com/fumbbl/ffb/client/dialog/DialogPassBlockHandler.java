package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.StatusType;
import com.fumbbl.ffb.client.FantasyFootballClient;
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

		if ((ClientMode.PLAYER != getClient().getMode()) || !game.isHomePlaying()) {
			showStatus("Pass Block", "Waiting for coach to move pass blockers.", StatusType.WAITING);

		} else {

			setDialog(new DialogInformation(getClient(), "Pass Block",
					new String[] { "You may move your players with PASS BLOCK skill up to 3 squares.",
							"The move must end in a square where the player can intercept or put a TZ on thrower or catcher." },
					DialogInformation.OK_DIALOG, IIconProperty.GAME_REF));
			getDialog().showDialog(this);
			playSound(SoundId.QUESTION);

		}

	}

	public void dialogClosed(IDialog pDialog) {
		hideDialog();
	}

}
