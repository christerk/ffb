package com.fumbbl.ffb.client.state.logic.bb2020;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.state.logic.BlockLogicModule;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

import java.util.HashSet;
import java.util.Set;

public class MaximumCarnageLogicModule extends BlockLogicModule {
	public MaximumCarnageLogicModule(FantasyFootballClient pClient) {
		super(pClient);
	}

	@Override
	public ClientStateId getId() {
		return ClientStateId.MAXIMUM_CARNAGE;
	}

	@Override
	public InteractionResult playerInteraction(Player<?> pPlayer) {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (actingPlayer.getPlayer() == pPlayer) {
			return new InteractionResult(InteractionResult.Kind.SHOW_ACTIONS);
		} else if (!pPlayer.getId().equalsIgnoreCase(game.getLastDefenderId()) && !game.getActingTeam().hasPlayer(pPlayer)) {
			extension.block(actingPlayer.getPlayerId(), pPlayer, false, true, false, false);
			return new InteractionResult(InteractionResult.Kind.HANDLED);
		}
		return new InteractionResult(InteractionResult.Kind.IGNORE);
	}

	@Override
	public InteractionResult playerPeek(Player<?> pPlayer) {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (actingPlayer.getPlayer() == pPlayer || pPlayer.getId().equalsIgnoreCase(game.getLastDefenderId()) || game.getActingTeam().hasPlayer(pPlayer)) {
			return new InteractionResult(InteractionResult.Kind.RESET);
		} else {
			return new InteractionResult(InteractionResult.Kind.PERFORM);
		}
	}

	@Override
	public Set<ClientAction> availableActions() {
		Set<ClientAction> actions = new HashSet<>();
		actions.add(ClientAction.END_MOVE);
		return actions;
	}

	@Override
	protected void performAvailableAction(Player<?> player, ClientAction action) {
		switch (action) {
			case END_MOVE:
				super.performAvailableAction(player, action);
				break;
			default:
				break;
		}
	}
}
