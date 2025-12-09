package com.fumbbl.ffb.client.state.logic.bb2025;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.state.logic.MoveLogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.model.*;
import com.fumbbl.ffb.util.UtilPlayer;

public class GazeLogicModule extends MoveLogicModule {
	public GazeLogicModule(FantasyFootballClient pClient) {
		super(pClient);
	}

	@Override
	public ClientStateId getId() {
		return ClientStateId.GAZE;
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
				return InteractionResult.handled();
			}
		}
		return InteractionResult.ignore();
	}

	@Override
	public InteractionResult playerPeek(Player<?> pPlayer) {
		if (canBeGazed(pPlayer)) {
			return InteractionResult.perform();
		} else {
			return InteractionResult.ignore();
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
