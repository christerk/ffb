package com.balancedbytes.games.ffb.client.dialog;

import java.awt.event.ActionListener;

import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.IIconProperty;
import com.balancedbytes.games.ffb.dialog.DialogId;

/**
 * 
 * @author Kalimar
 */
@SuppressWarnings("serial")
public class DialogConcedeGame extends DialogYesOrNoQuestion implements ActionListener {

	public DialogConcedeGame(FantasyFootballClient pClient, boolean pLegalConcession) {
		super(pClient, "Concede Game", createMessages(pLegalConcession), IIconProperty.GAME_REF);
	}

	public DialogId getId() {
		return DialogId.CONCEDE_GAME;
	}

	private static String[] createMessages(boolean pLegalConcession) {
		String[] messages = null;
		if (pLegalConcession) {
			messages = new String[2];
			messages[0] = "Do you want to concede this game?";
			messages[1] = "The concession will have no negative consequences at this point.";
		} else {
			messages = new String[4];
			messages[0] = "Do you want to concede this game?";
			messages[1] = "Your fan factor will decrease by 1.";
			messages[2] = "You will lose your player award and all your winnings.";
			messages[3] = "Some valuable players (SPP 51+) may decide to leave your team.";
		}
		return messages;
	}

}
