package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.StatusType;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogUseMortuaryAssistantParameter;
import com.fumbbl.ffb.factory.InducementTypeFactory;
import com.fumbbl.ffb.inducement.Usage;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

/**
 * @author Kalimar
 */
public class DialogUseMortuaryAssistantHandler extends DialogHandler {

	public DialogUseMortuaryAssistantHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {

		Game game = getClient().getGame();
		DialogUseMortuaryAssistantParameter dialogParameter = (DialogUseMortuaryAssistantParameter) game.getDialogParameter();

		if (dialogParameter != null) {

			Player<?> player = game.getPlayerById(dialogParameter.getPlayerId());

			if ((ClientMode.PLAYER == getClient().getMode()) && getClient().getGame().getTeamHome().hasPlayer(player)) {
				setDialog(new DialogUseMortuaryAssistant(getClient(), dialogParameter));
				getDialog().showDialog(this);

			} else {
				showStatus("Mortuary Assistant", "Waiting for coach to use a Mortuary Assistant.", StatusType.WAITING);
			}

		}

	}

	public void dialogClosed(IDialog pDialog) {
		hideDialog();
		if (testDialogHasId(pDialog, DialogId.USE_MORTUARY_ASSISTANT)) {
			DialogUseMortuaryAssistant dialog = (DialogUseMortuaryAssistant) pDialog;
			((InducementTypeFactory) getClient().getGame().getFactory(FactoryType.Factory.INDUCEMENT_TYPE)).allTypes().stream()
				.filter(type -> type.hasUsage(Usage.REGENERATION)).findFirst().ifPresent(type -> {
					if (dialog.isChoiceYes()) {
						getClient().getCommunication().sendUseInducement(type, dialog.getPlayerId());
					} else {
						getClient().getCommunication().sendUseInducement(type, (String) null);
					}
				});
		}
	}

}
