package com.fumbbl.ffb.client.state.bb2020;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.state.ClientStateAwt;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.PlaceBallLogicModule;
import com.fumbbl.ffb.model.Player;

import java.util.Collections;
import java.util.Map;

@RulesCollection(RulesCollection.Rules.BB2020)
public class ClientStatePlaceBall extends ClientStateAwt<PlaceBallLogicModule> {

	public ClientStatePlaceBall(FantasyFootballClientAwt pClient) {
		super(pClient, new PlaceBallLogicModule(pClient));
	}

	@Override
	public void clickOnField(FieldCoordinate pCoordinate) {
		logicModule.fieldInteraction(pCoordinate);
	}

	@Override
	public boolean mouseOverField(FieldCoordinate pCoordinate) {
		determineCursor(logicModule.fieldPeek(pCoordinate));
		return super.mouseOverField(pCoordinate);
	}

	@Override
	public boolean mouseOverPlayer(Player<?> pPlayer) {
		determineCursor(logicModule.playerPeek(pPlayer));
		return super.mouseOverPlayer(pPlayer);
	}

	@Override
	protected Map<Integer, ClientAction> actionMapping(int menuIndex) {
		return Collections.emptyMap();
	}

	@Override
	protected String validCursor() {
		return IIconProperty.CURSOR_PASS;
	}
}
