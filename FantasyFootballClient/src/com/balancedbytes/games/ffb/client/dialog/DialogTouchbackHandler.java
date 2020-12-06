package com.balancedbytes.games.ffb.client.dialog;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.StatusType;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.dialog.DialogId;
import com.balancedbytes.games.ffb.model.Game;

/**
 * 
 * @author Kalimar
 */
public class DialogTouchbackHandler extends DialogHandler {

	private static final String _DIALOG_TITLE = "Touchback";

	public DialogTouchbackHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {

		Game game = getClient().getGame();

		if ((ClientMode.PLAYER == getClient().getMode()) && !game.isHomePlaying()) {
			setDialog(new DialogInformation(getClient(), _DIALOG_TITLE,
					new String[] { "You may give the ball to any member of your team." }, DialogInformation.OK_DIALOG, null));
			getDialog().showDialog(this);

		} else {
			showStatus(_DIALOG_TITLE, "Waiting for coach to give the ball to any member of the team.", StatusType.WAITING);
		}

	}

	public void dialogClosed(IDialog pDialog) {
		hideDialog();
		if (testDialogHasId(pDialog, DialogId.INFORMATION)) {
			getClient().getClientState().setClickable(true);
		}
	}

}
