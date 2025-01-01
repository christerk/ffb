package com.fumbbl.ffb.client.state.logic;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.state.logic.interaction.ActionContext;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Player;

import java.util.Collections;
import java.util.Set;

public class PlaceBallLogicModule extends LogicModule {

	public PlaceBallLogicModule(FantasyFootballClient pClient) {
		super(pClient);
	}

	public ClientStateId getId() {
		return ClientStateId.PLACE_BALL;
	}

	@Override
	public InteractionResult fieldInteraction(FieldCoordinate pCoordinate) {
		if (client.getGame().getFieldModel().getMoveSquare(pCoordinate) != null) {
			client.getCommunication().sendFieldCoordinate(pCoordinate);
			return new InteractionResult(InteractionResult.Kind.HANDLED);
		}
		return new InteractionResult(InteractionResult.Kind.IGNORE);
	}

	@Override
	public InteractionResult fieldPeek(FieldCoordinate pCoordinate) {
		FieldModel fieldModel = client.getGame().getFieldModel();
		if (fieldModel.getMoveSquare(pCoordinate) != null) {
			return new InteractionResult(InteractionResult.Kind.PERFORM);
		} else {
			return new InteractionResult(InteractionResult.Kind.RESET);
		}
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
		throw new UnsupportedOperationException("actionContext for acting player is not supported in place ball context");
	}

}
