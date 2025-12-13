package com.fumbbl.ffb.server.step.generator;

import com.fumbbl.ffb.server.GameState;

public abstract class Kickoff extends SequenceGenerator<Kickoff.SequenceParams> {

	public Kickoff() {
		super(Type.Kickoff);
	}

	public static class SequenceParams extends SequenceGenerator.SequenceParams {
		private final boolean withCoinChoice;

		public SequenceParams(GameState gameState, boolean withCoinChoice) {
			super(gameState);
			this.withCoinChoice = withCoinChoice;
		}

		public boolean isWithCoinChoice() {
			return withCoinChoice;
		}
	}
}
