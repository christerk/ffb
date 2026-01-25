package com.fumbbl.ffb.server.step.generator;

import com.fumbbl.ffb.server.GameState;

public abstract class EndTurn extends SequenceGenerator<EndTurn.SequenceParams> {

	public EndTurn() {
		super(Type.EndTurn);
	}

	public static class SequenceParams extends SequenceGenerator.SequenceParams {
		private final boolean checkForgo;

		public SequenceParams(GameState gameState, boolean checkForgo) {
			super(gameState);
			this.checkForgo = checkForgo;
		}

		public boolean isCheckForgo() {
			return checkForgo;
		}
	}

}
