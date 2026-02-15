package com.fumbbl.ffb.util.pathfinding;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.stadium.OnPitchEnhancement;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.Collectors;

public class PathFinderWithMultiJump {


	private final PathFindContext theoreticalRangeContext = new PathFindContext.Builder().allowJump().build();

	private final PathFinderExtension extension = new PathFinderExtension();

	public final static PathFinderWithMultiJump INSTANCE = new PathFinderWithMultiJump();

	/**
	 * Gets the shortest path from the player in the start square to the end square.
	 * The start square must contain a player. The path will not leave or pass
	 * through a tackle zone, but it may end in one. The path will also avoid going
	 * through or landing on a player.
	 *
	 * @param start      Starting square of the player.
	 * @param pEndCoords List of target squares.
	 * @return Shortest path to target squares.
	 */
	private FieldCoordinate[] getShortestPath(Game game, FieldCoordinate start, Set<FieldCoordinate> pEndCoords,
		int maxDistance, PathFindContext context, Player<?> player) {

		// Sanity check
		if (game == null || start == null || pEndCoords == null || player == null || !isOnField(game, start) ||
			!isOnField(game, pEndCoords)) {
			return null;
		}

		boolean canJumpOverStandingPlayer = player.hasSkillProperty(NamedProperties.canLeap);

		Team movingTeam = player.getTeam();

		FieldModel fieldModel = game.getFieldModel();
		FieldCoordinateBounds endzoneBounds;
		if (movingTeam == game.getTeamHome()) {
			endzoneBounds = FieldCoordinateBounds.ENDZONE_AWAY;
		} else {
			endzoneBounds = FieldCoordinateBounds.ENDZONE_HOME;
		}

		// Keeps a list of potential candidates
		PriorityQueue<PathFindNode> openSet = new PriorityQueue<>();

		PathFindData data = new PathFindData();

		// Nodes which have already been processed
		Set<FieldCoordinate> closedSet = new HashSet<>();
		PathFindNode neighbour;

		// Initialise the open set with the start squares
		PathFindNode current = new PathFindNode(PathFindState.NORMAL, start, 0, false, pEndCoords, null);
		data.setNode(start, current);
		openSet.add(current);

		FieldCoordinate ballCoord = fieldModel.getBallCoordinate();

		if (context.isBlockBall()) {
			// Handle the square with the ball by marking it as a TZ.
			// This allows the player to move into the ball, but not through it.
			if (isOnField(game, ballCoord) && context.isBlockTacklezones() && fieldModel.isBallInPlay()) {
				data.setNode(ballCoord, new PathFindNode(PathFindState.NORMAL, ballCoord, 1000, true, pEndCoords, null));
			}
		}

		if (context.isBlockTrapdoors()) {
			// Treat trapdoor fields as tackle zones to avoid paths crossing them
			fieldModel.getOnPitchEnhancements().stream().map(OnPitchEnhancement::getCoordinate)
				.forEach(
					coord -> data.setNode(coord, new PathFindNode(PathFindState.NORMAL, coord, 1000, true, pEndCoords, null)));
		}

		boolean hasBall = start.equals(ballCoord);

		// Block off squares with players in them
		FieldCoordinate[] players = fieldModel.getPlayerCoordinates();
		for (FieldCoordinate pCoord : players) {
			if (!isOnField(game, pCoord)) {
				continue;
			}
			PathFindNode blockedNode = data.blockNode(pCoord);
			closedSet.add(blockedNode.getCoord());

			if (context.isBlockTacklezones()) {
				// And mark tackle zones as well for each opponent
				Player<?> p = fieldModel.getPlayer(pCoord);
				if (p.getTeam() != movingTeam) {
					// Skip if the player does not have a tackle zone
					if (!fieldModel.getPlayerState(p).hasTacklezones()) {
						continue;
					}

					// Don't allow an initial dodge
					if (pCoord.isAdjacent(start)) {
						return null;
					}

					FieldCoordinate[] tz = fieldModel.findAdjacentCoordinates(pCoord, FieldCoordinateBounds.FIELD, 1, false);
					for (FieldCoordinate tzCoord : tz) {
						if (data.isProcessed(PathFindState.NORMAL, tzCoord.getX(), tzCoord.getY())) {
							continue;
						}

						// mark the node as a tacklezone
						data.setNode(tzCoord, new PathFindNode(PathFindState.NORMAL, tzCoord, 1000, true, pEndCoords, null));
					}
				}
			}
		}

		while (!openSet.isEmpty()) {
			// Get the node with the shortest distance that hasn't been
			// processed
			current = openSet.poll();

			// Check if we're beyond normal movement range
			if (current.getDistance() > maxDistance) {
				return null;
			}

			// Are we at the target?
			if (pEndCoords.contains(current.getCoord())) {
				return reconstructPath(current);
			}

			// Mark as processed
			closedSet.add(current.getCoord());

			boolean isInEndzone = endzoneBounds.isInBounds(current.getCoord());

			// For each neighbour of the square we're processing...
			int searchDistance = context.isAllowJump() && maxDistance - current.getDistance() > 1 ? 2 : 1;
			FieldCoordinate[] neighbours = fieldModel.findAdjacentCoordinates(current.getCoord(), FieldCoordinateBounds.FIELD,
				searchDistance, false);
			for (FieldCoordinate neighbourCoord : neighbours) {

				int distance = current.getCoord().distanceInSteps(neighbourCoord);

				// Don't allow a jump if the context explicitly disallows it, if the path
				// already has jumped before or if the player can't jump
				boolean downedPlayerOnPath = extension.hasProneOrStunnedPlayerOnPath(game, current.getCoord(), neighbourCoord);
				Set<FieldCoordinate> pathSquares = extension.findPossiblePathSquares(current.getCoord(), neighbourCoord);
				pathSquares.removeAll(closedSet);
				if (distance > 1 &&
					(maxDistance - current.getDistance() - distance < 0 // square can't be reached
						|| !context.isAllowJump()  // we look for a path without any jumps
						|| (!canJumpOverStandingPlayer && !downedPlayerOnPath)
						// player without leap/pogo can only jump over downed players
						|| (!pathSquares.isEmpty()) // there is an empty square in between, so we should not jump over it
					)) {
					continue;
				}

				// Get the state of the next coordinate.
				PathFindState neighbourState = distance == 1 ? current.getState() : PathFindState.HAS_JUMPED;

				// Get the neighbour node from the cache if it exists
				neighbour = data.getNeighbour(neighbourState, neighbourCoord);

				// Did we already process the square?
				if (neighbour != null
					&& (closedSet.contains(neighbour.getCoord()) || (neighbour.isTz() && !pEndCoords.contains(neighbourCoord)))) {
					continue;
				}

				// Don't allow moving out of an endzone if the player has the ball.
				if (!context.isAllowExitEndzoneWithBall() && hasBall && isInEndzone &&
					!endzoneBounds.isInBounds(neighbourCoord)) {
					continue;
				}

				if (neighbour == null) {
					// This square has not been touched at all yet
					neighbour =
						new PathFindNode(neighbourState, neighbourCoord, current.getDistance() + distance, false, pEndCoords,
							current);
					data.setNode(neighbourCoord, neighbour);
					openSet.add(neighbour);
				} else if (current.getDistance() + distance < neighbour.getDistance()) {
					// Found an old path with a longer distance, so update
					openSet.remove(neighbour);
					neighbour.setSource(current, distance);
					openSet.add(neighbour);
				}
			}
		}

		// No path found
		return null;
	}

