package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogBloodlustActionParameter;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

/**
 * @author Kalimar
 */
public class DialogBloodlustActionHandler extends DialogHandler {

	public DialogBloodlustActionHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {

		Game game = getClient().getGame();
		Player<?> player = game.getActingPlayer().getPlayer();
		PlayerAction playerAction = game.getActingPlayer().getPlayerAction();

		if ((getClient().getMode() == ClientMode.PLAYER) && game.getTeamHome().hasPlayer(player)) {
			boolean changeToMove = ((DialogBloodlustActionParameter) game.getDialogParameter()).isChangeToMove();
			boolean hasReceiver = playerAction == PlayerAction.PASS || playerAction == PlayerAction.HAND_OVER;
			setDialog(new DialogBloodlustAction(getClient(), changeToMove, hasReceiver));
			getDialog().showDialog(this);
		}

	}

	public void dialogClosed(IDialog pDialog) {
		hideDialog();
		if (testDialogHasId(pDialog, DialogId.BLOODLUST_ACTION)) {
			DialogBloodlustAction dialog = (DialogBloodlustAction) pDialog;
			getClient().getCommunication().sendChangeBloodlustAction(dialog.isChoiceYes());
		}
	}

}
