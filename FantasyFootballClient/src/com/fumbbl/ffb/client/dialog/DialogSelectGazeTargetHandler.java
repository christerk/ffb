package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.model.Game;

public class DialogSelectGazeTargetHandler extends DialogHandler {

	public DialogSelectGazeTargetHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {

		Game game = getClient().getGame();

		if ((ClientMode.PLAYER == getClient().getMode()) && game.isHomePlaying()) {
			setDialog(new DialogSelectBlitzTarget(getClient(), "Select Gaze target",
				new String[]{"Select the player you intend to gaze or active player again to cancel.", "Once you select an opposing player the activation is used."},
				DialogSelectBlitzTarget.OK_DIALOG, IIconProperty.ACTION_BLITZ));
			getDialog().showDialog(this);

		}
	}

	public void dialogClosed(IDialog pDialog) {
		hideDialog();
	}

}
