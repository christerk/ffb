package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.BoxType;
import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.StatusType;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.ui.SideBarComponent;
import com.fumbbl.ffb.dialog.DialogSwarmingErrorParameter;
import com.fumbbl.ffb.model.Game;

public class DialogSwarmingErrorParameterHandler extends DialogHandler {

	public DialogSwarmingErrorParameterHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	@Override
	public void showDialog() {
		Game game = getClient().getGame();

		int allowed = ((DialogSwarmingErrorParameter) game.getDialogParameter()).getAllowed();
		int actual = ((DialogSwarmingErrorParameter) game.getDialogParameter()).getActual();

		if ((ClientMode.PLAYER == getClient().getMode()) && game.isHomePlaying()) {
			SideBarComponent sideBarHome = getClient().getUserInterface().getSideBarHome();
			if (!sideBarHome.isBoxOpen()) {
				sideBarHome.openBox(BoxType.RESERVES);
			}

			setDialog(new DialogInformation(getClient(), "Too many swarming players",
					new String[] { "You placed " + actual + " players rather than the allowed " + allowed + "." },
					DialogInformation.OK_DIALOG, IIconProperty.GAME_REF));
			getDialog().showDialog(this);

		} else {
			showStatus("Skill Use", "Waiting for coach to place swarming players.", StatusType.WAITING);
		}
	}

	@Override
	public void dialogClosed(IDialog pDialog) {
		hideDialog();
	}
}
