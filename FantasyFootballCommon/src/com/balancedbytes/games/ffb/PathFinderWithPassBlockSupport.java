package com.balancedbytes.games.ffb;

import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.FieldModel;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.model.property.NamedProperties;
import com.balancedbytes.games.ffb.util.UtilPassing;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Set;

/**
 *
 * @author Christer
 */
public class PathFinderWithPassBlockSupport {

	private static class PathFindContext {
		public boolean blockTacklezones = true;
		public boolean allowLeap = false;
		public boolean allowExitEndzoneWithBall = false;
	}

	private static PathFindState globalState;
	private static PathFindState normalState;
	private static PathFindState leapState;

	private static PathFindContext normalMoveContext;
	private static PathFindContext passBlockContext;

	static {
		passBlockContext = new PathFindContext();
		passBlockContext.blockTacklezones = false;
		passBlockContext.allowLeap = true;
		passBlockContext.allowExitEndzoneWithBall = true;

		normalMoveContext = new PathFindContext();
		normalMoveContext.blockTacklezones = true;
		normalMoveContext.allowLeap = false;
		normalMoveContext.allowExitEndzoneWithBall = false;

		globalState = new PathFindState();
		normalState = new PathFindState();
		leapState = new PathFindState();
	}

	private static class PathFindNode implements Comparable<PathFindNode> {
		// State of the current PathFindNode
		public PathFindState state;

		// Shortest distance found so far to the square
		public int distance;

		// Estimated distance to go
		private int estimate = -1;

		// Does this square have a tz?
		public boolean tz;

		// Coordinate of the square
		public FieldCoordinate coord;

		// Target of the square
		public Set<FieldCoordinate> target;

		// The previous node in the shortest path
		public PathFindNode parent;

		public PathFindNode(PathFindState state, FieldCoordinate coord, int distance, boolean tz,
				Set<FieldCoordinate> target, PathFindNode parent) {
			this.state = state;
			this.coord = coord;
			this.parent = parent;
			this.target = target;
			this.tz = tz;
			this.distance = distance;
			this.estimate = 1000;

			if (target != null) {
				for (FieldCoordinate t : target)
					estimate = Math.min(estimate, coord.distanceInSteps(t));
			}
		}

		public int getWeight() {
			return distance + estimate;
		}

		private int getNonDiagonalWeight() {
			int bestWeight = 10000;
			for (FieldCoordinate t : target) {
				int weight = Math.abs(coord.getX() - t.getX()) + Math.abs(coord.getY() - t.getY());
				if (weight < bestWeight)
					bestWeight = weight;
			}

			return distance + bestWeight;
		}

		// Order nodes by shortest distance + estimate first.
		public int compareTo(PathFindNode other) {
			int thisWeight = getWeight();
			int otherWeight = other.getWeight();

			// If the distance is the same, pick the one that seems more logical
			// by taking diagonals first.
			if (thisWeight == otherWeight) {
				thisWeight = getNonDiagonalWeight();
				otherWeight = other.getNonDiagonalWeight();
			}

			return thisWeight - otherWeight;
		}

		public void setSource(PathFindNode source, int length) {
			setSource(source, length, state);
		}

		public void setSource(PathFindNode source, int length, PathFindState newState) {
			distance = source.distance + length;
			parent = source;
			state = newState;
		}
	}

	private static class PathFindState {
		// This is simply a marker class. No data available.
	}

	private static class PathFindData {
		private Hashtable<PathFindState, PathFindNode[][]> nodes;

		public PathFindData() {
			nodes = new Hashtable<PathFindState, PathFinderWithPassBlockSupport.PathFindNode[][]>();
			nodes.put(normalState, new PathFindNode[FieldCoordinate.FIELD_WIDTH][FieldCoordinate.FIELD_HEIGHT]);
			nodes.put(leapState, new PathFindNode[FieldCoordinate.FIELD_WIDTH][FieldCoordinate.FIELD_HEIGHT]);
		}

		public PathFindNode blockNode(FieldCoordinate coordinate) {
			PathFindNode blockedNode = new PathFindNode(globalState, coordinate, 1000, false, null, null);

			for (PathFindState state : nodes.keySet())
				nodes.get(state)[coordinate.getX()][coordinate.getY()] = blockedNode;

			return blockedNode;
		}

