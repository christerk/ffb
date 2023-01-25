package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.mechanics.AgilityMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.Game;

/**
 * @author Kalimar
 */
public class DialogInterception extends DialogThreeWayChoice {

	public DialogInterception(FantasyFootballClient pClient, String skillText, int skillMnemonic) {
		super(pClient, title(pClient.getGame()), new String[]{description(pClient.getGame())},
			IIconProperty.GAME_REF, skillText, skillMnemonic);
	}

	public DialogId getId() {
		return DialogId.INTERCEPTION;
	}

	private static String title(Game game) {
		return ((AgilityMechanic) game.getFactory(FactoryType.Factory.MECHANIC)
			.forName(Mechanic.Type.AGILITY.name())).interceptionWording(false).getNoun();
	}

	private static String description(Game game) {
		return "Do you want to try to " + ((AgilityMechanic) game.getFactory(FactoryType.Factory.MECHANIC)
			.forName(Mechanic.Type.AGILITY.name())).interceptionWording(false).getVerb() + " the pass?";
	}

}
