package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.StatusType;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.model.Game;

/**
 * 
 * @author Kalimar
 */
public class DialogKickoffReturnHandler extends DialogHandler {

	public DialogKickoffReturnHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {

		Game game = getClient().getGame();

		if ((ClientMode.PLAYER == getClient().getMode()) && game.isHomePlaying()) {
			setDialog(new DialogInformation(getClient(), "Use Kick-Off Return Skill",
					new String[] { "You may move a single player with this skill up to 3 squares within your own half." },
					DialogInformation.OK_DIALOG, IIconProperty.GAME_REF));
			getDialog().showDialog(this);

		} else {
			showStatus("Skill Use", "Waiting for coach to use Kick-Off Return.", StatusType.WAITING);
		}

	}

	public void dialogClosed(IDialog pDialog) {
		hideDialog();
	}

}
