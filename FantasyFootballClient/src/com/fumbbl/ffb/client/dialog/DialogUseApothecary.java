package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.ApothecaryType;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogUseApothecaryParameter;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

/**
 * @author Kalimar
 */
public class DialogUseApothecary extends DialogThreeWayChoice {

	private final DialogUseApothecaryParameter fDialogParameter;

	private DialogUseApothecary(FantasyFootballClient pClient, DialogUseApothecaryParameter pDialogParameter) {
		super(pClient, "Use Apothecary", createMessages(pClient, pDialogParameter), IIconProperty.RESOURCE_APOTHECARY);
		fDialogParameter = pDialogParameter;
	}

	private DialogUseApothecary(FantasyFootballClient pClient, DialogUseApothecaryParameter pDialogParameter, ApothecaryType type1, ApothecaryType type2) {
		super(pClient, "Use Apothecary", createMessages(pClient, pDialogParameter), IIconProperty.RESOURCE_APOTHECARY,
			type1.getName(), type1.getName().charAt(0), type2.getName(), type2.getName().charAt(0), "None", 'N',
			null, null);
		fDialogParameter = pDialogParameter;
	}

	public static DialogUseApothecary create(FantasyFootballClient pClient, DialogUseApothecaryParameter pDialogParameter) {
		if (pDialogParameter.getApothecaryTypes().size() < 2) {
			return new DialogUseApothecary(pClient, pDialogParameter);
		}
		return new DialogUseApothecary(pClient, pDialogParameter, pDialogParameter.getApothecaryTypes().get(0), pDialogParameter.getApothecaryTypes().get(1));
	}

	public DialogId getId() {
		return DialogId.USE_APOTHECARY;
	}

	public String getPlayerId() {
		return fDialogParameter.getPlayerId();
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
			String description = pDialogParameter.getApothecaryTypes().size() == 1 ? " (" + pDialogParameter.getApothecaryTypes().get(0).getName() + ")" : "";
			messages[1] = "Do you want to use your Apothecary" + description + "?";
		}
		return messages;
	}

	public DialogUseApothecaryParameter getDialogParameter() {
		return fDialogParameter;
	}

}
