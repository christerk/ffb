package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.StatusType;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogSwarmingPlayersParameter;
import com.fumbbl.ffb.model.Game;

/**
 * 
 * @author Kalimar
 */
public class DialogSwarmingPlayersHandler extends DialogHandler {

	public DialogSwarmingPlayersHandler(FantasyFootballClient pClient) {
		super(pClient);
	}

	public void showDialog() {

		Game game = getClient().getGame();

		int amount = ((DialogSwarmingPlayersParameter) game.getDialogParameter()).getAmount();

		if ((ClientMode.PLAYER == getClient().getMode()) && game.isHomePlaying()) {
			setDialog(new DialogInformation(getClient(), "Place swarming players",
					new String[] { "You may place up to " + amount + " players with the Swarming skill in your half.",
							"They cannot be placed at the Line of Scrimmage or in the wide zones." },
					DialogInformation.OK_DIALOG, IIconProperty.GAME_REF));
			getDialog().showDialog(this);

		} else {
			showStatus("Skill Use", "Waiting for coach to place swarming players.", StatusType.WAITING);
		}

	}

	public void dialogClosed(IDialog pDialog) {
		hideDialog();
	}

}
