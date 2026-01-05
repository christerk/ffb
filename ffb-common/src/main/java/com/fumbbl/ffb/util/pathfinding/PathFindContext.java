package com.fumbbl.ffb.util.pathfinding;

class PathFindContext {
	private boolean allowJump;
	private boolean allowExitEndzoneWithBall;
	private boolean blockTacklezones;
	private boolean blockTrapdoors;
	private boolean blockBall;

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

	public boolean isBlockTrapdoors() {
		return blockTrapdoors;
	}

	public boolean isBlockBall() {
		return blockBall;
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

		// Keeping this unused methods around, in case we decide to use new pathfinder for more scenarios
		@SuppressWarnings("unused")
		public Builder blockTrapdoors() {
			context.blockTrapdoors = true;
			return this;
		}

		@SuppressWarnings("unused")
		public Builder blockBall() {
			context.blockBall = true;
			return this;
		}

		PathFindContext build() {
			return context;
		}
	}
}
