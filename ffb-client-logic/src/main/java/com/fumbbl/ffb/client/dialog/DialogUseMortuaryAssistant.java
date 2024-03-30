package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogUseMortuaryAssistantParameter;

public class DialogUseMortuaryAssistant extends DialogThreeWayChoice {

	private final DialogUseMortuaryAssistantParameter fDialogParameter;

	public DialogUseMortuaryAssistant(FantasyFootballClient pClient, DialogUseMortuaryAssistantParameter pDialogParameter) {
		super(pClient, "Use Mortuary Assistant", createMessages(pClient, pDialogParameter), IIconProperty.RESOURCE_IGOR);
		fDialogParameter = pDialogParameter;
	}

	private static String[] createMessages(FantasyFootballClient pClient, DialogUseMortuaryAssistantParameter pDialogParameter) {
		String playerName = pClient.getGame().getPlayerById(pDialogParameter.getPlayerId()).getName();
		String[] messages = new String[2];
		messages[0] = "Do you want to use your Mortuary Assistant (or Plague Doctor) for " + playerName + "?";
		messages[1] = "Using the Mortuary Assistant (or Plague Doctor) will re-roll the failed Regeneration.";
		return messages;
	}

	public DialogId getId() {
		return DialogId.USE_MORTUARY_ASSISTANT;
	}

	public String getPlayerId() {
		return fDialogParameter.getPlayerId();
	}

	public DialogUseMortuaryAssistantParameter getDialogParameter() {
		return fDialogParameter;
	}
}
