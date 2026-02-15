package com.fumbbl.ffb.client.state.logic.mixed;

import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.state.logic.AbstractBlockLogicModule;
import com.fumbbl.ffb.client.state.logic.BlockLogicExtension;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.Influences;
import com.fumbbl.ffb.client.state.logic.interaction.ActionContext;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

import java.util.HashSet;
import java.util.Set;

public class BlockLogicModule extends AbstractBlockLogicModule {

	protected final BlockLogicExtension extension;

	public BlockLogicModule(FantasyFootballClient client) {
		super(client);
		extension = new BlockLogicExtension(client);
	}

	@Override
	public InteractionResult playerInteraction(Player<?> player) {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (actingPlayer.getPlayer() == player) {
			if (isSufferingBloodLust(actingPlayer)) {
				return InteractionResult.selectAction(actionContext(actingPlayer));
			} else if (PlayerAction.BLITZ == actingPlayer.getPlayerAction()) {
				client.getCommunication().sendActingPlayer(actingPlayer.getPlayer(), PlayerAction.BLITZ_MOVE,
					actingPlayer.isJumping());
				return InteractionResult.handled();
			} else {
				ActionContext actionContext = actionContext(actingPlayer);
				if (actionContext.getActions().isEmpty()) {
					deselectActingPlayer();
					return InteractionResult.handled();
				} else {
				return InteractionResult.selectAction(actionContext);
				}
			}
		} else {
			return block(player, actingPlayer);
		}
	}

	protected InteractionResult block(Player<?> player, ActingPlayer actingPlayer) {
		return extension.playerInteraction(player, actingPlayer.getPlayerAction().isBlitzing(), actingPlayer.getPlayerAction() == PlayerAction.MULTIPLE_BLOCK);
	}


	@Override
	public InteractionResult playerPeek(Player<?> player) {
		if (extension.isBlockable(client.getGame(), player)) {
			return InteractionResult.perform();
		}

		return InteractionResult.reset();
	}

	@Override
	public Set<ClientAction> availableActions() {
		return new HashSet<ClientAction>() {{
			add(ClientAction.MOVE);
			add(ClientAction.END_MOVE);
			addAll(extension.availableActions());
		}};
	}

	@Override
	protected ActionContext actionContext(ActingPlayer actingPlayer) {
		ActionContext actionContext = new ActionContext().merge(extension.actionContext(actingPlayer));
		if (isSufferingBloodLust(actingPlayer)) {
			actionContext.add(ClientAction.MOVE);
		}
		if (!actionContext.getActions().isEmpty() || actingPlayer.hasActed()) {
			actionContext.add(ClientAction.END_MOVE);
			if (actingPlayer.hasActed()) {
				actionContext.add(Influences.HAS_ACTED);
			}
		}
		return actionContext;
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
				extension.performAvailableAction(player, action);
				break;
		}
	}
}
