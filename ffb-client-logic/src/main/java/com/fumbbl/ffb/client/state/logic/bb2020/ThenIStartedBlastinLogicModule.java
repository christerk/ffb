package com.fumbbl.ffb.client.state.logic.bb2020;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.MoveSquare;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.net.ClientCommunication;
import com.fumbbl.ffb.client.state.logic.ClientAction;
import com.fumbbl.ffb.client.state.logic.LogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

public class ThenIStartedBlastinLogicModule extends LogicModule {

	public ThenIStartedBlastinLogicModule(FantasyFootballClient pClient) {
		super(pClient);
	}

	@Override
	public void postInit() {
		super.postInit();
		Game game = client.getGame();
		FieldModel fieldModel = game.getFieldModel();
		Player<?> player = game.playingTeamHasActingPLayer() ? game.getActingPlayer().getPlayer() : game.getDefender();
		MoveSquare[] squares = Arrays.stream(fieldModel.findAdjacentCoordinates(fieldModel.getPlayerCoordinate(player), FieldCoordinateBounds.FIELD,
			3, false)).map(fieldCoordinate -> new MoveSquare(fieldCoordinate, 0, 0)).toArray(MoveSquare[]::new);
		fieldModel.add(squares);
	}

	@Override
	public void teardown() {
		FieldModel fieldModel = client.getGame().getFieldModel();
		fieldModel.clearMoveSquares();
	}

	@Override
	public InteractionResult playerInteraction(Player<?> player) {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (player == actingPlayer.getPlayer()) {
			if (game.playingTeamHasActingPLayer()) {
				return new InteractionResult(InteractionResult.Kind.SHOW_ACTIONS);
			}
		} else {
			if (isValidTarget(player, game)) {
				client.getCommunication().sendTargetSelected(player.getId());
				return new InteractionResult(InteractionResult.Kind.HANDLED);
			}
		}
		return new InteractionResult(InteractionResult.Kind.IGNORE);
	}

	public InteractionResult playerPeek(Player<?> player) {
		Game game = client.getGame();
		client.getClientData().setSelectedPlayer(player);
		if (isValidTarget(player, game)) {
			return new InteractionResult(InteractionResult.Kind.PERFORM);
		} else {
			return new InteractionResult(InteractionResult.Kind.INVALID);
		}
	}

	private boolean isValidTarget(Player<?> player, Game game) {
		FieldCoordinate sourceCoordinate;
		if (game.playingTeamHasActingPLayer()) {
			ActingPlayer actingPlayer = game.getActingPlayer();
			sourceCoordinate = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
		} else  {
			sourceCoordinate = game.getFieldModel().getPlayerCoordinate(game.getDefender());
		}
		FieldCoordinate targetCoordinate = game.getFieldModel().getPlayerCoordinate(player);
		int distance = targetCoordinate.distanceInSteps(sourceCoordinate);

		PlayerState playerState = game.getFieldModel().getPlayerState(player);


		return distance <= 3 && playerState.getBase() == PlayerState.STANDING
			&& (player.getTeam() != game.getActingTeam() || !game.playingTeamHasActingPLayer());
	}

	@Override
	public Set<ClientAction> availableActions() {
		return Collections.singleton(ClientAction.END_MOVE);
	}

	@Override
	protected void performAvailableAction(Player<?> player, ClientAction action) {
		ClientCommunication communication = client.getCommunication();
		switch (action) {
			case END_MOVE:
				if (isEndPlayerActionAvailable()) {
					communication.sendEndTurn(client.getGame().getTurnMode());
				}
				break;

			default:
				break;
		}
	}

	private boolean isEndPlayerActionAvailable() {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		return !actingPlayer.hasActed();
	}

}