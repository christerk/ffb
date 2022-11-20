package com.fumbbl.ffb.server.step.generator;

import com.fumbbl.ffb.server.GameState;

public abstract class RadingParty extends SequenceGenerator<RadingParty.SequenceParams> {
	public RadingParty() {
		super(Type.RaidingParty);
	}

	public static class SequenceParams extends SequenceGenerator.SequenceParams {

		private final String failureLabel;
		private final String successLabel;

		public SequenceParams(GameState gameState, String failureLabel, String successLabel) {
			super(gameState);
			this.failureLabel = failureLabel;
			this.successLabel = successLabel;
		}

		public String getFailureLabel() {
			return failureLabel;
		}

		public String getSuccessLabel() {
			return successLabel;
		}
	}
}
