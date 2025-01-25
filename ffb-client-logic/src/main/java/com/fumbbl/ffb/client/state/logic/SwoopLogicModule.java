package com.fumbbl.ffb.client.state.logic;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

/**
 *
 * @author Kalimar
 */
public class SwoopLogicModule extends MoveLogicModule {

	public SwoopLogicModule(FantasyFootballClient pClient) {
		super(pClient);
	}

	public ClientStateId getId() {
		return ClientStateId.SWOOP;
	}

	@Override
	public InteractionResult fieldInteraction(FieldCoordinate pCoordinate) {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();

		if (actingPlayer.getPlayerAction() == PlayerAction.SWOOP) {
			sendSwoop(game, actingPlayer, pCoordinate);
			return InteractionResult.handled();
		}
		return InteractionResult.ignore();
	}

	@Override
	public InteractionResult playerInteraction(Player<?> pPlayer) {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();

		if (actingPlayer.getPlayerAction() == PlayerAction.SWOOP) {
			FieldCoordinate coordinate = game.getFieldModel().getPlayerCoordinate(pPlayer);
			sendSwoop(game, actingPlayer, coordinate);
			return InteractionResult.handled();
		}
		return InteractionResult.ignore();
	}

	@Override
	public InteractionResult playerPeek(Player<?> pPlayer) {
		Game game = client.getGame();
		if ((game.getDefender() == null) && (game.getPassCoordinate() == null)) {
			return InteractionResult.reset();
		}
		return InteractionResult.ignore();
	}

	private void sendSwoop(Game game, ActingPlayer actingPlayer, FieldCoordinate destination) {
		FieldCoordinate source = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
		if (source.isAdjacent(destination)) {
			// Check if the destination is in one of the 4 cardinal directions from the
			// player
			if (source.getY() == destination.getY() || source.getX() == destination.getX()) {
				client.getCommunication().sendSwoop(actingPlayer.getPlayerId(), destination);
			}
		}
	}
}
