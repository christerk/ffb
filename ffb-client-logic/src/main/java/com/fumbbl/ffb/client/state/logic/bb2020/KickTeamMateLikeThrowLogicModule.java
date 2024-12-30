package com.fumbbl.ffb.client.state.logic.bb2020;

import com.fumbbl.ffb.ClientStateId;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.state.logic.MoveLogicModule;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.TtmMechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.Arrays;
import java.util.Objects;

public class KickTeamMateLikeThrowLogicModule extends MoveLogicModule {

	public KickTeamMateLikeThrowLogicModule(FantasyFootballClient client) {
		super(client);
	}

	@Override
	public ClientStateId getId() {
		return ClientStateId.KICK_TEAM_MATE_THROW;
	}

	@Override
	protected boolean showGridForKTM(Game game, ActingPlayer actingPlayer) {
		return ((PlayerAction.KICK_TEAM_MATE_MOVE == actingPlayer.getPlayerAction())
			&& UtilPlayer.canKickTeamMate(game, actingPlayer.getPlayer(), false));
	}

	@Override
	public InteractionResult playerInteraction(Player<?> player) {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (player == actingPlayer.getPlayer()) {
			return super.playerInteraction(player);
		} else {
			if ((game.getDefender() == null) && canBeKicked(player)) {
				client.getCommunication().sendThrowTeamMate(actingPlayer.getPlayerId(), player.getId(), true);
				return new InteractionResult(InteractionResult.Kind.PERFORM);
			}
			if (game.getDefender() != null) {
				game.getFieldModel().setRangeRuler(null);
				client.getCommunication().sendThrowTeamMate(actingPlayer.getPlayerId(),
					game.getFieldModel().getPlayerCoordinate(player), true);
				return new InteractionResult(InteractionResult.Kind.HANDLED);
			}
		}
		return new InteractionResult(InteractionResult.Kind.IGNORE);
	}

	@Override
	public InteractionResult fieldInteraction(FieldCoordinate pCoordinate) {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (actingPlayer.getPlayerAction() == PlayerAction.KICK_TEAM_MATE_MOVE) {
			return new InteractionResult(InteractionResult.Kind.SUPER);
		} else {
			game.getFieldModel().setRangeRuler(null);
			client.getCommunication().sendThrowTeamMate(actingPlayer.getPlayerId(), pCoordinate, true);
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
			if (canBeKicked(pPlayer)) {
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

	private boolean canBeKicked(Player<?> pPlayer) {
		Game game = client.getGame();
		TtmMechanic mechanic = (TtmMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.TTM.name());
		ActingPlayer actingPlayer = game.getActingPlayer();
		FieldCoordinate throwerCoordinate = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
		FieldCoordinate catcherCoordinate = game.getFieldModel().getPlayerCoordinate(pPlayer);
		// added a check, so you could not throw the opponents players, maybe this should
		// be in the server-check?
		return actingPlayer.getPlayer().hasSkillProperty(NamedProperties.canKickTeamMates)
			&& mechanic.canBeKicked(game, pPlayer)
			&& catcherCoordinate.isAdjacent(throwerCoordinate);
	}

	public Player<?>[] findKickablePlayers(Game game, Player<?> pThrower) {
		if (game.getDefender() != null) {
			return null;
		}
		TtmMechanic mechanic = (TtmMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.TTM.name());

		FieldModel fieldModel = game.getFieldModel();
		FieldCoordinate throwerCoordinate = fieldModel.getPlayerCoordinate(pThrower);

		return Arrays.stream(fieldModel.findAdjacentCoordinates(throwerCoordinate, FieldCoordinateBounds.FIELD,
				1, false))
			.map(fieldModel::getPlayer)
			.filter(Objects::nonNull)
			.filter(player -> mechanic.canBeKicked(game, player)).toArray(Player[]::new);
	}


}
