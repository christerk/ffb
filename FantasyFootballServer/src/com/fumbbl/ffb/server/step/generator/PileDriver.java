package com.fumbbl.ffb.server.step.generator;

import com.fumbbl.ffb.server.GameState;

import java.util.List;

public abstract class PileDriver extends SequenceGenerator<PileDriver.SequenceParams> {

	public PileDriver() {
		super(Type.PileDriver);
	}

	public static class SequenceParams extends SequenceGenerator.SequenceParams {

		private final List<String> knockedDownPlayers;

		public SequenceParams(GameState gameState, List<String> knockedDownPlayers) {
			super(gameState);
			this.knockedDownPlayers = knockedDownPlayers;
		}

		public List<String> getKnockedDownPlayers() {
			return knockedDownPlayers;
		}
	}
}
