package com.balancedbytes.games.ffb.client.dialog;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.StatusType;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.dialog.DialogId;
import com.balancedbytes.games.ffb.dialog.DialogPilingOnParameter;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.property.NamedProperties;
import com.balancedbytes.games.ffb.util.UtilCards;

/**
 *
 * @author Kalimar
 */
public class DialogPilingOnHandler extends DialogHandler {

	public DialogPilingOnHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {

		Game game = getClient().getGame();
		DialogPilingOnParameter dialogParameter = (DialogPilingOnParameter) game.getDialogParameter();

		if (dialogParameter != null) {

			Player<?> player = game.getPlayerById(dialogParameter.getPlayerId());

			if ((ClientMode.PLAYER == getClient().getMode()) && (game.getTeamHome().hasPlayer(player))) {
				setDialog(new DialogPilingOn(getClient(), dialogParameter));
				getDialog().showDialog(this);

			} else {
				showStatus("Piling On", "Waiting for coach to use Piling On.", StatusType.WAITING);
			}

		}

	}

	public void dialogClosed(IDialog pDialog) {
		hideDialog();
		if (testDialogHasId(pDialog, DialogId.PILING_ON)) {
			DialogPilingOn pilingOnDialog = (DialogPilingOn) pDialog;
			String playerId = ((DialogPilingOnParameter) getClient().getGame().getDialogParameter()).getPlayerId();
			Player<?> player = getClient().getGame().getPlayerById(playerId);
			UtilCards.getSkillWithProperty(player, NamedProperties.canPileOnOpponent).ifPresent(
				skill -> getClient().getCommunication().sendUseSkill(skill, pilingOnDialog.isChoiceYes(), playerId)
			);
		}
	}

}
