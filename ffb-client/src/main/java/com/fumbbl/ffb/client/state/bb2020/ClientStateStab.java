package com.fumbbl.ffb.client.state.bb2020;

import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.layer.FieldLayerRangeRuler;
import com.fumbbl.ffb.client.state.AbstractClientStateBlock;
import com.fumbbl.ffb.client.state.logic.bb2020.StabLogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.model.Player;

@RulesCollection(RulesCollection.Rules.BB2020)
public class ClientStateStab extends AbstractClientStateBlock<StabLogicModule> {

	private Player<?>[] targets;
	public ClientStateStab(FantasyFootballClientAwt pClient) {
		super(pClient, new StabLogicModule(pClient));
	}

	@Override
	public void setUp() {
		targets = logicModule.getTargets();
		markTargets();
	}

	@Override
	public void tearDown() {
		UserInterface userInterface = getClient().getUserInterface();
		userInterface.getFieldComponent().getLayerRangeRuler().clearMarkedCoordinates();
		userInterface.getFieldComponent().refresh();
		super.tearDown();
	}

	private void markTargets() {
		UserInterface userInterface = getClient().getUserInterface();
		userInterface.getFieldComponent().getLayerRangeRuler().markPlayers(targets, FieldLayerRangeRuler.COLOR_THROWABLE_PLAYER);
		userInterface.getFieldComponent().refresh();
	}

	@Override
	public boolean mouseOverPlayer(Player<?> player) {
		super.mouseOverPlayer(player);
		InteractionResult result = logicModule.playerPeek(player);
		determineCursor(result);
		return true;
	}

	@Override
	protected String validCursor() {
		return IIconProperty.CURSOR_BLADE;
	}
}
