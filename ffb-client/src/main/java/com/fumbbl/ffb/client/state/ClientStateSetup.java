package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.state.logic.SetupLogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.net.NetCommand;

/**
 * @author Kalimar
 */
public class ClientStateSetup extends AbstractClientStateSetup<SetupLogicModule> {

	protected boolean fLoadDialog;

	protected ClientStateSetup(FantasyFootballClientAwt pClient) {
		super(pClient, new SetupLogicModule(pClient));
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
		InteractionResult result = logicModule.handleCommand(pNetCommand, fLoadDialog);
		switch (result.getKind()) {
			case HANDLED:
				getClient().getUserInterface().getDialogManager().updateDialog();
				break;
			default:
				break;
		}
	}

}
