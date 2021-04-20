package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.model.Player;

/**
 *
 * @author Kalimar
 */
public class DialogArgueTheCall extends DialogYesOrNoQuestion {

	public DialogArgueTheCall(FantasyFootballClient pClient, Player<?> pPlayer) {
		super(pClient, "Argue the call", createMessages(pPlayer), null);
	}

	public DialogId getId() {
		return DialogId.ARGUE_THE_CALL;
	}

	private static String[] createMessages(Player<?> pPlayer) {
		String[] messages;
		if (pPlayer != null) {
			messages = new String[3];
			StringBuilder message = new StringBuilder();
			message.append("On a roll of 6 the ref sends ").append(pPlayer.getName()).append(" to the reserves instead.");
			messages[1] = message.toString();
			messages[2] = "On a roll of 1 the ref will ban the coach for the rest of the game.";
		} else {
			messages = new String[1];
		}
		messages[0] = "Do you want to argue the call?";
		return messages;
	}

}
