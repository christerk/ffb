package com.fumbbl.ffb.server.step.generator;

import com.fumbbl.ffb.server.GameState;

public abstract class BlackInk extends SequenceGenerator<BlackInk.SequenceParams> {
	public BlackInk() {
		super(Type.BlackInk);
	}

	public static class SequenceParams extends SequenceGenerator.SequenceParams {

		private final String goToLabelFailure;

		public SequenceParams(GameState gameState, String goToLabelFailure) {
			super(gameState);
			this.goToLabelFailure = goToLabelFailure;
		}

		public String getGoToLabelFailure() {
			return goToLabelFailure;
		}
	}
}
