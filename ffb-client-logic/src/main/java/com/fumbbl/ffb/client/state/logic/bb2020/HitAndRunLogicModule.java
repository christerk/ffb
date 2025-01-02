package com.fumbbl.ffb.client.state.logic.bb2020;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.LogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.ActionContext;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

import java.util.HashSet;
import java.util.Set;

public class HitAndRunLogicModule extends LogicModule {
	public HitAndRunLogicModule(FantasyFootballClient client) {
		super(client);
	}

	@Override
	public Set<ClientAction> availableActions() {
		return new HashSet<ClientAction>() {{
			add(ClientAction.HIT_AND_RUN);
		}};
	}

	@Override
	protected ActionContext actionContext(ActingPlayer actingPlayer) {
		ActionContext context = new ActionContext();
		context.add(ClientAction.HIT_AND_RUN);
		return context;
	}

	@Override
	public ClientStateId getId() {
		return ClientStateId.HIT_AND_RUN;
	}

	@Override
	protected void performAvailableAction(Player<?> player, ClientAction action) {
		if (action == ClientAction.HIT_AND_RUN) {
			client.getCommunication().sendEndTurn(client.getGame().getTurnMode());
		}
	}

	@Override
	public InteractionResult fieldInteraction(FieldCoordinate coordinate) {
		if (isValidField(coordinate)) {
			client.getCommunication().sendFieldCoordinate(coordinate);
			return new InteractionResult(InteractionResult.Kind.PERFORM);
		}
		return new InteractionResult(InteractionResult.Kind.IGNORE);
	}

	private boolean isValidField(FieldCoordinate coordinate) {
		return client.getGame().getFieldModel().getMoveSquare(coordinate) != null;
	}

	@Override
	public InteractionResult playerInteraction(Player<?> player) {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (player == actingPlayer.getPlayer()) {
			return new InteractionResult(InteractionResult.Kind.SHOW_ACTIONS);
		}
		return new InteractionResult(InteractionResult.Kind.IGNORE);
	}

	@Override
	public InteractionResult playerPeek(Player<?> player) {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (player == actingPlayer.getPlayer()) {
			return new InteractionResult(InteractionResult.Kind.RESET);
		} else {
			return new InteractionResult(InteractionResult.Kind.IGNORE);
		}
	}

	@Override
	public InteractionResult fieldPeek(FieldCoordinate coordinate) {
		if (isValidField(coordinate)) {
			return new InteractionResult(InteractionResult.Kind.PERFORM);
		}
		return new InteractionResult(InteractionResult.Kind.IGNORE);
	}
}
