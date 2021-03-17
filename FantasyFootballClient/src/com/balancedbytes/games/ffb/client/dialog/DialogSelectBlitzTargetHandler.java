package com.balancedbytes.games.ffb.client.dialog;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.IIconProperty;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.model.Game;

/**
 * 
 * @author Kalimar
 */
public class DialogSelectBlitzTargetHandler extends DialogHandler {

	public DialogSelectBlitzTargetHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {

		Game game = getClient().getGame();

		if ((ClientMode.PLAYER == getClient().getMode()) && game.isHomePlaying()) {
			setDialog(new DialogInformation(getClient(), "Select Blitz target",
					new String[] { "Select the player you intend to blitz or active player again to cancel." },
					DialogInformation.OK_DIALOG, IIconProperty.ACTION_BLITZ));
			getDialog().showDialog(this);

		}
	}

	public void dialogClosed(IDialog pDialog) {
		hideDialog();
	}

}
