package com.fumbbl.ffb.client.state.logic;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.TtmMechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

/**
 *
 * @author Kalimar
 */
public class ThrowTeamMateLogicModule extends MoveLogicModule {

	public ThrowTeamMateLogicModule(FantasyFootballClient pClient) {
		super(pClient);
	}

	public ClientStateId getId() {
		return ClientStateId.THROW_TEAM_MATE;
	}

	@Override
	public InteractionResult playerInteraction(Player<?> player) {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (player == actingPlayer.getPlayer()) {
			return super.playerInteraction(player);
		} else {
			if ((game.getDefender() == null) && canBeThrown(player)) {
				client.getCommunication().sendThrowTeamMate(actingPlayer.getPlayerId(), player.getId());
				return new InteractionResult(InteractionResult.Kind.PERFORM);
			}
			if (game.getDefender() != null) {
				game.getFieldModel().setRangeRuler(null);
				client.getCommunication().sendThrowTeamMate(actingPlayer.getPlayerId(),
						game.getFieldModel().getPlayerCoordinate(player));
				return new InteractionResult(InteractionResult.Kind.HANDLED);

			}
		}
		return new InteractionResult(InteractionResult.Kind.IGNORE);
	}

	@Override
	public InteractionResult fieldInteraction(FieldCoordinate pCoordinate) {
		Game game = client.getGame();
		ActingPlayer actingPlayer = client.getGame().getActingPlayer();
		if (actingPlayer.getPlayerAction() == PlayerAction.THROW_TEAM_MATE_MOVE) {
			return InteractionResult.delegate(super.getId());
		} else {
			game.getFieldModel().setRangeRuler(null);
			client.getCommunication().sendThrowTeamMate(actingPlayer.getPlayerId(), pCoordinate);
			return new InteractionResult(InteractionResult.Kind.HANDLED);
		}
	}

	@Override
	public InteractionResult fieldPeek(FieldCoordinate pCoordinate) {
		Game game = client.getGame();
		if ((game.getDefender() != null) && (game.getPassCoordinate() == null)) {
			return new InteractionResult(InteractionResult.Kind.DRAW);
		}
		return new InteractionResult(InteractionResult.Kind.RESET);
	}

	@Override
	public InteractionResult playerPeek(Player<?> pPlayer) {
		Game game = client.getGame();
		client.getClientData().setSelectedPlayer(pPlayer);
		if ((game.getDefender() == null) && (game.getPassCoordinate() == null)) {
			if (canBeThrown(pPlayer)) {
				return new InteractionResult(InteractionResult.Kind.PERFORM);
			} else {
				return new InteractionResult(InteractionResult.Kind.RESET);
			}
		}
		if ((game.getDefender() != null) && (game.getPassCoordinate() == null)) {
			return new InteractionResult(InteractionResult.Kind.DRAW);
		}
		return new InteractionResult(InteractionResult.Kind.IGNORE);
	}
	
	private boolean canBeThrown(Player<?> pPlayer) {
		Game game = client.getGame();
		TtmMechanic mechanic = (TtmMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.TTM.name());
		ActingPlayer actingPlayer = game.getActingPlayer();
		FieldCoordinate throwerCoordinate = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
		FieldCoordinate catcherCoordinate = game.getFieldModel().getPlayerCoordinate(pPlayer);
		// added a check so you could not throw the opponents players, maybe this should
		// be in the server-check?
		return mechanic.canThrow(actingPlayer.getPlayer())
			&& mechanic.canBeThrown(game, pPlayer)
			&& catcherCoordinate.isAdjacent(throwerCoordinate);
	}
}
