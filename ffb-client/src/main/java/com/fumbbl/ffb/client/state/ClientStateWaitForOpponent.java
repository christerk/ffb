package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.WaitForOpponentLogicModule;

import java.util.Collections;
import java.util.Map;

/**
 * @author Kalimar
 */
public class ClientStateWaitForOpponent extends ClientStateAwt<WaitForOpponentLogicModule> {

	protected ClientStateWaitForOpponent(FantasyFootballClientAwt pClient) {
		super(pClient, new WaitForOpponentLogicModule(pClient));
	}

	public void setUp() {
		super.setUp();
		setClickable(false);
	}

	public boolean actionKeyPressed(ActionKey pActionKey) {
		boolean actionHandled = true;
		switch (pActionKey) {
			case TOOLBAR_ILLEGAL_PROCEDURE:
				logicModule.illegalProcedure();
				break;
			default:
				actionHandled = handleResize(pActionKey);
				break;
		}
		return actionHandled;
	}

	@Override
	protected Map<Integer, ClientAction> actionMapping() {
		return Collections.emptyMap();
	}

}
