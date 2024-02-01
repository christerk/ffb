package com.fumbbl.ffb.client.dialog.inducements;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.StatusType;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.dialog.DialogHandler;
import com.fumbbl.ffb.client.dialog.IDialog;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogUseInducementParameter;
import com.fumbbl.ffb.inducement.InducementType;
import com.fumbbl.ffb.model.Game;

/**
 * 
 * @author Kalimar
 */
public class DialogUseInducementHandler extends DialogHandler {

	public DialogUseInducementHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {

		Game game = getClient().getGame();
		DialogUseInducementParameter dialogParameter = (DialogUseInducementParameter) game.getDialogParameter();

		if (dialogParameter != null) {

			if ((ClientMode.PLAYER == getClient().getMode())
					&& game.getTeamHome().getId().equals(dialogParameter.getTeamId())) {
				setDialog(new DialogUseInducement(getClient(), dialogParameter));
				getDialog().showDialog(this);

			} else {
				showStatus("Use Inducement", "Waiting for coach to select an inducement.", StatusType.WAITING);
			}

		}

	}

	@Override
	public boolean isEndTurnAllowedWhileDialogVisible() {
		return false;
	}

	public void dialogClosed(IDialog pDialog) {
		hideDialog();
		if ((pDialog != null) && (pDialog.getId() == DialogId.USE_INDUCEMENT)) {
			DialogUseInducement useInducementDialog = (DialogUseInducement) pDialog;
			if (useInducementDialog.getInducement() != null) {
				getClient().getCommunication().sendUseInducement(useInducementDialog.getInducement());
			} else if (useInducementDialog.getCard() != null) {
				getClient().getCommunication().sendUseInducement(useInducementDialog.getCard());
			} else {
				getClient().getCommunication().sendUseInducement((InducementType) null);
			}
		}
	}

}
