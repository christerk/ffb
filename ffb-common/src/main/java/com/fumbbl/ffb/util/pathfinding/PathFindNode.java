package com.fumbbl.ffb.util.pathfinding;

import com.fumbbl.ffb.FieldCoordinate;

import java.util.Set;

class PathFindNode implements Comparable<PathFindNode> {
	// State of the current PathFindNode
	private PathFindState state;

	// Shortest distance found so far to the square
	private int distance;

	// Estimated distance to go
	private int estimate;

	// Does this square have a tz?
	private final boolean tz;

	// Coordinate of the square
	private final FieldCoordinate coord;

	// Target of the square
	private final Set<FieldCoordinate> target;

	// The previous node in the shortest path
	private PathFindNode parent;

	public PathFindNode(PathFindState state, FieldCoordinate coord, int distance,
		boolean tz, Set<FieldCoordinate> target, PathFindNode parent) {
		this.state = state;
		this.coord = coord;
		this.parent = parent;
		this.target = target;
		this.tz = tz;
		this.distance = distance;
		this.estimate = 1000;

		if (target != null) {
			for (FieldCoordinate t : target) {
				estimate = Math.min(estimate, coord.distanceInSteps(t));
			}
		}
	}

	public int getWeight() {
		return distance + estimate;
	}

	private int getNonDiagonalWeight() {
		int bestWeight = 10000;
		if (target != null) {
			for (FieldCoordinate t : target) {
				int weight = Math.abs(coord.getX() - t.getX()) + Math.abs(coord.getY() - t.getY());
				if (weight < bestWeight) {
					bestWeight = weight;
				}
			}
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
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		PathFindNode that = (PathFindNode) o;

		if (getWeight() != that.getWeight()) {
			return false;
		}
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

	public FieldCoordinate getCoord() {
		return coord;
	}

	public PathFindState getState() {
		return state;
	}

	public int getDistance() {
		return distance;
	}

	public boolean isTz() {
		return tz;
	}

	public PathFindNode getParent() {
		return parent;
	}
}
