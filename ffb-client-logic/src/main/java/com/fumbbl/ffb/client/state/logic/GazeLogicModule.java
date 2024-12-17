package com.fumbbl.ffb.client.state.logic;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.model.*;
import com.fumbbl.ffb.util.UtilPlayer;

public class GazeLogicModule extends MoveLogicModule {
	public GazeLogicModule(FantasyFootballClient pClient) {
		super(pClient);
	}

	@Override
	public InteractionResult playerInteraction(Player<?> player) {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (player == actingPlayer.getPlayer()) {
			return super.playerInteraction(player);
		} else {
			if (canBeGazed(player)) {
				client.getCommunication().sendGaze(actingPlayer.getPlayerId(), player);
				return new InteractionResult(InteractionResult.Kind.HANDLED);
			}
		}
		return new InteractionResult(InteractionResult.Kind.IGNORE);
	}

	@Override
	public InteractionResult playerPeek(Player<?> pPlayer) {
		if (canBeGazed(pPlayer)) {
			return new InteractionResult(InteractionResult.Kind.PERFORM);
		} else {
			return new InteractionResult(InteractionResult.Kind.INVALID);
		}
	}

	@Override
	public boolean playerActivationUsed() {
		FieldModel fieldModel = client.getGame().getFieldModel();
		if (fieldModel.getTargetSelectionState() == null) {
			return super.playerActivationUsed();
		}
		return fieldModel.getTargetSelectionState().isCommitted();
	}


	// Added a check to see if the player had tacklezones so no prone players could
	// be gazed or already gazed players.
	private boolean canBeGazed(Player<?> pVictim) {
		boolean result = false;
		if (pVictim != null) {
			Game game = client.getGame();
			ActingPlayer actingPlayer = game.getActingPlayer();
			FieldCoordinate actorCoordinate = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
			FieldCoordinate victimCoordinate = game.getFieldModel().getPlayerCoordinate(pVictim);
			Team actorTeam = game.getTeamHome().hasPlayer(actingPlayer.getPlayer()) ? game.getTeamHome() : game.getTeamAway();
			Team victimTeam = game.getTeamHome().hasPlayer(pVictim) ? game.getTeamHome() : game.getTeamAway();
			result = (UtilPlayer.canGaze(game, actingPlayer.getPlayer()) && (victimCoordinate != null)
					&& victimCoordinate.isAdjacent(actorCoordinate) && (actorTeam != victimTeam)
					&& (game.getFieldModel().getPlayerState(pVictim).hasTacklezones()));
		}
		return result;
	}

}
