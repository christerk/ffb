package com.fumbbl.ffb.client.state.logic;

import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

import java.util.HashSet;
import java.util.Set;

public class BlockLogicModule extends LogicModule {

	private final BlockLogicExtension extension = new BlockLogicExtension();

	public BlockLogicModule(FantasyFootballClient client) {
		super(client);
	}

	@Override
	public InteractionResult playerInteraction(Player<?> player) {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (actingPlayer.getPlayer() == player) {
			if (isSufferingBloodLust(actingPlayer)) {
				return new InteractionResult(InteractionResult.Kind.SHOW_ACTIONS);
			} else if (PlayerAction.BLITZ == actingPlayer.getPlayerAction()) {
				client.getCommunication().sendActingPlayer(actingPlayer.getPlayer(), PlayerAction.BLITZ_MOVE,
					actingPlayer.isJumping());
			} else {
				return new InteractionResult(InteractionResult.Kind.SHOW_ACTIONS);
			}
		} else {
			return new InteractionResult(InteractionResult.Kind.PERFORM);
		}
		return new InteractionResult(InteractionResult.Kind.IGNORE);
	}


	@Override
	public InteractionResult playerPeek(Player<?> player) {
		if (extension.isBlockable(client.getGame(), player)) {
			return new InteractionResult(InteractionResult.Kind.PERFORM);
		}

		return new InteractionResult(InteractionResult.Kind.IGNORE);
	}

	@Override
	public Set<ClientAction> availableActions() {
		return new HashSet<ClientAction>() {{
			add(ClientAction.MOVE);
			add(ClientAction.END_MOVE);
			addAll(extension.genericBlockActions());
		}};
	}

	@Override
	protected void performAvailableAction(Player<?> player, ClientAction action) {
		switch (action) {
			case END_MOVE:
				client.getCommunication().sendActingPlayer(null, null, false);
				break;
			case MOVE:
				client.getCommunication().sendActingPlayer(player, PlayerAction.MOVE, client.getGame().getActingPlayer().isJumping());
				break;
			default:
				extension.performBlockAction(client, player, action);
				break;
		}
	}

	@Override
	public void endTurn() {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		perform(actingPlayer.getPlayer(), ClientAction.END_MOVE);
		client.getCommunication().sendEndTurn(game.getTurnMode());
	}

	public boolean isSufferingBloodLust(ActingPlayer actingPlayer) {
		return actingPlayer.isSufferingBloodLust();
	}

}
