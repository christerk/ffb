package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.BoxType;
import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.ui.SideBarComponent;
import com.fumbbl.ffb.dialog.DialogSetupErrorParameter;
import com.fumbbl.ffb.model.Game;

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
			System.out.println("Setup error dialog shown");
		}
	}

	public void dialogClosed(IDialog pDialog) {
		hideDialog();
		SideBarComponent sideBarHome = getClient().getUserInterface().getSideBarHome();
		sideBarHome.getTurnDiceStatusComponent().enableButton();
	}

}
