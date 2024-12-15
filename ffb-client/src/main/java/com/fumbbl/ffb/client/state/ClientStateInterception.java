package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.InterceptionLogicModule;
import com.fumbbl.ffb.model.Player;

import java.util.Collections;
import java.util.Map;

/**
 *
 * @author Kalimar
 */
public class ClientStateInterception extends ClientStateAwt<InterceptionLogicModule> {

	protected ClientStateInterception(FantasyFootballClientAwt pClient) {
		super(pClient, new InterceptionLogicModule(pClient));
	}

	public ClientStateId getId() {
		return ClientStateId.INTERCEPTION;
	}

	public void initUI() {
		super.initUI();
		setClickable(true);
	}

	protected void clickOnPlayer(Player<?> pPlayer) {
		logicModule.playerInteraction(pPlayer);
	}

	protected boolean mouseOverPlayer(Player<?> pPlayer) {
		super.mouseOverPlayer(pPlayer);
		determineCursor(logicModule.playerPeek(pPlayer));
		return true;
	}

	protected boolean mouseOverField(FieldCoordinate pCoordinate) {
		super.mouseOverField(pCoordinate);
		determineCursor(logicModule.fieldPeek(pCoordinate));
		return true;
	}

	@Override
	protected Map<Integer, ClientAction> actionMapping() {
		return Collections.emptyMap();
	}

}
