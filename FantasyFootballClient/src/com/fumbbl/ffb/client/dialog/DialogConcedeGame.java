package com.fumbbl.ffb.client.dialog;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.mechanics.GameMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.Game;

import java.awt.event.ActionListener;

/**
 * 
 * @author Kalimar
 */
public class DialogConcedeGame extends DialogYesOrNoQuestion implements ActionListener {

	public DialogConcedeGame(FantasyFootballClient pClient, boolean pLegalConcession) {
		super(pClient, "Concede Game", createMessages(pClient.getGame(), pLegalConcession), IIconProperty.GAME_REF);
	}

	public DialogId getId() {
		return DialogId.CONCEDE_GAME;
	}

	private static String[] createMessages(Game game, boolean pLegalConcession) {

		GameMechanic mechanic = (GameMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.GAME.name());

		return mechanic.concessionDialogMessages(pLegalConcession);
	}

}
