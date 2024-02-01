package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.BoxType;
import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.UserInterface;

/**
 * 
 * @author Kalimar
 */
public class ClientStateStartGame extends ClientState {

	protected ClientStateStartGame(FantasyFootballClient pClient) {
		super(pClient);
	}

	public ClientStateId getId() {
		return ClientStateId.START_GAME;
	}

	public void enterState() {
		super.enterState();
		setClickable(false);
		UserInterface userInterface = getClient().getUserInterface();
		userInterface.getSideBarAway().openBox(BoxType.RESERVES);
	}

	public void leaveState() {
		closeAwayBox();
	}

	private void closeAwayBox() {
		UserInterface userInterface = getClient().getUserInterface();
		if (BoxType.RESERVES == userInterface.getSideBarAway().getBoxComponent().getOpenBox()) {
			userInterface.getSideBarAway().closeBox();
		}
	}

}
