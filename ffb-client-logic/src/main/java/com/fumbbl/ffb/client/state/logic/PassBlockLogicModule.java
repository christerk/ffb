package com.fumbbl.ffb.client.state.logic;

import com.fumbbl.ffb.*;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.net.ClientCommunication;
import com.fumbbl.ffb.client.state.logic.interaction.ActionContext;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.OnTheBallMechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.skill.Skill;

import java.util.HashSet;
import java.util.Optional;
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
	public InteractionResult playerInteraction(Player<?> player) {
		Game game = client.getGame();
		PlayerState playerState = game.getFieldModel().getPlayerState(player);
		if (game.getTeamHome().hasPlayer(player) && playerState.isActive()) {
			return InteractionResult.selectAction(actionContext(player));
		}
		return InteractionResult.ignore();
	}

	@Override
	public InteractionResult fieldInteraction(FieldCoordinate pCoordinate) {
		Game game = client.getGame();
		MoveSquare moveSquare = game.getFieldModel().getMoveSquare(pCoordinate);
		if (moveSquare != null) {
			if (movePlayer(pCoordinate)) {
				return InteractionResult.handled();
			}
		}
		return InteractionResult.ignore();
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

	@Override
	protected ActionContext actionContext(ActingPlayer actingPlayer) {
		throw new UnsupportedOperationException("actionContext for acting player is not supported in pass block context");
	}
	
	protected ActionContext actionContext(Player<?> player) {
		ActionContext actionContext = new ActionContext();
		Game game = client.getGame();
		PlayerState playerState = game.getFieldModel().getPlayerState(player);
		ActingPlayer actingPlayer = game.getActingPlayer();
		if ((actingPlayer.getPlayer() == null) && (playerState != null) && playerState.isAbleToMove()) {
			actionContext.add(ClientAction.MOVE);
		}
		if ((actingPlayer.getPlayer() != null)
			&& isJumpAvailableAsNextMove(game, actingPlayer, false)) {
			actionContext.add(ClientAction.JUMP);
			if (actingPlayer.isJumping()) {
				actionContext.add(Influences.IS_JUMPING);
			} else {

				Optional<Skill> boundingLeap = isBoundingLeapAvailable(game, actingPlayer);
				if (boundingLeap.isPresent()) {
					actionContext.add(ClientAction.BOUNDING_LEAP);
				}
			}
		}
		if (game.getActingPlayer().getPlayer() == player) {
			if (!actingPlayer.hasActed()) {
				actionContext.add(ClientAction.END_MOVE);
			} else {
				OnTheBallMechanic mechanic = (OnTheBallMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.ON_THE_BALL.name());
				if (mechanic.hasReachedValidPosition(game, actingPlayer.getPlayer())) {
					actionContext.add(ClientAction.END_MOVE);
					actionContext.add(Influences.HAS_ACTED);
				}
			}
		}
		return actionContext;
	}
}
