package com.fumbbl.ffb.client.dialog.inducements;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.StatusType;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.dialog.DialogHandler;
import com.fumbbl.ffb.client.dialog.IDialog;
import com.fumbbl.ffb.dialog.DialogBuyCardsAndInducementsParameter;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.inducement.Card;
import com.fumbbl.ffb.model.Game;

import java.util.HashSet;
import java.util.Set;

public class DialogBuyCardsAndInducementsHandler extends DialogHandler {

	private final Set<Card> fCardsDrawn = new HashSet<>();

	public DialogBuyCardsAndInducementsHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {

		Game game = getClient().getGame();
		DialogBuyCardsAndInducementsParameter dialogParameter = (DialogBuyCardsAndInducementsParameter) game.getDialogParameter();

		if (dialogParameter != null) {
			if ((ClientMode.PLAYER == getClient().getMode())
					&& (game.getTeamHome().getId().equals(dialogParameter.getTeamId()))) {
				setDialog(
						new DialogBuyCardsAndInducements(getClient(), dialogParameter));
				getDialog().showDialog(this);
			} else {
				showStatus("Buy Inducements", "Waiting for coach to buy Inducements.", StatusType.WAITING);
			}
		}

	}

	public void dialogClosed(IDialog pDialog) {
		hideDialog();
		if (testDialogHasId(pDialog, DialogId.BUY_CARDS_AND_INDUCEMENTS)) {
			DialogBuyCardsAndInducements buyInducementsDialog = (DialogBuyCardsAndInducements) pDialog;
			getClient().getCommunication().sendBuyInducements(buyInducementsDialog.getTeamId(),
				buyInducementsDialog.getAvailableGold(), buyInducementsDialog.getSelectedInducements(),
				buyInducementsDialog.getSelectedStarPlayerIds(), buyInducementsDialog.getSelectedMercenaryIds(),
				buyInducementsDialog.getSelectedMercenarySkills(), buyInducementsDialog.getSelectedStaffIds());
		}
	}

	@Override
	public void updateDialog() {
		Game game = getClient().getGame();
		DialogBuyCardsAndInducements buyCardsDialog = (DialogBuyCardsAndInducements) getDialog();
		if (buyCardsDialog != null) {
			if (game.getDialogParameter() instanceof DialogBuyCardsAndInducementsParameter) {
				DialogBuyCardsAndInducementsParameter parameter = (DialogBuyCardsAndInducementsParameter) game.getDialogParameter();
				if (parameter.getCardChoices() != buyCardsDialog.getCardChoices()) {
					buyCardsDialog.setCardChoices(parameter.getCardChoices());
				}
			}
			for (Card card : game.getTurnDataHome().getInducementSet().getAvailableCards()) {
				if (!fCardsDrawn.contains(card)) {
					fCardsDrawn.add(card);
					buyCardsDialog.addCard(card);
					break;
				}
			}
		}
	}
}