	// Constructs a path from the PathFindNode structure
	private FieldCoordinate[] reconstructPath(PathFindNode end) {
		LinkedList<FieldCoordinate> list = new LinkedList<>();
		FieldCoordinate[] result = new FieldCoordinate[end.getDistance()];

		while (end.getParent() != null) {
			// The list is reversed, so we add to the head of the linked list.
			list.addFirst(end.getCoord());
			end = end.getParent();
		}

		return list.toArray(result);
	}

	public FieldCoordinate[] getPathToBlitzTarget(Game pGame, Player<?> targetPlayer) {
		FieldModel fieldModel = pGame.getFieldModel();
		FieldCoordinate[] adjacentSquares = fieldModel.findAdjacentCoordinates(
			fieldModel.getPlayerCoordinate(targetPlayer), FieldCoordinateBounds.FIELD, 1, false);

		Set<FieldCoordinate> pEndCoords =
			Arrays.stream(adjacentSquares).filter(s -> fieldModel.getPlayer(s) == null).collect(Collectors.toSet());

		ActingPlayer actingPlayer = pGame.getActingPlayer();

		if (actingPlayer == null || actingPlayer.getPlayer() == null) {
			return null;
		}

		FieldCoordinate start = pGame.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());

		int maxDistance = actingPlayer.getPlayer().getMovementWithModifiers() - actingPlayer.getCurrentMove();

		// increase max distance to include rushes but remove 1 since we will check to reach squares adjacent to the target
		// and need one move for the actual blitz
		maxDistance += targetPlayer.hasSkillProperty(NamedProperties.canMakeAnExtraGfi) ? 2 : 1;

		return getShortestPath(pGame, start, pEndCoords, maxDistance, theoreticalRangeContext, actingPlayer.getPlayer());
	}

	private boolean isOnField(Game pGame, FieldCoordinate pCoordinate) {
		return (pGame.getTurnMode() == TurnMode.KICKOFF_RETURN) ? FieldCoordinateBounds.HALF_HOME.isInBounds(pCoordinate)
			: FieldCoordinateBounds.FIELD.isInBounds(pCoordinate);
	}

	private boolean isOnField(Game pGame, Set<FieldCoordinate> pCoordinates) {
		boolean result = true;
		for (FieldCoordinate coord : pCoordinates) {
			result = result && isOnField(pGame, coord);
		}
		return result;
	}
}
