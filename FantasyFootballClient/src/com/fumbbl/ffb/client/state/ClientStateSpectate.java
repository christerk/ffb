package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.GameTitle;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.model.Game;

/**
 * 
 * @author Kalimar
 */
public class ClientStateSpectate extends ClientState {

	protected ClientStateSpectate(FantasyFootballClient pClient) {
		super(pClient);
	}

	public ClientStateId getId() {
		return ClientStateId.SPECTATE;
	}

	public void enterState() {
		super.enterState();
		setSelectable(true);
		setClickable(false);
		Game game = getClient().getGame();
		if ((game.getFinished() != null) && (ClientMode.PLAYER == getClient().getMode())) {
			getClient().setMode(ClientMode.SPECTATOR);
			UserInterface userInterface = getClient().getUserInterface();
			GameTitle gameTitle = userInterface.getGameTitle();
			gameTitle.setClientMode(ClientMode.SPECTATOR);
			userInterface.setGameTitle(gameTitle);
			userInterface.getGameMenuBar().refresh();
		}
	}

	public boolean actionKeyPressed(ActionKey pActionKey) {
		boolean actionHandled = false;
		if (pActionKey == ActionKey.MENU_REPLAY) {
			actionHandled = true;
			getClient().getReplayer().start();
			getClient().getUserInterface().getGameMenuBar().refresh();
			getClient().updateClientState();
		}
		return actionHandled;
	}

}
