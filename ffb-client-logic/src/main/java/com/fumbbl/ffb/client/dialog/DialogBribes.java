package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.model.Player;

/**
 * @author Kalimar
 */
public class DialogBribes extends DialogThreeWayChoice {

	public DialogBribes(FantasyFootballClient pClient, Player<?> pPlayer) {
		super(pClient, "Use a bribe", createMessages(pPlayer), null);
	}

	public DialogId getId() {
		return DialogId.BRIBES;
	}

	private static String[] createMessages(Player<?> pPlayer) {
		String[] messages;
		if (pPlayer != null) {
			messages = new String[2];
			StringBuilder message = new StringBuilder();
			message.append("On a roll of 2+ he will refrain from ejecting ").append(pPlayer.getName()).append(".");
			messages[1] = message.toString();
		} else {
			messages = new String[1];
		}
		messages[0] = "Do you want to bribe the ref?";
		return messages;
	}

}
