package com.fumbbl.ffb.server.step.generator;

import com.fumbbl.ffb.server.GameState;

public abstract class SpecialEffect extends SequenceGenerator<SpecialEffect.SequenceParams> {

	public SpecialEffect() {
		super(Type.SpecialEffect);
	}

	public static class SequenceParams extends SequenceGenerator.SequenceParams {
		private final com.fumbbl.ffb.SpecialEffect specialEffect;
		private final String playerId;
		private final boolean rollForEffect;

		public SequenceParams(GameState gameState, com.fumbbl.ffb.SpecialEffect specialEffect, String playerId, boolean rollForEffect) {
			super(gameState);
			this.specialEffect = specialEffect;
			this.playerId = playerId;
			this.rollForEffect = rollForEffect;
		}

		public com.fumbbl.ffb.SpecialEffect getSpecialEffect() {
			return specialEffect;
		}

		public String getPlayerId() {
			return playerId;
		}

		public boolean isRollForEffect() {
			return rollForEffect;
		}
	}
}
