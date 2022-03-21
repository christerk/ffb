package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.BoxType;
import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.ui.SideBarComponent;
import com.fumbbl.ffb.model.Game;

/**
 * 
 * @author Kalimar
 */
public class ClientStateWaitForSetup extends ClientState {

	private boolean fReservesBoxOpened;

	protected ClientStateWaitForSetup(FantasyFootballClient pClient) {
		super(pClient);
	}

	public ClientStateId getId() {
		return ClientStateId.WAIT_FOR_SETUP;
	}

	public void enterState() {
		super.enterState();
		setClickable(false);
		Game game = getClient().getGame();
		SideBarComponent sideBarAway = getClient().getUserInterface().getSideBarAway();
		fReservesBoxOpened = ((game.getTurnMode() == TurnMode.SETUP) && !sideBarAway.isBoxOpen());
		if (fReservesBoxOpened) {
			sideBarAway.openBox(BoxType.RESERVES);
		}
	}

	public void leaveState() {
		SideBarComponent sideBarAway = getClient().getUserInterface().getSideBarAway();
		if (fReservesBoxOpened && (sideBarAway.getOpenBox() == BoxType.RESERVES)) {
			sideBarAway.closeBox();
		}
	}

}
