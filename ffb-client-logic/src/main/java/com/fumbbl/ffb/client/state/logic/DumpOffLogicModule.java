package com.fumbbl.ffb.client.state.logic;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PassingDistance;
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

public class DumpOffLogicModule extends MoveLogicModule {

	public DumpOffLogicModule(FantasyFootballClient client) {
		super(client);
	}

	@Override
	public void postInit() {
		Game game = client.getGame();
		game.setPassCoordinate(null);
	}

	@Override
	public Set<ClientAction> availableActions() {
		return null;
	}

	@Override
	protected void performAvailableAction(Player<?> player, ClientAction action) {

	}

	@Override
	public InteractionResult fieldInteraction(FieldCoordinate coordinate) {
		Game game = client.getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		if (testCoordinateInRange(coordinate)) {
			game.setPassCoordinate(coordinate);
			game.getFieldModel().setRangeRuler(null);
			client.getCommunication().sendPass(actingPlayer.getPlayerId(), coordinate);
			return new InteractionResult(InteractionResult.Kind.PERFORM);
		}
		return new InteractionResult(InteractionResult.Kind.IGNORE);
	}

	private boolean testCoordinateInRange(FieldCoordinate coordinate) {
		boolean validInRange;
		Game game = client.getGame();
		FieldCoordinate throwerCoordinate = game.getFieldModel().getPlayerCoordinate(game.getThrower());
		PassMechanic mechanic = (PassMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.PASS.name());
		PassingDistance passingDistance = mechanic.findPassingDistance(game, throwerCoordinate, coordinate, false);
		validInRange = (PassingDistance.QUICK_PASS == passingDistance);
		return validInRange;
	}

	@Override
	public InteractionResult fieldPeek(FieldCoordinate coordinate) {
		Game game = client.getGame();
		if (testCoordinateInRange(coordinate) && (game.getPassCoordinate() == null)) {
			RangeRuler rangeRuler = UtilRangeRuler.createRangeRuler(game, game.getThrower(), coordinate, false);
			game.getFieldModel().setRangeRuler(rangeRuler);
			return new InteractionResult(InteractionResult.Kind.PERFORM);
		} else {
			return new InteractionResult(InteractionResult.Kind.IGNORE);
		}
	}

	@Override
	public InteractionResult playerPeek(Player<?> player) {
		client.getClientData().setSelectedPlayer(player);
		return new InteractionResult(InteractionResult.Kind.IGNORE);
	}
}