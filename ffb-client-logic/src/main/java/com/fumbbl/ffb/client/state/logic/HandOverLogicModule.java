package com.fumbbl.ffb.client.state.logic;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.util.UtilPlayer;

/**
 * @author Kalimar
 */
public class HandOverLogicModule extends MoveLogicModule {

	public HandOverLogicModule(FantasyFootballClient pClient) {
		super(pClient);
	}

	@Override
	public ClientStateId getId() {
		return ClientStateId.HAND_OVER;
	}

	@Override
	public InteractionResult playerInteraction(Player<?> player) {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (player == actingPlayer.getPlayer()) {
			return super.playerInteraction(player);
		} else {
			return handOver(player);
		}
	}

	@Override
	public InteractionResult playerPeek(Player<?> pPlayer) {
		if (canPlayerGetHandOver(pPlayer)) {
			return new InteractionResult(InteractionResult.Kind.PERFORM);
		} else {
			return new InteractionResult(InteractionResult.Kind.IGNORE);
		}
	}

	@Override
	public InteractionResult fieldPeek(FieldCoordinate pCoordinate) {
		return new InteractionResult(InteractionResult.Kind.IGNORE);
	}

	public boolean canPlayerGetHandOver(Player<?> pCatcher) {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if ((pCatcher != null) && (actingPlayer.getPlayer() != null)) {
			FieldModel fieldModel = game.getFieldModel();
			FieldCoordinate throwerCoordinate = fieldModel.getPlayerCoordinate(actingPlayer.getPlayer());
			FieldCoordinate catcherCoordinate = fieldModel.getPlayerCoordinate(pCatcher);
			PlayerState catcherState = fieldModel.getPlayerState(pCatcher);
			return (throwerCoordinate.isAdjacent(catcherCoordinate) && (catcherState != null)
				&& (!actingPlayer.isSufferingAnimosity() || actingPlayer.getRace().equals(pCatcher.getRace()))
				&& (catcherState.hasTacklezones()
				&& (game.getTeamHome() == pCatcher.getTeam() || actingPlayer.getPlayerAction() == PlayerAction.HAND_OVER)));
		}
		return false;
	}

	private InteractionResult handOver(Player<?> pCatcher) {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (UtilPlayer.hasBall(game, actingPlayer.getPlayer()) && canPlayerGetHandOver(pCatcher)) {
			client.getCommunication().sendHandOver(actingPlayer.getPlayerId(), pCatcher);
			return new InteractionResult(InteractionResult.Kind.HANDLED);
		}
		return new InteractionResult(InteractionResult.Kind.IGNORE);
	}

	public boolean ballInHand() {
		Game game = client.getGame();
		return UtilPlayer.hasBall(game, game.getActingPlayer().getPlayer());
	}

}
