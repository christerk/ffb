package com.balancedbytes.games.ffb.client.dialog;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.IIconProperty;
import com.balancedbytes.games.ffb.dialog.DialogId;
import com.balancedbytes.games.ffb.mechanics.AgilityMechanic;
import com.balancedbytes.games.ffb.mechanics.Mechanic;
import com.balancedbytes.games.ffb.model.Game;

/**
 * 
 * @author Kalimar
 */
@SuppressWarnings("serial")
public class DialogInterception extends DialogYesOrNoQuestion {

	public DialogInterception(FantasyFootballClient pClient) {
		super(pClient, title(pClient.getGame()), new String[] { description(pClient.getGame()) },
				IIconProperty.GAME_REF);
	}

	public DialogId getId() {
		return DialogId.INTERCEPTION;
	}

	private static String title(Game game) {
		return ((AgilityMechanic) game.getFactory(FactoryType.Factory.MECHANIC)
			.forName(Mechanic.Type.AGILITY.name())).interceptionWording().getNoun();
	}

	private static String description(Game game) {
		return "Do you want to try to " + ((AgilityMechanic) game.getFactory(FactoryType.Factory.MECHANIC)
			.forName(Mechanic.Type.AGILITY.name())).interceptionWording().getVerb() + " the pass?";
	}

}
