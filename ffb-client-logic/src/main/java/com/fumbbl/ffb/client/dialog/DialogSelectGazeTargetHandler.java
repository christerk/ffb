package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.IClientPropertyValue;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.model.Game;

public class DialogSelectGazeTargetHandler extends DialogHandler {

	public DialogSelectGazeTargetHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {

		Game game = getClient().getGame();

		if ((ClientMode.PLAYER == getClient().getMode()) && game.isHomePlaying()) {
			setDialog(new DialogSelectTarget(getClient(), "Select Gaze target",
				new String[]{"Select the player you intend to gaze or active player again to cancel.", "The action can only be cancelled as long as no dice have been rolled."},
				IIconProperty.ACTION_GAZE, DialogId.SELECT_GAZE_TARGET, CommonProperty.SETTING_GAZE_TARGET_PANEL, IClientPropertyValue.SETTING_GAZE_TARGET_PANEL_OFF));
			getDialog().showDialog(this);

		}
	}

	public void dialogClosed(IDialog pDialog) {
		hideDialog();
	}

}
