package com.fumbbl.ffb.util.pathfinding;

import com.fumbbl.ffb.FieldCoordinate;

import java.util.Hashtable;

class PathFindData {
	private final Hashtable<PathFindState, PathFindNode[][]> nodes;

	public PathFindData() {
		nodes = new Hashtable<>();
		nodes.put(PathFindState.NORMAL, new PathFindNode[FieldCoordinate.FIELD_WIDTH][FieldCoordinate.FIELD_HEIGHT]);
		nodes.put(PathFindState.JUMP, new PathFindNode[FieldCoordinate.FIELD_WIDTH][FieldCoordinate.FIELD_HEIGHT]);
	}

	public PathFindNode blockNode(FieldCoordinate coordinate) {
		PathFindNode blockedNode = new PathFindNode(PathFindState.GLOBAL, coordinate, 1000, false, null, null);

		for (PathFindState state : nodes.keySet()) {
			nodes.get(state)[coordinate.getX()][coordinate.getY()] = blockedNode;
		}

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
