package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.BoxType;
import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.StartGameLogicModule;

import java.util.Collections;
import java.util.Map;

/**
 * 
 * @author Kalimar
 */
public class ClientStateStartGame extends ClientStateAwt<StartGameLogicModule> {

	protected ClientStateStartGame(FantasyFootballClientAwt pClient) {
		super(pClient, new StartGameLogicModule(pClient));
	}

	public ClientStateId getId() {
		return ClientStateId.START_GAME;
	}

	public void initUI() {
		super.initUI();
		setClickable(false);
		UserInterface userInterface = getClient().getUserInterface();
		userInterface.getSideBarAway().openBox(BoxType.RESERVES);
	}

	public void leaveState() {
		closeAwayBox();
	}

	@Override
	protected Map<Integer, ClientAction> actionMapping() {
		return Collections.emptyMap();
	}

	private void closeAwayBox() {
		UserInterface userInterface = getClient().getUserInterface();
		if (BoxType.RESERVES == userInterface.getSideBarAway().getBoxComponent().getOpenBox()) {
			userInterface.getSideBarAway().closeBox();
		}
	}

}
