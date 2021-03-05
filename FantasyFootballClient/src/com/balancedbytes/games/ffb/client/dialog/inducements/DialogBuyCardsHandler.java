package com.balancedbytes.games.ffb.client.dialog.inducements;

import java.util.HashSet;
import java.util.Set;

import com.balancedbytes.games.ffb.inducement.Card;
import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.StatusType;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.dialog.DialogHandler;
import com.balancedbytes.games.ffb.client.dialog.IDialog;
import com.balancedbytes.games.ffb.dialog.DialogBuyCardsParameter;
import com.balancedbytes.games.ffb.dialog.DialogId;
import com.balancedbytes.games.ffb.model.Game;

public class DialogBuyCardsHandler extends DialogHandler {

	private Set<Card> fCardsDrawn;

	public DialogBuyCardsHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	@Override
	public void showDialog() {
		fCardsDrawn = new HashSet<>();
		Game game = getClient().getGame();
		DialogBuyCardsParameter dialogParameter = (DialogBuyCardsParameter) game.getDialogParameter();
		if (dialogParameter != null) {
			if ((ClientMode.PLAYER == getClient().getMode())
					&& (game.getTeamHome().getId().equals(dialogParameter.getTeamId()))) {
				setDialog(new DialogBuyCards(getClient(), dialogParameter));
				getDialog().showDialog(this);
			} else {
				showStatus("Buy Cards", "Waiting for coach to buy Cards.", StatusType.WAITING);
			}
		}
	}

	@Override
	public void updateDialog() {
		Game game = getClient().getGame();
		DialogBuyCards buyCardsDialog = (DialogBuyCards) getDialog();
		if (buyCardsDialog != null) {
			for (Card card : game.getTurnDataHome().getInducementSet().getAvailableCards()) {
				if (!fCardsDrawn.contains(card)) {
					fCardsDrawn.add(card);
					buyCardsDialog.addCard(card);
					break;
				}
			}
		}
	}

	public void dialogClosed(IDialog pDialog) {
		hideDialog();
		if (testDialogHasId(pDialog, DialogId.BUY_CARDS)) {
			getClient().getCommunication().sendBuyCard(null); // end card buying
		}
	}

}
