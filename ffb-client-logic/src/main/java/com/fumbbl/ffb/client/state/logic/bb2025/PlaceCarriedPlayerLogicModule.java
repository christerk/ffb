package com.fumbbl.ffb.client.state.logic.bb2025;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.LogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.ActionContext;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Player;

import java.util.Collections;
import java.util.Set;

public class PlaceCarriedPlayerLogicModule extends LogicModule {

	public PlaceCarriedPlayerLogicModule(FantasyFootballClient client) {
		super(client);
	}

	@Override
	public ClientStateId getId() {
		return ClientStateId.PLACE_CARRIED_PLAYER;
	}

	@Override
	public InteractionResult fieldInteraction(FieldCoordinate coordinate) {
		if (client.getGame().getFieldModel().getMoveSquare(coordinate) != null) {
			client.getCommunication().sendFieldCoordinate(coordinate);
			return InteractionResult.handled();
		}
		return InteractionResult.ignore();
	}

	@Override
	public InteractionResult fieldPeek(FieldCoordinate coordinate) {
		FieldModel fieldModel = client.getGame().getFieldModel();
		if (fieldModel.getMoveSquare(coordinate) != null) {
			return InteractionResult.perform();
		}
		return InteractionResult.reset();
	}

	@Override
	public Set<ClientAction> availableActions() {
		return Collections.emptySet();
	}

	@Override
	protected void performAvailableAction(Player<?> player, ClientAction action) {
	}

	@Override
	protected ActionContext actionContext(ActingPlayer actingPlayer) {
		throw new UnsupportedOperationException("actionContext for acting player is not supported in place carried player context");
	}
}
