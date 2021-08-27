package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.StatusType;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogUseIgorParameter;
import com.fumbbl.ffb.factory.InducementTypeFactory;
import com.fumbbl.ffb.inducement.Usage;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

/**
 * @author Kalimar
 */
public class DialogUseIgorHandler extends DialogHandler {

	public DialogUseIgorHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {

		Game game = getClient().getGame();
		DialogUseIgorParameter dialogUseIgorParameter = (DialogUseIgorParameter) game.getDialogParameter();

		if (dialogUseIgorParameter != null) {

			Player<?> player = game.getPlayerById(dialogUseIgorParameter.getPlayerId());

			if ((ClientMode.PLAYER == getClient().getMode()) && getClient().getGame().getTeamHome().hasPlayer(player)) {
				setDialog(new DialogUseIgor(getClient(), dialogUseIgorParameter));
				getDialog().showDialog(this);

			} else {
				showStatus("Igor", "Waiting for coach to use Igor.", StatusType.WAITING);
			}

		}

	}

	public void dialogClosed(IDialog pDialog) {
		hideDialog();
		if (testDialogHasId(pDialog, DialogId.USE_IGOR)) {
			DialogUseIgor igorDialog = (DialogUseIgor) pDialog;
			((InducementTypeFactory) getClient().getGame().getFactory(FactoryType.Factory.INDUCEMENT_TYPE)).allTypes().stream()
				.filter(type -> type.getUsage() == Usage.REGENERATION).findFirst().ifPresent(type -> {
				if (igorDialog.isChoiceYes()) {
					getClient().getCommunication().sendUseInducement(type, igorDialog.getPlayerId());
				} else {
					getClient().getCommunication().sendUseInducement(type, (String) null);
				}
			});
		}
	}

}
