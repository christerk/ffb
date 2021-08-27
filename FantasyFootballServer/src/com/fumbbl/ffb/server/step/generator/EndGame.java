package com.fumbbl.ffb.server.step.generator;

import com.fumbbl.ffb.server.GameState;

public abstract class EndGame extends SequenceGenerator<EndGame.SequenceParams> {

	public EndGame() {
		super(Type.EndGame);
	}

	public static class SequenceParams extends SequenceGenerator.SequenceParams {
		private final boolean adminMode;

		public SequenceParams(GameState gameState, boolean adminMode) {
			super(gameState);
			this.adminMode = adminMode;
		}

		public boolean isAdminMode() {
			return adminMode;
		}
	}
}
