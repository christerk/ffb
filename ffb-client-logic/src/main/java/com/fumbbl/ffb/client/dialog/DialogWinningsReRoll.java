package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;

import java.awt.event.ActionListener;

/**
 * @author Kalimar
 */
public class DialogWinningsReRoll extends DialogThreeWayChoice implements ActionListener {

	public DialogWinningsReRoll(FantasyFootballClient pClient, int pOldRoll) {
		super(pClient, "Re-roll Winnings", createMessages(pOldRoll), IIconProperty.GAME_DICE_SMALL, "Keep", 'K',
			"Re-Roll", 'R');
	}

	public DialogId getId() {
		return DialogId.WINNINGS_RE_ROLL;
	}

	private static String[] createMessages(int pOldRoll) {
		String[] messages = new String[3];
		messages[0] = "Do you want to keep your winnings?";
		messages[1] = "The current roll is " + pOldRoll + ".";
		if (pOldRoll < 6) {
			messages[2] = "If you re-roll you must keep the new result.";
		} else {
			messages[2] = "Rolled maximum. If you re-roll this it can only get worse.";
		}
		return messages;
	}

}
