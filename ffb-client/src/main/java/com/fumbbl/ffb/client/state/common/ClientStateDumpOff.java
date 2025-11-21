package com.fumbbl.ffb.client.state.common;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.UserInterface;
import com.fumbbl.ffb.client.state.AbstractClientStateMove;
import com.fumbbl.ffb.client.state.logic.DumpOffLogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

/**
 *
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public class ClientStateDumpOff extends AbstractClientStateMove<DumpOffLogicModule> {

	public ClientStateDumpOff(FantasyFootballClientAwt pClient) {
		super(pClient, new DumpOffLogicModule(pClient));
	}

	public void clickOnPlayer(Player<?> player) {
		Game game = getClient().getGame();
		FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(player);
		clickOnField(playerCoordinate);
	}

	public void clickOnField(FieldCoordinate pCoordinate) {
		InteractionResult result = logicModule.fieldInteraction(pCoordinate);
		if (result.getKind() == InteractionResult.Kind.PERFORM) {
			UserInterface userInterface = getClient().getUserInterface();
			userInterface.getFieldComponent().refresh();
		}
	}

	public boolean mouseOverField(FieldCoordinate pCoordinate) {
		boolean selectable = false;
		UserInterface userInterface = getClient().getUserInterface();
		InteractionResult result = logicModule.fieldPeek(pCoordinate);
		determineCursor(result);
		switch (result.getKind()) {
			case RESET:
				selectable = true;
				break;
			default:
				break;
		}
		userInterface.getFieldComponent().refresh();
		return selectable;
	}

	@Override
	protected String validCursor() {
		return IIconProperty.CURSOR_PASS;
	}

	public boolean mouseOverPlayer(Player<?> pPlayer) {
		logicModule.playerPeek(pPlayer);
		UserInterface userInterface = getClient().getUserInterface();
		userInterface.refreshSideBars();
		Game game = getClient().getGame();
		FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(pPlayer);
		return mouseOverField(playerCoordinate);
	}

	@Override
	public void tearDown() {
		getClient().getUserInterface().getFieldComponent().getLayerRangeRuler().removeRangeRuler();
		getClient().getUserInterface().getFieldComponent().refresh();
		super.tearDown();
	}
}
