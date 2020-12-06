package com.balancedbytes.games.ffb.client.dialog;

import com.balancedbytes.games.ffb.BoxType;
import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.ui.SideBarComponent;
import com.balancedbytes.games.ffb.dialog.DialogSetupErrorParameter;
import com.balancedbytes.games.ffb.model.Game;

/**
 * 
 * @author Kalimar
 */
public class DialogSetupErrorHandler extends DialogHandler {

	public DialogSetupErrorHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {
		Game game = getClient().getGame();
		DialogSetupErrorParameter dialogSetupErrorParameter = (DialogSetupErrorParameter) game.getDialogParameter();
		if ((dialogSetupErrorParameter != null) && game.getTeamHome().getId().equals(dialogSetupErrorParameter.getTeamId())
				&& (ClientMode.PLAYER == getClient().getMode())) {
			SideBarComponent sideBarHome = getClient().getUserInterface().getSideBarHome();
			sideBarHome.openBox(BoxType.RESERVES);
			setDialog(new DialogSetupError(getClient(), dialogSetupErrorParameter.getSetupErrors()));
			getDialog().showDialog(this);
		}
	}

	public void dialogClosed(IDialog pDialog) {
		hideDialog();
	}

}
