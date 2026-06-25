package com.fumbbl.ffb.client.state.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.state.ClientStateAwt;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.bb2025.PlaceCarriedPlayerLogicModule;
import com.fumbbl.ffb.model.Player;

import java.util.Collections;
import java.util.Map;

@RulesCollection(RulesCollection.Rules.BB2025)
public class ClientStatePlaceCarriedPlayer extends ClientStateAwt<PlaceCarriedPlayerLogicModule> {

	public ClientStatePlaceCarriedPlayer(FantasyFootballClientAwt client) {
		super(client, new PlaceCarriedPlayerLogicModule(client));
	}

	@Override
	public void clickOnField(FieldCoordinate coordinate) {
		logicModule.fieldInteraction(coordinate);
	}

	@Override
	public boolean mouseOverField(FieldCoordinate coordinate) {
		determineCursor(logicModule.fieldPeek(coordinate));
		return super.mouseOverField(coordinate);
	}

	@Override
	public boolean mouseOverPlayer(Player<?> player) {
		determineCursor(logicModule.playerPeek(player));
		return super.mouseOverPlayer(player);
	}

	@Override
	protected Map<Integer, ClientAction> actionMapping(int menuIndex) {
		return Collections.emptyMap();
	}

	@Override
	protected String validCursor() {
		return IIconProperty.CURSOR_PASS;
	}

	@Override
	public void clickOnPlayer(Player<?> player) {
		logicModule.playerInteraction(player);
	}
}
