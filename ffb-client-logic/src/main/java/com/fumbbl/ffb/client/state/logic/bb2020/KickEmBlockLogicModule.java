package com.fumbbl.ffb.client.state.logic.bb2020;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.state.logic.BlockLogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.util.UtilCards;
import com.fumbbl.ffb.util.UtilPlayer;

public class KickEmBlockLogicModule extends BlockLogicModule {
	public KickEmBlockLogicModule(FantasyFootballClient client) {
		super(client);
	}

	@Override
	public ClientStateId getId() {
		return ClientStateId.KICK_EM_BLOCK;
	}

	public InteractionResult playerInteraction(Player<?> player) {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (player == actingPlayer.getPlayer()) {
			return super.playerInteraction(player);
		} else if (UtilCards.hasUnusedSkillWithProperty(actingPlayer, NamedProperties.canUseChainsawOnDownedOpponents)
			&& game.getFieldModel().getPlayerState(player).isProneOrStunned()
			&& game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer()).isAdjacent(game.getFieldModel().getPlayerCoordinate(player))) {
			extension.block(actingPlayer.getPlayerId(), player, false, true, false, false);
		}
		return new InteractionResult(InteractionResult.Kind.IGNORE);
	}

	public InteractionResult playerPeek(Player<?> pPlayer) {
		if (UtilPlayer.isKickable(client.getGame(), pPlayer)) {
			return new InteractionResult(InteractionResult.Kind.PERFORM);
		} else {
			return new InteractionResult(InteractionResult.Kind.RESET);
		}
	}
}