		public boolean isProcessed(PathFindState state, int x, int y) {
			return nodes.get(state)[x][y] != null;
		}

		public void setNode(FieldCoordinate coord, PathFindNode node) {
			nodes.get(node.state)[coord.getX()][coord.getY()] = node;
		}

		public PathFindNode getNeighbour(PathFindState state, FieldCoordinate neighbour) {
			return nodes.get(state)[neighbour.getX()][neighbour.getY()];
		}
	}

	// Constructs a path from the PathFindNode structure
	private static FieldCoordinate[] reconstructPath(PathFindNode end) {
		LinkedList<FieldCoordinate> list = new LinkedList<FieldCoordinate>();
		FieldCoordinate[] result = new FieldCoordinate[end.distance];

		while (end.parent != null) {
			// The list is reversed, so we add to the head of the linked list.
			list.addFirst(end.coord);
			end = end.parent;
		}

		return list.toArray(result);
	}

	/**
	 * Gets the shortest path from the player in the start square to the end square.
	 * The start square must contain a player. The path will not leave or pass
	 * through a tackle zone, but it may end in one. The path will also avoid going
	 * through or landing on a player.
	 *
	 * @param pEndCoord  Target square.
	 * @return Shortest path to target square
	 */
	public static FieldCoordinate[] getShortestPath(Game pGame, FieldCoordinate pEndCoord) {
		if (pGame == null)
			return null;

		ActingPlayer actingPlayer = pGame.getActingPlayer();

		if (actingPlayer == null || actingPlayer.getPlayer() == null)
			return null;

		Team movingTeam = actingPlayer.getPlayer().getTeam();
		int maxDistance = actingPlayer.getPlayer().getMovementWithModifiers() - actingPlayer.getCurrentMove();

		Set<FieldCoordinate> pEndCoords = new HashSet<>(1);
		pEndCoords.add(pEndCoord);
		FieldCoordinate start = pGame.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());

