package com.fumbbl.ffb.client.state.logic.mixed;

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
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.util.UtilCards;

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
		if (UtilCards.hasUnusedSkillWithProperty(actingPlayer, NamedProperties.canMoveAfterBlock)) {
			context.add(ClientAction.HIT_AND_RUN);
		}
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
			return InteractionResult.perform();
		}
		return InteractionResult.ignore();
	}

	private boolean isValidField(FieldCoordinate coordinate) {
		return client.getGame().getFieldModel().getMoveSquare(coordinate) != null;
	}

	@Override
	public InteractionResult playerInteraction(Player<?> player) {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (player == actingPlayer.getPlayer()) {
			return InteractionResult.selectAction(actionContext(actingPlayer));
		}
		return InteractionResult.ignore();
	}

	@Override
	public InteractionResult playerPeek(Player<?> player) {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (player == actingPlayer.getPlayer()) {
			return InteractionResult.reset();
		} else {
			return InteractionResult.invalid();
		}
	}

	@Override
	public InteractionResult fieldPeek(FieldCoordinate coordinate) {
		if (isValidField(coordinate)) {
			return InteractionResult.perform();
		}
		return InteractionResult.invalid();
	}
}
