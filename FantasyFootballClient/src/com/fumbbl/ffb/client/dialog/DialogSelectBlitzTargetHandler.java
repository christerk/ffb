package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.IClientProperty;
import com.fumbbl.ffb.client.IClientPropertyValue;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.model.Game;

public class DialogSelectBlitzTargetHandler extends DialogHandler {

	public DialogSelectBlitzTargetHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {

		Game game = getClient().getGame();

		if ((ClientMode.PLAYER == getClient().getMode()) && game.isHomePlaying()) {
			setDialog(new DialogSelectBlitzTarget(getClient(), "Select Blitz target",
				new String[]{"Select the player you intend to blitz or active player again to cancel.", "Once you select an opposing player the action is used."},
				IIconProperty.ACTION_BLITZ, DialogId.SELECT_BLITZ_TARGET, IClientProperty.SETTING_BLITZ_TARGET_PANEL, IClientPropertyValue.SETTING_BLITZ_TARGET_PANEL_OFF));

			getDialog().showDialog(this);

		}
	}

	public void dialogClosed(IDialog pDialog) {
		hideDialog();
	}

}
