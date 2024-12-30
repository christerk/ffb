package com.fumbbl.ffb.client.state;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.PlaceBallLogicModule;
import com.fumbbl.ffb.model.Player;

import java.util.Collections;
import java.util.Map;

public class ClientStatePlaceBall extends ClientStateAwt<PlaceBallLogicModule> {

	protected ClientStatePlaceBall(FantasyFootballClientAwt pClient) {
		super(pClient, new PlaceBallLogicModule(pClient));
	}

	@Override
	protected void clickOnField(FieldCoordinate pCoordinate) {
		logicModule.fieldInteraction(pCoordinate);
	}

	@Override
	protected boolean mouseOverField(FieldCoordinate pCoordinate) {
		determineCursor(logicModule.fieldPeek(pCoordinate));
		return super.mouseOverField(pCoordinate);
	}

	@Override
	protected boolean mouseOverPlayer(Player<?> pPlayer) {
		determineCursor(logicModule.playerPeek(pPlayer));
		return super.mouseOverPlayer(pPlayer);
	}

	@Override
	protected Map<Integer, ClientAction> actionMapping() {
		return Collections.emptyMap();
	}

	@Override
	protected String validCursor() {
		return IIconProperty.CURSOR_PASS;
	}
}
