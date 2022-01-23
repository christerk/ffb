package com.fumbbl.ffb;

import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.stadium.OnPitchEnhancement;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author Christer
 */
public class PathFinderWithPassBlockSupport {

	private static class PathFindContext {
		public boolean blockTacklezones = true;
		public boolean allowJump = false;
		public boolean allowExitEndzoneWithBall = false;
	}

	private static final PathFindState globalState;
	private static final PathFindState normalState;
	private static final PathFindState jumpState;

	private static final PathFindContext normalMoveContext;
	private static final PathFindContext passBlockContext;

	static {
		passBlockContext = new PathFindContext();
		passBlockContext.blockTacklezones = false;
		passBlockContext.allowJump = true;
		passBlockContext.allowExitEndzoneWithBall = true;

		normalMoveContext = new PathFindContext();
		normalMoveContext.blockTacklezones = true;
		normalMoveContext.allowJump = false;
		normalMoveContext.allowExitEndzoneWithBall = false;

		globalState = new PathFindState();
		normalState = new PathFindState();
		jumpState = new PathFindState();
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

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			PathFindNode that = (PathFindNode) o;

			if (getWeight() != that.getWeight()) return false;
			return getNonDiagonalWeight() == that.getNonDiagonalWeight();
		}

		@Override
		public int hashCode() {
			int result = getWeight();
			result = 31 * result + getNonDiagonalWeight();
			return result;
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
			int maxDistance, Team movingTeam, PathFindContext context, boolean canJump) {

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
		if (isOnField(pGame, ballCoord) && context.blockTacklezones && fieldModel.isBallInPlay()) {
			data.setNode(ballCoord, new PathFindNode(normalState, ballCoord, 1000, true, pEndCoords, null));
		}

		// Treat trapdoor fields as tackle zones to avoid paths crossing them
		fieldModel.getOnPitchEnhancements().stream().map(OnPitchEnhancement::getCoordinate)
				.forEach(coord -> data.setNode(coord, new PathFindNode(normalState, coord, 1000, true, pEndCoords, null)));

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
					if (!fieldModel.getPlayerState(p).hasTacklezones()) {
						continue;
					}

					// Don't allow an initial dodge
					if (pCoord.isAdjacent(start)) {
						return null;
					}

					FieldCoordinate[] tz = fieldModel.findAdjacentCoordinates(pCoord, FieldCoordinateBounds.FIELD, 1, false);
					for (FieldCoordinate tzCoord : tz) {
						if (data.isProcessed(normalState, tzCoord.getX(), tzCoord.getY())) {
							continue;
						}

						// mark the node as a tacklezone
						data.setNode(tzCoord, new PathFindNode(normalState, tzCoord, 1000, true, pEndCoords, null));
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
			int searchDistance = canJump && current.state != jumpState && context.allowJump
					&& maxDistance - current.distance > 1 ? 2 : 1;
			FieldCoordinate[] neighbours = fieldModel.findAdjacentCoordinates(current.coord, FieldCoordinateBounds.FIELD,
					searchDistance, false);
			for (FieldCoordinate neighbourCoord : neighbours) {

				int distance = current.coord.distanceInSteps(neighbourCoord);

				// Don't allow a jump if the context explicitly disallows it, if the path
				// already has jumped before or if the player can't jump
				if (distance > 1 && (maxDistance - current.distance - distance < 0 || current.state == jumpState
						|| !context.allowJump || !canJump))
					continue;

				// Get the state of the next coordinate.
				PathFindState neighbourState = distance == 1 ? current.state : jumpState;

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
	 * @param pEndCoord Target square.
	 * @return Shortest path to target square
	 */
	public static FieldCoordinate[] getShortestPath(Game pGame, FieldCoordinate pEndCoord) {
		Set<FieldCoordinate> pEndCoords = new HashSet<>(1);
		pEndCoords.add(pEndCoord);

		ActingPlayer actingPlayer = pGame.getActingPlayer();

		if (actingPlayer == null || actingPlayer.getPlayer() == null) {
			return null;
		}

		return getShortestPath(pGame, pEndCoords, actingPlayer.getPlayer(), actingPlayer.getCurrentMove());
	}

	/**
	 * Gets the shortest path from the player in the start square to the target
	 * player. The start square must contain a player. The path will not leave or
	 * pass through a tackle zone, but it may end in one. The path will also avoid
	 * going through.
	 *
	 * @param targetPlayer Target player.
	 * @return Shortest path to target square
	 */
	public static FieldCoordinate[] getShortestPathToPlayer(Game pGame, Player<?> targetPlayer) {
		FieldModel fieldModel = pGame.getFieldModel();
		FieldCoordinate[] adjacentSquares = fieldModel.findAdjacentCoordinates(
			fieldModel.getPlayerCoordinate(targetPlayer), FieldCoordinateBounds.FIELD, 1, false);

		Set<FieldCoordinate> pEndCoords = Arrays.stream(adjacentSquares).filter(s -> fieldModel.getPlayer(s) == null).collect(Collectors.toSet());

		ActingPlayer actingPlayer = pGame.getActingPlayer();

		if (actingPlayer == null || actingPlayer.getPlayer() == null) {
			return null;
		}

		FieldCoordinate start = pGame.getFieldModel().getPlayerCoordinate(actingPlayer.getPlayer());

		int maxDistance = actingPlayer.getPlayer().getMovementWithModifiers() - actingPlayer.getCurrentMove();

		return getShortestPath(pGame, start, pEndCoords, maxDistance, actingPlayer.getPlayer().getTeam(),
			normalMoveContext, false);
	}

	/**
	 * Gets the shortest path from the player in the start square to the end square.
	 * The start square must contain a player. The path will not leave or pass
	 * through a tackle zone, but it may end in one. The path will also avoid going
	 * through or landing on a player.
	 *
	 * @param pEndCoords Target squares.
	 * @return Shortest path to a target square
	 */
	public static FieldCoordinate[] getShortestPath(Game pGame, Set<FieldCoordinate> pEndCoords, Player<?> player,
			int currentMove) {
		if (pGame == null || player == null) {
			return null;
		}

		Team movingTeam = player.getTeam();
		int maxDistance = player.getMovementWithModifiers() - currentMove;

		FieldCoordinate start = pGame.getFieldModel().getPlayerCoordinate(player);

		return getShortestPath(pGame, start, pEndCoords, maxDistance, movingTeam, normalMoveContext, false);
	}

	private static class PathFindData {
		private final Hashtable<PathFindState, PathFindNode[][]> nodes;

		public PathFindData() {
			nodes = new Hashtable<PathFindState, PathFinderWithPassBlockSupport.PathFindNode[][]>();
			nodes.put(normalState, new PathFindNode[FieldCoordinate.FIELD_WIDTH][FieldCoordinate.FIELD_HEIGHT]);
			nodes.put(jumpState, new PathFindNode[FieldCoordinate.FIELD_WIDTH][FieldCoordinate.FIELD_HEIGHT]);
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
			int distance, boolean canJump, Set<FieldCoordinate> validEndCoordinates) {
		// Skip if the player doesn't have pass block

		if (!passBlocker.hasSkillProperty(NamedProperties.canMoveWhenOpponentPasses)) {
			return new FieldCoordinate[0];
		}

		// If we have a path, the player can intercept.
		return getShortestPath(pGame, startPosition, validEndCoordinates, distance, passBlocker.getTeam(), passBlockContext,
				canJump);

	}

}
