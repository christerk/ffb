package com.fumbbl.ffb.client.state.common;

import com.fumbbl.ffb.Direction;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.ActionKey;
import com.fumbbl.ffb.client.FantasyFootballClientAwt;
import com.fumbbl.ffb.client.FieldComponent;
import com.fumbbl.ffb.client.layer.FieldLayerOverPlayers;
import com.fumbbl.ffb.client.state.ClientStateAwt;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.PushbackLogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.client.util.UtilClientActionKeys;
import com.fumbbl.ffb.model.Player;

import java.util.Collections;
import java.util.Map;

/**
 * @author Kalimar
 */
@RulesCollection(RulesCollection.Rules.COMMON)
public class ClientStatePushback extends ClientStateAwt<PushbackLogicModule> {

	public ClientStatePushback(FantasyFootballClientAwt pClient) {
		super(pClient, new PushbackLogicModule(pClient));
	}

	public boolean mouseOverPlayer(Player<?> pPlayer) {
		super.mouseOverPlayer(pPlayer);
		InteractionResult result = logicModule.playerPeek(pPlayer);
		switch (result.getKind()) {
			case HANDLED:
				updatePushbackSquares(result);
				return true;
			default:
				return false;
		}
	}

	private void updatePushbackSquares(InteractionResult result) {
		FieldComponent fieldComponent = getClient().getUserInterface().getFieldComponent();
		FieldLayerOverPlayers layerOverPlayers = fieldComponent.getLayerOverPlayers();
		result.getPushbackSquares().forEach(layerOverPlayers::drawPushbackSquare);
		fieldComponent.refresh();
	}

	public boolean mouseOverField(FieldCoordinate pCoordinate) {
		super.mouseOverField(pCoordinate);
		InteractionResult result = logicModule.fieldPeek(pCoordinate);
		switch (result.getKind()) {
			case HANDLED:
				updatePushbackSquares(result);
				return true;
			default:
				return false;
		}
	}

	public void clickOnField(FieldCoordinate pCoordinate) {
		logicModule.fieldInteraction(pCoordinate);
	}

	public void clickOnPlayer(Player<?> pPlayer) {
		logicModule.playerInteraction(pPlayer);
	}

	public boolean actionKeyPressed(ActionKey pActionKey, int menuIndex) {
		Direction moveDirection = UtilClientActionKeys.findMoveDirection(pActionKey);
		if (moveDirection != null) {
			return logicModule.pushbackTo(moveDirection);
		}
		return handleResize(pActionKey);
	}

	@Override
	protected Map<Integer, ClientAction> actionMapping(int menuIndex) {
		return Collections.emptyMap();
	}
}
