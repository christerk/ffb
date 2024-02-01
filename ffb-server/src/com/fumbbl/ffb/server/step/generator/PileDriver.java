package com.fumbbl.ffb.server.step.generator;

import com.fumbbl.ffb.server.GameState;

public abstract class PileDriver extends SequenceGenerator<PileDriver.SequenceParams> {

	public PileDriver() {
		super(Type.PileDriver);
	}

	public static class SequenceParams extends SequenceGenerator.SequenceParams {

		private final String targetPlayerId;

		public SequenceParams(GameState gameState, String targetPlayerId) {
			super(gameState);
			this.targetPlayerId = targetPlayerId;
		}

		public String getTargetPlayerId() {
			return targetPlayerId;
		}
	}
}
