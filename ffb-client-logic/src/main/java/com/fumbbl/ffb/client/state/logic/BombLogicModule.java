package com.fumbbl.ffb.client.state.logic;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PassingDistance;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.RangeRuler;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.state.logic.interaction.InteractionResult;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.PassMechanic;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.util.UtilRangeRuler;

import java.util.Set;

public class BombLogicModule extends LogicModule {

	private boolean showRangeRuler;
	public BombLogicModule(FantasyFootballClient client) {
		super(client);
		showRangeRuler = true;
	}

	@Override
	public Set<ClientAction> availableActions() {
		return null;
	}

	@Override
	protected void performAvailableAction(Player<?> player, ClientAction action) {

	}

	@Override
	public InteractionResult playerInteraction(Player<?> player) {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (player == actingPlayer.getPlayer()) {
			return new InteractionResult(InteractionResult.Kind.SHOW_ACTIONS);
		} else {
			FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(player);
			return new InteractionResult(InteractionResult.Kind.PERFORM, playerCoordinate);
		}
	}

	@Override
	public InteractionResult fieldInteraction(FieldCoordinate coordinate) {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		FieldCoordinate throwerCoordinate = game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());
		PassMechanic mechanic = (PassMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.PASS.name());
		PassingDistance passingDistance = mechanic.findPassingDistance(game, throwerCoordinate, coordinate, false);
		if ((PlayerAction.HAIL_MARY_BOMB == actingPlayer.getPlayerAction()) || (passingDistance != null)) {
			game.setPassCoordinate(coordinate);
			client.getCommunication().sendPass(actingPlayer.getPlayerId(), game.getPassCoordinate());
			game.getFieldModel().setRangeRuler(null);
			return new InteractionResult(InteractionResult.Kind.PERFORM);
		}
		return new InteractionResult(InteractionResult.Kind.IGNORE);
	}

	@Override
	public InteractionResult playerPeek(Player<?> player) {
		client.getClientData().setSelectedPlayer(player);
		Game game = client.getGame();
		return new InteractionResult(InteractionResult.Kind.PERFORM, game.getFieldModel().getPlayerCoordinate(player));
	}

	@Override
	public InteractionResult fieldPeek(FieldCoordinate coordinate) {

		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (PlayerAction.HAIL_MARY_BOMB == actingPlayer.getPlayerAction()) {
			game.getFieldModel().setRangeRuler(null);
			return new InteractionResult(InteractionResult.Kind.PERFORM);
		} else {
			if (showRangeRuler()) {
				RangeRuler rangeRuler = UtilRangeRuler.createRangeRuler(game, actingPlayer.getPlayer(), coordinate, false);
				game.getFieldModel().setRangeRuler(rangeRuler);
				return new InteractionResult(InteractionResult.Kind.DRAW, rangeRuler);
			} else {
				return new InteractionResult(InteractionResult.Kind.IGNORE);
			}
		}
	}

	public boolean showRangeRuler() {
		return showRangeRuler && (client.getGame().getPassCoordinate() == null);
	}
}
