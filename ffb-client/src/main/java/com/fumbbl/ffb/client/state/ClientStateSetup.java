package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.state.logic.SetupLogicModule;
import com.fumbbl.ffb.net.NetCommand;

/**
 * @author Kalimar
 */
public class ClientStateSetup extends AbstractClientStateSetup<SetupLogicModule> {

	protected boolean fLoadDialog;

	protected ClientStateSetup(FantasyFootballClientAwt pClient) {
		super(pClient, new SetupLogicModule(pClient));
	}

	public ClientStateId getId() {
		return ClientStateId.SETUP;
	}

	public boolean actionKeyPressed(ActionKey pActionKey) {
		switch (pActionKey) {
			case MENU_SETUP_LOAD:
				fLoadDialog = true;
				logicModule.requestSetups();
				return true;
			case MENU_SETUP_SAVE:
				fLoadDialog = false;
				logicModule.requestSetups();
				return true;
			default:
				return super.actionKeyPressed(pActionKey);
		}
	}

	public void handleCommand(NetCommand pNetCommand) {
		logicModule.handleCommand(pNetCommand, fLoadDialog);
	}

}
