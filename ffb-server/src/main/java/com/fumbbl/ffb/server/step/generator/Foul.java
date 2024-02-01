package com.fumbbl.ffb.server.step.generator;

import com.fumbbl.ffb.server.GameState;

public abstract class Foul extends SequenceGenerator<Foul.SequenceParams> {

	public Foul() {
		super(Type.Foul);
	}

	public static class SequenceParams extends SequenceGenerator.SequenceParams {
		private final String fouledDefenderId;
		private final boolean usingChainsaw;

		public SequenceParams(GameState gameState, String fouledDefenderId, boolean usingChainsaw) {
			super(gameState);
			this.fouledDefenderId = fouledDefenderId;
			this.usingChainsaw = usingChainsaw;
		}

		public SequenceParams(GameState gameState) {
			this(gameState, null, false);
		}

		public String getFouledDefenderId() {
			return fouledDefenderId;
		}

		public boolean isUsingChainsaw() {
			return usingChainsaw;
		}
	}
}