package com.fumbbl.ffb.server.step.generator;

import com.fumbbl.ffb.server.GameState;

public abstract class BlitzBlock extends SequenceGenerator<BlitzBlock.SequenceParams> {

	public BlitzBlock() {
		super(Type.BlitzBlock);
	}

	public static class SequenceParams extends SequenceGenerator.SequenceParams {
		private final String blockDefenderId;
		private final String multiBlockDefenderId;
		private final boolean usingStab, usingChainsaw;

		public SequenceParams(GameState gameState, String blockDefenderId, boolean usingStab, boolean usingChainsaw) {
			this(gameState, blockDefenderId, usingStab, usingChainsaw, null);
		}

		public SequenceParams(GameState gameState, String blockDefenderId, boolean usingStab, String multiBlockDefenderId) {
			this(gameState, blockDefenderId, usingStab, false, multiBlockDefenderId);
		}

		public SequenceParams(GameState gameState, String blockDefenderId, boolean usingStab, boolean usingChainsaw, String multiBlockDefenderId) {
			super(gameState);
			this.blockDefenderId = blockDefenderId;
			this.multiBlockDefenderId = multiBlockDefenderId;
			this.usingStab = usingStab;
			this.usingChainsaw = usingChainsaw;
		}

		public SequenceParams(GameState gameState, boolean usingChainsaw) {
			this(gameState, null, false, usingChainsaw, null);
		}

		public SequenceParams(GameState gameState) {
			this(gameState, null, false, false, null);
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

		public boolean isUsingChainsaw() {
			return usingChainsaw;
		}
	}
}
