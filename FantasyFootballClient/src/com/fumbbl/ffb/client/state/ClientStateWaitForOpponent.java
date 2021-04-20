package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClient;

/**
 * 
 * @author Kalimar
 */
public class ClientStateWaitForOpponent extends ClientState {

	protected ClientStateWaitForOpponent(FantasyFootballClient pClient) {
		super(pClient);
	}

	public ClientStateId getId() {
		return ClientStateId.WAIT_FOR_OPPONENT;
	}

	public void enterState() {
		super.enterState();
		setSelectable(true);
		setClickable(false);
	}

	public boolean actionKeyPressed(ActionKey pActionKey) {
		boolean actionHandled = true;
		switch (pActionKey) {
		case TOOLBAR_ILLEGAL_PROCEDURE:
			getClient().getCommunication().sendIllegalProcedure();
			break;
		default:
			actionHandled = false;
			break;
		}
		return actionHandled;
	}

}
