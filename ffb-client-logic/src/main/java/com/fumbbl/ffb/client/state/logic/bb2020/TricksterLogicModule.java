package com.fumbbl.ffb.client.state.logic.bb2020;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.LogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.ActionContext;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

import java.util.Collections;
import java.util.Set;

public class TricksterLogicModule extends LogicModule {
	public TricksterLogicModule(FantasyFootballClient pClient) {
		super(pClient);
	}


	@Override
	public ClientStateId getId() {
		return ClientStateId.TRICKSTER;
	}

	@Override
	public InteractionResult fieldInteraction(FieldCoordinate pCoordinate) {
		if (client.getGame().getFieldModel().getMoveSquare(pCoordinate) != null) {
			client.getCommunication().sendFieldCoordinate(pCoordinate);
			return InteractionResult.handled();
		}
		return InteractionResult.ignore();
	}

	@Override
	public InteractionResult playerInteraction(Player<?> player) {
		Game game = client.getGame();
		if (player == game.getDefender()) {
			return InteractionResult.selectAction(actionContext(client.getGame().getActingPlayer()));
		}
		return InteractionResult.ignore();
	}


	@Override
	public InteractionResult fieldPeek(FieldCoordinate pCoordinate) {
		if (client.getGame().getFieldModel().getMoveSquare(pCoordinate) != null) {
			return InteractionResult.perform();
		} else {
			return InteractionResult.invalid();
		}
	}

	@Override
	protected void performAvailableAction(Player<?> player, ClientAction action) {
		if (action == ClientAction.END_MOVE) {
			client.getCommunication().sendEndTurn(TurnMode.TRICKSTER);
		}
	}

	@Override
	public Set<ClientAction> availableActions() {
		return Collections.singleton(ClientAction.END_MOVE);
	}

	@Override
	protected ActionContext actionContext(ActingPlayer actingPlayer) {
		ActionContext actionContext = new ActionContext();
		actionContext.add(ClientAction.END_MOVE);
		return actionContext;
	}
}
