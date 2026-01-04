package com.fumbbl.ffb.util.pathfinding;

class PathFindContext {
	private final boolean allowJump;
	private final boolean allowExitEndzoneWithBall;
	private final boolean blockTacklezones;

	PathFindContext(boolean allowJump, boolean allowExitEndzoneWithBall, boolean blockTacklezones) {
		this.allowJump = allowJump;
		this.allowExitEndzoneWithBall = allowExitEndzoneWithBall;
		this.blockTacklezones = blockTacklezones;
	}

	public boolean isAllowExitEndzoneWithBall() {
		return allowExitEndzoneWithBall;
	}

	public boolean isBlockTacklezones() {
		return blockTacklezones;
	}

	public boolean isAllowJump() {
		return allowJump;
	}
}
