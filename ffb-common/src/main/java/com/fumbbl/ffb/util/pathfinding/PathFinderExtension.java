package com.fumbbl.ffb.util.pathfinding;

import com.fumbbl.ffb.FantasyFootballException;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.model.Game;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PathFinderExtension {

	public boolean hasProneOrStunnedPlayerOnPath(Game game, FieldCoordinate from, FieldCoordinate to) {
		return hasProneOrStunnedPlayers(game, findPossiblePathSquares(from, to).stream());
	}

	public boolean hasProneOrStunnedPlayers(Game game, Stream<FieldCoordinate> coordinates) {
		return coordinates
			.map(game.getFieldModel()::getPlayer)
			.filter(Objects::nonNull)
			.map(game.getFieldModel()::getPlayerState)
			.filter(Objects::nonNull)
			.anyMatch(state -> state.isStunned() || state.isProneOrStunned());
	}

	public Set<FieldCoordinate> findPossiblePathSquares(FieldCoordinate from, FieldCoordinate to) {
		return combineVariances(dimensionVariance(to.getX() - from.getX()), dimensionVariance(to.getY() - from.getY()), from).stream()
			.filter(FieldCoordinateBounds.FIELD::isInBounds).collect(Collectors.toSet());
	}

	private Set<FieldCoordinate> combineVariances(int[] xVariances, int[] yVariances, FieldCoordinate startCoordinate) {
		Set<FieldCoordinate> coordinates = new HashSet<>();
		for (int xVariance : xVariances) {
			for (int yVariance : yVariances) {
				coordinates.add(new FieldCoordinate(startCoordinate.getX() + xVariance, startCoordinate.getY() + yVariance));
			}
		}
		return coordinates;
	}

	private int[] dimensionVariance(int diff) {
		if (Math.abs(diff) == 2) {
			return new int[]{ diff/2 };
		} else if (Math.abs(diff) == 1) {
			return new int[] {diff, 0};
		} else if (diff == 0) {
			return new int[] {0};
		} else {
			throw new FantasyFootballException("Received illegal dimension difference of " + diff);
		}
	}
}
