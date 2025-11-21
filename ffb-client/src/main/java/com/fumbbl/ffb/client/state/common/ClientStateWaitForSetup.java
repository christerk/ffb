package com.fumbbl.ffb.client.state.common;

import com.fumbbl.ffb.BoxType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.state.ClientStateAwt;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.WaitForSetupLogicModule;
import com.fumbbl.ffb.client.ui.SideBarComponent;
import com.fumbbl.ffb.model.Game;

import java.util.Collections;
import java.util.Map;

/**
 * 
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public class ClientStateWaitForSetup extends ClientStateAwt<WaitForSetupLogicModule> {

	private boolean fReservesBoxOpened;

	public ClientStateWaitForSetup(FantasyFootballClientAwt pClient) {
		super(pClient, new WaitForSetupLogicModule(pClient));
	}

	public void setUp() {
		super.setUp();
		setClickable(false);
		Game game = getClient().getGame();
		SideBarComponent sideBarAway = getClient().getUserInterface().getSideBarAway();
		fReservesBoxOpened = ((game.getTurnMode() == TurnMode.SETUP) && !sideBarAway.isBoxOpen());
		if (fReservesBoxOpened) {
			sideBarAway.openBox(BoxType.RESERVES);
		}
	}

	public void tearDown() {
		SideBarComponent sideBarAway = getClient().getUserInterface().getSideBarAway();
		if (fReservesBoxOpened && (sideBarAway.getOpenBox() == BoxType.RESERVES)) {
			sideBarAway.closeBox();
		}
		super.tearDown();
	}

	@Override
	protected Map<Integer, ClientAction> actionMapping() {
		return Collections.emptyMap();
	}

}
