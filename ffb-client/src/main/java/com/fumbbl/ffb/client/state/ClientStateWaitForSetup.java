package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.BoxType;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
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
public class ClientStateWaitForSetup extends ClientStateAwt<WaitForSetupLogicModule> {

	private boolean fReservesBoxOpened;

	protected ClientStateWaitForSetup(FantasyFootballClientAwt pClient) {
		super(pClient, new WaitForSetupLogicModule(pClient));
	}

	public void initUI() {
		super.initUI();
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

	@Override
	protected Map<Integer, ClientAction> actionMapping() {
		return Collections.emptyMap();
	}

}
