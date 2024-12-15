package com.fumbbl.ffb.client.state.logic;

import com.fumbbl.ffb.*;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.net.ClientCommunication;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.OnTheBallMechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Kalimar
 */
public class PassBlockLogicModule extends MoveLogicModule {

	public PassBlockLogicModule(FantasyFootballClient pClient) {
		super(pClient);
	}

	public ClientStateId getId() {
		return ClientStateId.PASS_BLOCK;
	}

	@Override
	public InteractionResult playerInteraction(Player<?> pPlayer) {
		Game game = client.getGame();
		PlayerState playerState = game.getFieldModel().getPlayerState(pPlayer);
		if (game.getTeamHome().hasPlayer(pPlayer) && playerState.isActive()) {
			return new InteractionResult(InteractionResult.Kind.SHOW_ACTIONS);
		}
		return new InteractionResult(InteractionResult.Kind.IGNORE);
	}

	@Override
	public InteractionResult fieldInteraction(FieldCoordinate pCoordinate) {
		Game game = client.getGame();
		MoveSquare moveSquare = game.getFieldModel().getMoveSquare(pCoordinate);
		if (moveSquare != null) {
			if (movePlayer(pCoordinate)) {
				return new InteractionResult(InteractionResult.Kind.HANDLED);
			}
		}
		return new InteractionResult(InteractionResult.Kind.IGNORE);
	}

	@Override
	public Set<ClientAction> availableActions() {
		return new HashSet<ClientAction>() {{
			add(ClientAction.JUMP);
			add(ClientAction.MOVE);
			add(ClientAction.END_MOVE);
			add(ClientAction.BOUNDING_LEAP);
		}};
	}

	@Override
	protected void performAvailableAction(Player<?> player, ClientAction action) {
		if (player != null) {
			Game game = client.getGame();
			ActingPlayer actingPlayer = game.getActingPlayer();
			ClientCommunication communication = client.getCommunication();
			switch (action) {
				case JUMP:
					if ((actingPlayer.getPlayer() != null)
						&& isJumpAvailableAsNextMove(game, actingPlayer, false)) {
						communication.sendActingPlayer(player, actingPlayer.getPlayerAction(), !actingPlayer.isJumping());
					}
					break;
				case MOVE:
					communication.sendActingPlayer(player, PlayerAction.MOVE, false);
					break;
				case END_MOVE:
					communication.sendActingPlayer(null, null, false);
					break;
				case BOUNDING_LEAP:
					isBoundingLeapAvailable(game, actingPlayer).ifPresent(skill ->
						communication.sendUseSkill(skill, true, actingPlayer.getPlayerId()));
				default:
					break;
			}
		}
	}

	public boolean isTurnEnding(){
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		OnTheBallMechanic mechanic = (OnTheBallMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.ON_THE_BALL.name());
		return mechanic.hasReachedValidPosition(game, actingPlayer.getPlayer());
	}

	@Override
	public void endTurn() {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (actingPlayer.getPlayer() != null) {
			if (isTurnEnding()) {
				client.getCommunication().sendEndTurn(client.getGame().getTurnMode());
				client.getClientData().setEndTurnButtonHidden(true);
			}
		}
	}
}
