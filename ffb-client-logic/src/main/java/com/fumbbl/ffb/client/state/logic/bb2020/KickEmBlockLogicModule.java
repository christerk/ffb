package com.fumbbl.ffb.client.state.logic.bb2020;

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

	public InteractionResult playerInteraction(Player<?> pPlayer) {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (pPlayer == actingPlayer.getPlayer()) {
			return new InteractionResult(InteractionResult.Kind.SUPER);
		} else if (UtilCards.hasUnusedSkillWithProperty(actingPlayer, NamedProperties.canUseChainsawOnDownedOpponents)
			&& game.getFieldModel().getPlayerState(pPlayer).isProneOrStunned()
			&& game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer()).isAdjacent(game.getFieldModel().getPlayerCoordinate(pPlayer))) {
			extension.block(actingPlayer.getPlayerId(), pPlayer, false, true, false, false);
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
