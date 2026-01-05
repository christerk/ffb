package com.fumbbl.ffb.mechanics.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.util.UtilPlayer;
import com.fumbbl.ffb.util.pathfinding.PathFinderExtension;

import java.util.Arrays;

@RulesCollection(RulesCollection.Rules.BB2025)
public class JumpMechanic extends com.fumbbl.ffb.mechanics.JumpMechanic {

	private final PathFinderExtension extension = new PathFinderExtension();

	@Override
	public boolean isAvailableAsNextMove(Game game, ActingPlayer actingPlayer, boolean jumping) {
		return canStillJump(game, actingPlayer) && UtilPlayer.isNextMovePossible(game, jumping);
	}

	@Override
	public boolean canStillJump(Game game, ActingPlayer actingPlayer) {
		return (actingPlayer.getPlayer().hasSkillProperty(NamedProperties.canLeap)
			|| (hasProneOrStunnedPlayersAdjacent(game, game.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer())))) && !actingPlayer.getPlayer().hasSkillProperty(NamedProperties.movesRandomly);
	}

	@Override
	public boolean canJump(Game game, Player<?> player, FieldCoordinate coordinate) {
		return (player.hasSkillProperty(NamedProperties.canLeap) || hasProneOrStunnedPlayersAdjacent(game, coordinate)) && !player.hasSkillProperty(NamedProperties.movesRandomly);
	}

	@Override
	public boolean isValidJump(Game game, Player<?> player, FieldCoordinate from, FieldCoordinate to) {
		return !to.equals(from) && to.distanceInSteps(from) == 2 && ( player.hasSkillProperty(NamedProperties.canLeap) || extension.hasProneOrStunnedPlayerOnPath(game, from, to));
	}

	private boolean hasProneOrStunnedPlayersAdjacent(Game game, FieldCoordinate coordinate) {
		FieldCoordinate[] coordinates = game.getFieldModel().findAdjacentCoordinates(coordinate, FieldCoordinateBounds.FIELD, 1, false);
		return extension.hasProneOrStunnedPlayers(game, Arrays.stream(coordinates));
	}
}
