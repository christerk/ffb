package com.balancedbytes.games.ffb.server.step.generator;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.server.GameState;

public abstract class Pass extends SequenceGenerator<com.balancedbytes.games.ffb.server.step.generator.Pass.SequenceParams> {
	public Pass() {
		super(Type.Pass);
	}

	public abstract void pushSequence(SequenceParams params);

	public static class SequenceParams extends SequenceGenerator.SequenceParams {
		private final FieldCoordinate targetCoordinate;

		public SequenceParams(GameState gameState, FieldCoordinate targetCoordinate) {
			super(gameState);
			this.targetCoordinate = targetCoordinate;
		}

		public FieldCoordinate getTargetCoordinate() {
			return targetCoordinate;
		}

		public SequenceParams(GameState gameState) {
			this(gameState, null);
		}
	}
}
