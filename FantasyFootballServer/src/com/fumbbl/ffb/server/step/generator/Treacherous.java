package com.fumbbl.ffb.server.step.generator;

import com.fumbbl.ffb.server.GameState;

public abstract class Treacherous extends SequenceGenerator<Treacherous.SequenceParams> {
	public Treacherous() {
		super(Type.Treacherous);
	}

	public static class SequenceParams extends SequenceGenerator.SequenceParams {

		private final String failureLabel;

		public SequenceParams(GameState gameState, String failureLabel) {
			super(gameState);
			this.failureLabel = failureLabel;
		}

		public String getFailureLabel() {
			return failureLabel;
		}
	}
}
