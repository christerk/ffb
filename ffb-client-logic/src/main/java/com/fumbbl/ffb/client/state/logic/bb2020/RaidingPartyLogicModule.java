package com.fumbbl.ffb.client.state.logic.bb2020;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerChoiceMode;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.LogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

import java.util.Collections;
import java.util.Set;

public class RaidingPartyLogicModule extends LogicModule {
	public RaidingPartyLogicModule(FantasyFootballClient pClient) {
		super(pClient);
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
			return new InteractionResult(InteractionResult.Kind.INVALID);
		}
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
		if (client.getGame().getFieldModel().getMoveSquare(pCoordinate) != null) {
			return new InteractionResult(InteractionResult.Kind.PERFORM);
		} else {
			return new InteractionResult(InteractionResult.Kind.INVALID);
		}
	}

	@Override
	public Set<ClientAction> availableActions() {
		return Collections.singleton(ClientAction.RAIDING_PARTY);
	}

	@Override
	protected void performAvailableAction(Player<?> player, ClientAction action) {
		switch (action) {
			case RAIDING_PARTY:
				client.getCommunication().sendPlayerChoice(PlayerChoiceMode.RAIDING_PARTY, null);
				break;
			default:
				break;
		}
	}
}
