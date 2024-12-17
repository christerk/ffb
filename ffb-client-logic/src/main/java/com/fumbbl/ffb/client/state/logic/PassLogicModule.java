package com.fumbbl.ffb.client.state.logic;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.net.ClientCommunication;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.Set;

/**
 * @author Kalimar
 */
public class PassLogicModule extends MoveLogicModule {

	public PassLogicModule(FantasyFootballClient pClient) {
		super(pClient);
	}

	public ClientStateId getId() {
		return ClientStateId.PASS;
	}
	
	@Override
	public InteractionResult playerInteraction(Player<?> player) {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (player == actingPlayer.getPlayer()) {
			return super.playerInteraction(player);
		} else {
			if (!actingPlayer.hasPassed() && (PlayerAction.HAIL_MARY_PASS == actingPlayer.getPlayerAction()
				|| (UtilPlayer.hasBall(game, actingPlayer.getPlayer())
				&& ((PlayerAction.PASS == actingPlayer.getPlayerAction()) || canPlayerGetPass(player))))) {
				game.setPassCoordinate(game.getFieldModel().getPlayerCoordinate(player));
				client.getCommunication().sendPass(actingPlayer.getPlayerId(), game.getPassCoordinate());
				game.getFieldModel().setRangeRuler(null);
				return new InteractionResult(InteractionResult.Kind.HANDLED);
			}
		}
		return new InteractionResult(InteractionResult.Kind.IGNORE);
	}

	@Override
	public InteractionResult fieldInteraction(FieldCoordinate pCoordinate) {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (actingPlayer.getPlayerAction() == PlayerAction.PASS_MOVE) {
			return new InteractionResult(InteractionResult.Kind.SUPER);
		} else {
			if ((PlayerAction.HAIL_MARY_PASS == actingPlayer.getPlayerAction())
				|| UtilPlayer.hasBall(game, actingPlayer.getPlayer())) {
				game.setPassCoordinate(pCoordinate);
				client.getCommunication().sendPass(actingPlayer.getPlayerId(), game.getPassCoordinate());
				game.getFieldModel().setRangeRuler(null);
				return new InteractionResult(InteractionResult.Kind.HANDLED);
			}
		}
		return new InteractionResult(InteractionResult.Kind.IGNORE);
	}

	@Override
	public InteractionResult playerPeek(Player<?> pPlayer) {
		client.getClientData().setSelectedPlayer(pPlayer);
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if ((PlayerAction.HAIL_MARY_PASS != actingPlayer.getPlayerAction())
			&& UtilPlayer.hasBall(game, actingPlayer.getPlayer())) {
			FieldCoordinate catcherCoordinate = game.getFieldModel().getPlayerCoordinate(pPlayer);
			if ((PlayerAction.PASS == actingPlayer.getPlayerAction()) || canPlayerGetPass(pPlayer)) {
				return new InteractionResult(InteractionResult.Kind.DRAW, catcherCoordinate);
			}
		} else {
			game.getFieldModel().setRangeRuler(null);
			if (actionIsHmp()) {
				return new InteractionResult(InteractionResult.Kind.PERFORM);
			} else {
				return new InteractionResult(InteractionResult.Kind.RESET);
			}
		}
		return new InteractionResult(InteractionResult.Kind.IGNORE);
	}

	@Override
	public InteractionResult fieldPeek(FieldCoordinate pCoordinate) {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (actionIsHmp()) {
			game.getFieldModel().setRangeRuler(null);
			return new InteractionResult(InteractionResult.Kind.PERFORM);
		} else if (actingPlayer.getPlayerAction() == PlayerAction.PASS_MOVE) {
			game.getFieldModel().setRangeRuler(null);
			return new InteractionResult(InteractionResult.Kind.SUPER);
		} else {
			return new InteractionResult(InteractionResult.Kind.DRAW, pCoordinate);
		}
	}

	public boolean canPlayerGetPass(Player<?> pCatcher) {
		boolean canGetPass = false;
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if ((pCatcher != null) && (actingPlayer.getPlayer() != null)) {
			PlayerState catcherState = game.getFieldModel().getPlayerState(pCatcher);
			canGetPass = ((catcherState != null)
				&& catcherState.hasTacklezones() && (game.getTeamHome() == pCatcher.getTeam())
				&& (!actingPlayer.isSufferingAnimosity() || actingPlayer.getRace().equals(pCatcher.getRace())));
		}
		return canGetPass;
	}

	@Override
	public Set<ClientAction> availableActions() {
		Set<ClientAction> actions = super.availableActions();
		actions.add(ClientAction.HAIL_MARY_PASS);
		return actions;
	}

	@Override
	protected void performAvailableAction(Player<?> player, ClientAction action) {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		ClientCommunication communication = client.getCommunication();
		switch (action) {
			case HAIL_MARY_PASS:
				if (hmpAvailable()) {
					if (actionIsHmp()) {
						communication.sendActingPlayer(player, PlayerAction.PASS, actingPlayer.isJumping());
					} else {
						communication.sendActingPlayer(player, PlayerAction.HAIL_MARY_PASS, actingPlayer.isJumping());
						game.getFieldModel().setRangeRuler(null);
					}
				}
				break;
			default:
				super.performAvailableAction(player, action);
				break;
		}
	}

	public boolean actionIsHmp() {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		return PlayerAction.HAIL_MARY_PASS == actingPlayer.getPlayerAction();
	}

	public boolean hmpAvailable() {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		return actingPlayer.getPlayer().hasSkillProperty(NamedProperties.canPassToAnySquare);
	}
}
