package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.client.FantasyFootballClient;

public class DialogBriberyAndCorruption extends DialogYesOrNoQuestion {
	public DialogBriberyAndCorruption(FantasyFootballClient pClient) {
		super(pClient, "Use Bribery and Corruption Re-Roll", createMessages(), IIconProperty.GAME_REF);
	}

	private static String[] createMessages() {
		String[] messages = new String[3];
		messages[0] = "Do you want to use Bribery and Corruption?";
		messages[1] = "You can re-roll a natural 1 on Argue the Call";
		messages[2] = "This can only be done once per game.";
		return messages;
	}
}
