package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogPilingOnParameter;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

/**
 * @author Kalimar
 */
public class DialogPilingOn extends DialogThreeWayChoice {

	public DialogPilingOn(FantasyFootballClient pClient, DialogPilingOnParameter pDialogParameter) {
		super(pClient, "Use Piling On", createMessages(pClient, pDialogParameter), null);
	}

	public DialogId getId() {
		return DialogId.PILING_ON;
	}

	private static String[] createMessages(FantasyFootballClient pClient, DialogPilingOnParameter pDialogParameter) {
		String[] messages = new String[0];
		if ((pClient != null) && (pDialogParameter != null)) {
			Game game = pClient.getGame();
			Player<?> player = game.getPlayerById(pDialogParameter.getPlayerId());
			if (player != null) {
				messages = new String[3];
				StringBuilder line = new StringBuilder();
				line.append("Do you want ").append(player.getName()).append(" to use Piling On?");
				messages[0] = line.toString();
				line = new StringBuilder();
				line.append("Using Piling On will re-roll ").append(pDialogParameter.isReRollInjury() ? "Injury" : "Armor");
				line.append(" for ").append(player.getPlayerGender().getGenitive()).append(" opponent.");
				messages[1] = line.toString();
				line = new StringBuilder();
				line.append(player.getName()).append(" will be prone after using this skill.");
				messages[2] = line.toString();
			}
		}
		return messages;
	}

}
