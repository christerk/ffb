package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.StatusType;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogPilingOnParameter;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.util.UtilCards;

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
