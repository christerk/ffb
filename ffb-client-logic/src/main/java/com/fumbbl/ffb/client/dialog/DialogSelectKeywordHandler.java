package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.StatusType;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogSelectKeywordParameter;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Keyword;
import com.fumbbl.ffb.model.Player;

import java.util.Arrays;
import java.util.List;

public class DialogSelectKeywordHandler extends DialogHandler {

	private DialogSelectKeywordParameter dialogParameter;

	public DialogSelectKeywordHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {

		Game game = getClient().getGame();
		dialogParameter = (DialogSelectKeywordParameter) game.getDialogParameter();

		if (dialogParameter != null) {

			Player<?> player = game.getPlayerById(dialogParameter.getPlayerId());
			if ((ClientMode.PLAYER == getClient().getMode())
				&& game.getTeamHome().hasPlayer(player)) {
				String dialogHeader = dialogParameter.getKeywordChoiceMode().getDialogHeader(player.getName());
				List<Keyword> keywords = dialogParameter.getKeywords();

				setDialog(new DialogSelectKeyword(getClient(), dialogHeader, keywords,
					1, 1, false));
				getDialog().showDialog(this);

			} else {
				showStatus(dialogParameter.getKeywordChoiceMode().getStatusTitle(),
					dialogParameter.getKeywordChoiceMode().getStatusMessage(), StatusType.WAITING);
			}

		}

	}

	public void dialogClosed(IDialog pDialog) {
		hideDialog();
		if (testDialogHasId(pDialog, DialogId.SELECT_KEYWORD)) {
			DialogSelectKeyword keywordChoiceDialog = (DialogSelectKeyword) pDialog;
			getClient().getCommunication()
				.sendKeywordSelection(dialogParameter.getPlayerId(), Arrays.asList(keywordChoiceDialog.getSelectedKeywords()));
		}
	}

}