		return getShortestPath(pGame, start, pEndCoords, maxDistance, movingTeam, normalMoveContext, false);
	}

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
	private static FieldCoordinate[] getShortestPath(Game pGame, FieldCoordinate start, Set<FieldCoordinate> pEndCoords,
			int maxDistance, Team movingTeam, PathFindContext context, boolean canLeap) {

		// Sanity check
		if (pGame == null) {
			return null;
		}

		FieldModel fieldModel = pGame.getFieldModel();

		// Sanity check
		if (start == null || pEndCoords == null || !isOnField(pGame, start) || !isOnField(pGame, pEndCoords)) {
			return null;
		}

		FieldCoordinateBounds endzoneBounds;
		if (movingTeam == pGame.getTeamHome()) {
			endzoneBounds = FieldCoordinateBounds.ENDZONE_AWAY;
		} else {
			endzoneBounds = FieldCoordinateBounds.ENDZONE_HOME;
		}

		// Keeps a list of potential candidates
		PriorityQueue<PathFindNode> openSet = new PriorityQueue<>();

		PathFindData data = new PathFindData();

		// Nodes which have already been processed
		Set<PathFindNode> closedSet = new HashSet<>();
		PathFindNode neighbour;

		// Initialise the open set with the start squares
		PathFindNode current = new PathFindNode(normalState, start, 0, false, pEndCoords, null);
		data.setNode(start, current);
		openSet.add(current);

		// Handle the square with the ball by marking it as a TZ.
		// This allows the player to move into the ball, but not through it.
		FieldCoordinate ballCoord = fieldModel.getBallCoordinate();
		if (isOnField(pGame, ballCoord) && context.blockTacklezones)
			data.setNode(ballCoord, new PathFindNode(normalState, ballCoord, 1000, true, pEndCoords, null));

		boolean hasBall = start.equals(ballCoord);

		// Block off squares with players in them
		FieldCoordinate[] players = fieldModel.getPlayerCoordinates();
		for (FieldCoordinate pCoord : players) {
			if (!isOnField(pGame, pCoord))
				continue;
			PathFindNode blockedNode = data.blockNode(pCoord);
			closedSet.add(blockedNode);

			if (context.blockTacklezones) {
				// And mark tackle zones as well for each opponent
				Player<?> p = fieldModel.getPlayer(pCoord);
				if (p.getTeam() != movingTeam) {
					// Skip if the player does not have a tackle zone
					if (!fieldModel.getPlayerState(p).hasTacklezones())
						continue;

					// Don't allow an initial dodge
					if (pCoord.isAdjacent(start))
						return null;

					FieldCoordinate[] tz = fieldModel.findAdjacentCoordinates(pCoord, FieldCoordinateBounds.FIELD, 1, false);
					for (FieldCoordinate tzCoord : tz) {
						if (data.isProcessed(normalState, tzCoord.getX(), tzCoord.getY()))
							continue;

						// mark the node as a tacklezone
						data.setNode(tzCoord, new PathFindNode(normalState, tzCoord, 1000, true, pEndCoords, null));
						continue;
					}
				}
			}
		}

		while (openSet.size() > 0) {
			// Get the node with the shortest distance that hasn't been
			// processed
			current = openSet.poll();

			// Check if we're beyond normal movement range
			if (current.distance > maxDistance) {
				return null;
			}

			// Are we at the target?
			if (pEndCoords.contains(current.coord)) {
				return reconstructPath(current);
			}

			// Mark as processed
			closedSet.add(current);

			boolean isInEndzone = endzoneBounds.isInBounds(current.coord);

			// For each neighbour of the square we're processing...
			int searchDistance = canLeap && current.state != leapState && context.allowLeap
					&& maxDistance - current.distance > 1 ? 2 : 1;
			FieldCoordinate[] neighbours = fieldModel.findAdjacentCoordinates(current.coord, FieldCoordinateBounds.FIELD,
					searchDistance, false);
			for (FieldCoordinate neighbourCoord : neighbours) {

				int distance = current.coord.distanceInSteps(neighbourCoord);

				// Don't allow a leap if the context explicitly disallows it, if the path
				// already has lept before or if the player can't leap
				if (distance > 1 && (maxDistance - current.distance - distance < 0 || current.state == leapState
						|| !context.allowLeap || !canLeap))
					continue;

				// Get the state of the next coordinate.
				PathFindState neighbourState = distance == 1 ? current.state : leapState;

				// Get the neighbour node from the cache if it exists
				neighbour = data.getNeighbour(neighbourState, neighbourCoord);

				// Did we already process the square?
				if (neighbour != null
						&& (closedSet.contains(neighbour) || (neighbour.tz && !pEndCoords.contains(neighbourCoord)))) {
					continue;
				}

				// Don't allow moving out of an endzone if the player has the ball.
				if (!context.allowExitEndzoneWithBall && hasBall && isInEndzone && !endzoneBounds.isInBounds(neighbourCoord))
					continue;

				if (neighbour == null) {
					// This square has not been touched at all yet
					neighbour = new PathFindNode(neighbourState, neighbourCoord, current.distance + distance, false, pEndCoords,
							current);
					data.setNode(neighbourCoord, neighbour);
					openSet.add(neighbour);
				} else if (current.distance + distance < neighbour.distance) {
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

	private static boolean isOnField(Game pGame, FieldCoordinate pCoordinate) {
		return (pGame.getTurnMode() == TurnMode.KICKOFF_RETURN) ? FieldCoordinateBounds.HALF_HOME.isInBounds(pCoordinate)
				: FieldCoordinateBounds.FIELD.isInBounds(pCoordinate);
	}

	private static boolean isOnField(Game pGame, Set<FieldCoordinate> pCoordinates) {
		boolean result = true;
		for (FieldCoordinate coord : pCoordinates) {
			result = result && isOnField(pGame, coord);
		}
		return result;
	}

	public static FieldCoordinate[] allowPassBlockMove(Game pGame, Player<?> passBlocker, FieldCoordinate startPosition,
			int distance, boolean canLeap) {
		// Skip if the player doesn't have pass block

		if (!passBlocker.hasSkillWithProperty(NamedProperties.canMoveWhenOpponentPasses)) {
			return new FieldCoordinate[0];
		}

		// Get a list of coordinates eligible for interception
		Set<FieldCoordinate> validEndCoordinates = UtilPassing.findValidPassBlockEndCoordinates(pGame);

		// Pathfind to the interception coordinates
		FieldCoordinate[] path = getShortestPath(pGame, startPosition, validEndCoordinates, distance, passBlocker.getTeam(),
				passBlockContext, canLeap);

		// If we have a path, the player can intercept.
		return path;

	}

}
