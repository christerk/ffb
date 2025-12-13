package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.StatusType;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogSwarmingPlayersParameter;
import com.fumbbl.ffb.model.Game;

import java.util.ArrayList;
import java.util.List;

/**
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
			List<String> messages = new ArrayList<>();
			if (((DialogSwarmingPlayersParameter) game.getDialogParameter()).isRestrictPlacement()) {
				messages.add("You may place up to " + amount + " players with the Swarming skill in your half.");
				messages.add("They cannot be placed at the Line of Scrimmage or in the wide zones.");
			} else {
				messages.add("You may place up to " + amount + " Lineman players in your half.");
			}

			setDialog(new DialogInformation(getClient(), "Place swarming players",
					messages.toArray(new String[0]),
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
