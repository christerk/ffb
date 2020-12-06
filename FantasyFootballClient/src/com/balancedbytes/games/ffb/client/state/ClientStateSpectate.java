package com.balancedbytes.games.ffb.client.state;

import com.balancedbytes.games.ffb.ClientMode;
import com.balancedbytes.games.ffb.ClientStateId;
import com.balancedbytes.games.ffb.client.ActionKey;
import com.balancedbytes.games.ffb.client.FantasyFootballClient;
import com.balancedbytes.games.ffb.client.GameTitle;
import com.balancedbytes.games.ffb.client.UserInterface;
import com.balancedbytes.games.ffb.model.Game;

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
