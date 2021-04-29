package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogUseSafePairOfHandsParameter;
import com.fumbbl.ffb.model.Player;

public class DialogUseSafePairOfHands extends DialogYesOrNoQuestion {

	private final DialogUseSafePairOfHandsParameter fDialogParameter;

	public DialogUseSafePairOfHands(FantasyFootballClient pClient, DialogUseSafePairOfHandsParameter pDialogParameter) {
		super(pClient, "Safe Pair of Hands", createMessages(pClient, pDialogParameter), IIconProperty.GAME_BALL);
		fDialogParameter = pDialogParameter;
	}

	public DialogId getId() {
		return DialogId.USE_SAFE_PAIR_OF_HANDS;
	}

	public String getPlayerId() {
		return fDialogParameter.getPlayerId();
	}

	public DialogUseSafePairOfHandsParameter getDialogParameter() {
		return fDialogParameter;
	}

	private static String[] createMessages(FantasyFootballClient pClient, DialogUseSafePairOfHandsParameter pDialogParameter) {
		Player<?> player = pClient.getGame().getPlayerById(pDialogParameter.getPlayerId());
		String playerName = player.getName();

		String[] messages = new String[2];
		messages[0] = playerName +" has a Safe Pair of Hands.";
		messages[1] = "Do you want " + player.getPlayerGender().getDative() + " to use them?";
		return messages;
	}
}
