package com.fumbbl.ffb.client.state.bb2025;

import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.state.AbstractClientStateMove;
import com.fumbbl.ffb.client.state.logic.bb2025.GazeLogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.model.Player;

@RulesCollection(RulesCollection.Rules.BB2025)
public class ClientStateGaze extends AbstractClientStateMove<GazeLogicModule> {

	public ClientStateGaze(FantasyFootballClientAwt pClient) {
		super(pClient, new GazeLogicModule(pClient));
	}

	public boolean mouseOverPlayer(Player<?> pPlayer) {
		super.mouseOverPlayer(pPlayer);
		determineCursor(logicModule.playerPeek(pPlayer));
		return true;
	}

	@Override
	protected String validCursor() {
		return IIconProperty.CURSOR_GAZE;
	}

	@Override
	public void clickOnPlayer(Player<?> player) {
		InteractionResult result = logicModule.playerInteraction(player);
		evaluateClick(result, player);
	}
}
