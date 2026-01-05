package com.fumbbl.ffb.util.pathfinding;

class PathFindContext {
	private boolean allowJump;
	private boolean allowExitEndzoneWithBall;
	private boolean blockTacklezones;

	private PathFindContext() {
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public boolean isAllowExitEndzoneWithBall() {
		return allowExitEndzoneWithBall;
	}

	public boolean isBlockTacklezones() {
		return blockTacklezones;
	}

	public boolean isAllowJump() {
		return allowJump;
	}

	public static class Builder {
		private final PathFindContext context;

		public Builder() {
			context = new PathFindContext();
		}

		public Builder allowJump() {
			context.allowJump = true;
			return this;
		}

		public Builder allowExitEndzoneWithBall() {
			context.allowExitEndzoneWithBall = true;
			return this;
		}

		public Builder blockTacklezones() {
			context.blockTacklezones = true;
			return this;
		}

		PathFindContext build() {
			return context;
		}
	}
}
