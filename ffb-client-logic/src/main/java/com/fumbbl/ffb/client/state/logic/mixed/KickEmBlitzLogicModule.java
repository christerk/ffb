package com.fumbbl.ffb.client.state.logic.mixed;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.state.logic.BlitzLogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilPlayer;

public class KickEmBlitzLogicModule extends BlitzLogicModule {
	public KickEmBlitzLogicModule(FantasyFootballClient client) {
		super(client);
	}

	@Override
	public ClientStateId getId() {
		return ClientStateId.KICK_EM_BLITZ;
	}

	@Override
	protected PlayerAction moveAction() {
		return PlayerAction.KICK_EM_BLITZ;
	}

	@Override
	public InteractionResult playerInteraction(Player<?> player) {
			Game game = client.getGame();
			ActingPlayer actingPlayer = game.getActingPlayer();
			if (player == actingPlayer.getPlayer()) {
				return super.playerInteraction(player);
			} else {
				if (UtilPlayer.isNextMoveGoingForIt(game) && !actingPlayer.isGoingForIt()) {
					return InteractionResult.selectAction(actionContext(actingPlayer));
				} else {
					if (UtilCards.hasUnusedSkillWithProperty(actingPlayer, NamedProperties.canUseChainsawOnDownedOpponents)
						&& game.getFieldModel().getPlayerState(player).isProneOrStunned()
						&& game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer()).isAdjacent(game.getFieldModel().getPlayerCoordinate(player))) {
						extension.block(actingPlayer.getPlayerId(), player, false, true, false, false, false);
						return InteractionResult.perform();
					}
				}
			}

			return InteractionResult.ignore();
	}

	@Override
	public InteractionResult playerPeek(Player<?> player) {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (!actingPlayer.hasBlocked() && UtilPlayer.isKickable(game, player)) {
			return InteractionResult.perform();
		} else {
			return InteractionResult.reset();
		}
	}

}
