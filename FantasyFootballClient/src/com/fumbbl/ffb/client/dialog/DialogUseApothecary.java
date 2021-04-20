package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogUseApothecaryParameter;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

/**
 *
 * @author Kalimar
 */
public class DialogUseApothecary extends DialogYesOrNoQuestion {

	private DialogUseApothecaryParameter fDialogParameter;

	public DialogUseApothecary(FantasyFootballClient pClient, DialogUseApothecaryParameter pDialogParameter) {
		super(pClient, "Use Apothecary", createMessages(pClient, pDialogParameter), IIconProperty.RESOURCE_APOTHECARY);
		fDialogParameter = pDialogParameter;
	}

	public DialogId getId() {
		return DialogId.USE_APOTHECARY;
	}

	public String getPlayerId() {
		return fDialogParameter.getPlayerId();
	}

	public DialogUseApothecaryParameter getDialogParameter() {
		return fDialogParameter;
	}

	private static String[] createMessages(FantasyFootballClient pClient, DialogUseApothecaryParameter pDialogParameter) {
		String[] messages = new String[0];
		if ((pClient != null) && (pDialogParameter != null)) {
			Game game = pClient.getGame();
			Player<?> player = game.getPlayerById(pDialogParameter.getPlayerId());
			messages = new String[2];
			StringBuilder injuryMessage = new StringBuilder();
			injuryMessage.append(player.getName()).append(" ");
			if (pDialogParameter.getSeriousInjury() != null) {
				injuryMessage.append(pDialogParameter.getSeriousInjury().getDescription());
			} else {
				injuryMessage.append(pDialogParameter.getPlayerState().getDescription());
			}
			messages[0] = injuryMessage.toString();
			messages[1] = "Do you want to use your Apothecary?";
		}
		return messages;
	}

}
