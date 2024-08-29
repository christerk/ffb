package com.fumbbl.ffb.client.state.logic.bb2020;

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
	protected PlayerAction moveAction() {
		return PlayerAction.KICK_EM_BLITZ;
	}

	@Override
	public InteractionResult playerInteraction(Player<?> player) {
			Game game = client.getGame();
			ActingPlayer actingPlayer = game.getActingPlayer();
			if (player == actingPlayer.getPlayer()) {
				return new InteractionResult(InteractionResult.Kind.SUPER);
			} else {
				if (UtilPlayer.isNextMoveGoingForIt(game) && !actingPlayer.isGoingForIt()) {
					return new InteractionResult(InteractionResult.Kind.SHOW_ACTIONS);
				} else {
					if (UtilCards.hasUnusedSkillWithProperty(actingPlayer, NamedProperties.canUseChainsawOnDownedOpponents)
						&& game.getFieldModel().getPlayerState(player).isProneOrStunned()
						&& game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer()).isAdjacent(game.getFieldModel().getPlayerCoordinate(player))) {
						return new InteractionResult(InteractionResult.Kind.PERFORM);
					}
				}
			}

			return new InteractionResult(InteractionResult.Kind.IGNORE);
	}

	@Override
	public InteractionResult playerPeek(Player<?> player) {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (!actingPlayer.hasBlocked() && UtilPlayer.isKickable(game, player)) {
			return new InteractionResult(InteractionResult.Kind.PERFORM);
		} else {
			return new InteractionResult(InteractionResult.Kind.IGNORE);
		}
	}

	/*
	/*
	public class ClientStateKickEmBlitz extends ClientStateBlitz {
	public ClientStateKickEmBlitz(FantasyFootballClient pClient) {
		super(pClient);
	}

	@Override
	public ClientStateId getId() {
		return ClientStateId.KICK_EM_BLITZ;
	}

	public void clickOnPlayer(Player<?> pPlayer) {
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (pPlayer == actingPlayer.getPlayer()) {
			super.clickOnPlayer(pPlayer);
		} else {
			if (UtilPlayer.isNextMoveGoingForIt(game) && !actingPlayer.isGoingForIt()) {
				createAndShowPopupMenuForActingPlayer();
			} else {
				if (UtilCards.hasUnusedSkillWithProperty(actingPlayer, NamedProperties.canUseChainsawOnDownedOpponents)
					&& game.getFieldModel().getPlayerState(pPlayer).isProneOrStunned()
					&& game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer()).isAdjacent(game.getFieldModel().getPlayerCoordinate(pPlayer))) {
					UtilClientStateBlocking.block(this, actingPlayer.getPlayerId(), pPlayer, false, true, false);
				}
			}
		}
	}

	protected boolean mouseOverPlayer(Player<?> pPlayer) {
		super.mouseOverPlayer(pPlayer);
		Game game = getClient().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (!actingPlayer.hasBlocked() && UtilPlayer.isKickable(game, pPlayer)) {
			UtilClientCursor.setCustomCursor(getClient().getUserInterface(), IIconProperty.CURSOR_BLOCK);
		} else {
			UtilClientCursor.setDefaultCursor(getClient().getUserInterface());
		}
		return true;
	}

	@Override
	protected PlayerAction moveAction() {
		return PlayerAction.KICK_EM_BLITZ;
	}
}
	 */
	 */
}
