package com.balancedbytes.games.ffb.server.step.generator;

import com.balancedbytes.games.ffb.server.GameState;

public abstract class BlitzBlock extends SequenceGenerator<BlitzBlock.SequenceParams> {

	protected BlitzBlock(Type type) {
		super(type);
	}

	public static class SequenceParams extends SequenceGenerator.SequenceParams {
		private final String blockDefenderId;
		private final String multiBlockDefenderId;
		private final boolean usingStab;

		public SequenceParams(GameState gameState, String blockDefenderId, boolean usingStab, String multiBlockDefenderId) {
			super(gameState);
			this.blockDefenderId = blockDefenderId;
			this.multiBlockDefenderId = multiBlockDefenderId;
			this.usingStab = usingStab;
		}

		public SequenceParams(GameState gameState) {
			this(gameState, null, false, null);
		}

		public String getBlockDefenderId() {
			return blockDefenderId;
		}

		public String getMultiBlockDefenderId() {
			return multiBlockDefenderId;
		}

		public boolean isUsingStab() {
			return usingStab;
		}
	}
}
