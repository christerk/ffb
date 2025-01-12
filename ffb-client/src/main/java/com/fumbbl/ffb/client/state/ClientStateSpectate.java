package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.GameTitle;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.SpectateLogicModule;

import java.util.Collections;
import java.util.Map;

/**
 * 
 * @author Kalimar
 */
public class ClientStateSpectate extends ClientStateAwt<SpectateLogicModule> {

	protected ClientStateSpectate(FantasyFootballClientAwt pClient) {
		super(pClient, new SpectateLogicModule(pClient));
	}

	public void initUI() {
		super.initUI();
		setClickable(false);
		if (logicModule.canSwitchToSpectate()) {
			UserInterface userInterface = getClient().getUserInterface();
			GameTitle gameTitle = userInterface.getGameTitle();
			gameTitle.setClientMode(ClientMode.SPECTATOR);
			userInterface.setGameTitle(gameTitle);
			userInterface.getGameMenuBar().refresh();
		}

	}

	public boolean actionKeyPressed(ActionKey pActionKey) {
		boolean actionHandled;
		if (pActionKey == ActionKey.MENU_REPLAY) {
			actionHandled = true;
			logicModule.startReplay();
			getClient().getUserInterface().getGameMenuBar().refresh();
		} else {
			actionHandled = handleResize(pActionKey);
		}
		return actionHandled;
	}

	@Override
	protected Map<Integer, ClientAction> actionMapping() {
		return Collections.emptyMap();
	}

}
