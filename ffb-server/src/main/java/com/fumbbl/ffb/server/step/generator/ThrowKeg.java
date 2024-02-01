package com.fumbbl.ffb.server.step.generator;

import com.fumbbl.ffb.server.GameState;

public abstract class ThrowKeg extends SequenceGenerator<ThrowKeg.SequenceParams> {
	public ThrowKeg() {
		super(Type.ThrowKeg);
	}

	public static class SequenceParams extends SequenceGenerator.SequenceParams {

		private final String playerId;

		public SequenceParams(GameState gameState, String playerId) {
			super(gameState);
			this.playerId = playerId;
		}

		public String getPlayerId() {
			return playerId;
		}
	}
}
