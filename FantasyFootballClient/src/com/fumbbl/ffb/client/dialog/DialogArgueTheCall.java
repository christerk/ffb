package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.model.Player;

/**
 * @author Kalimar
 */
public class DialogArgueTheCall extends DialogYesOrNoQuestion {

	public DialogArgueTheCall(FantasyFootballClient pClient, Player<?> pPlayer, boolean stayOnPitch, boolean friendsWithTheRef) {
		super(pClient, "Argue the call", createMessages(pPlayer, stayOnPitch, friendsWithTheRef), null);
	}

	public DialogId getId() {
		return DialogId.ARGUE_THE_CALL;
	}

	private static String[] createMessages(Player<?> pPlayer, boolean stayOnPitch, boolean friendsWithTheRef) {
		String[] messages;
		if (pPlayer != null) {
			messages = new String[3];
			StringBuilder message = new StringBuilder();
			message.append("On a roll of ").append(successResult(friendsWithTheRef)).append(" the ref ");
			if (stayOnPitch) {
				message.append("refrains from banning ").append(pPlayer.getName()).append(".");
			} else {
				message.append("sends ").append(pPlayer.getName()).append(" to the reserves instead.");
			}
			messages[1] = message.toString();
			messages[2] = "On a roll of 1 the ref will ban the coach for the rest of the game.";
		} else {
			messages = new String[1];
		}
		messages[0] = "Do you want to argue the call?";
		return messages;
	}

	private static String successResult(boolean friendsWithTheRef) {
		if (friendsWithTheRef) {
			return "5+ (for being friends)";
		}
		return "6";
	}

}
