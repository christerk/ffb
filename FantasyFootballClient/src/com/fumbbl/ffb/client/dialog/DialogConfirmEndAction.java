package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;

import java.util.ArrayList;
import java.util.List;

public class DialogConfirmEndAction extends DialogYesOrNoQuestion {

	public DialogConfirmEndAction(FantasyFootballClient pClient, PlayerAction playerAction) {
		super(pClient, "End " + title(playerAction) + "?", createMessages(playerAction), null);
	}

	private static String[] createMessages(PlayerAction playerAction) {
		List<String> messages = new ArrayList<>();

		messages.add("Do you want to end the player action?");
		if (playerAction.isBlitzing()) {
			messages.add("Your blitz action will be lost for this turn.");
		}
		return messages.toArray(new String[0]);
	}

	private static String title(PlayerAction playerAction) {
		if (playerAction.isBlitzing()) {
			return "Blitz";
		} else if (playerAction.isGaze()) {
			return "Gaze";
		} else {
			return "";
		}
	}

	public DialogId getId() {
		return DialogId.CONFIRM_END_ACTION;
	}
}
