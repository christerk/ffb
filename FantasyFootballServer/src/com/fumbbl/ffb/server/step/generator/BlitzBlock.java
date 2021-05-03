package com.fumbbl.ffb.server.step.generator;

import com.fumbbl.ffb.server.GameState;

public abstract class BlitzBlock extends SequenceGenerator<BlitzBlock.SequenceParams> {

	public BlitzBlock() {
		super(Type.BlitzBlock);
	}

	public static class SequenceParams extends SequenceGenerator.SequenceParams {
		private final String blockDefenderId;
		private final String multiBlockDefenderId;
		private final boolean usingStab, usingChainsaw, usingVomit;

		public SequenceParams(GameState gameState, String blockDefenderId, boolean usingStab, boolean usingChainsaw, boolean usingVomit) {
			this(gameState, blockDefenderId, usingStab, usingChainsaw, usingVomit, null);
		}

		public SequenceParams(GameState gameState, String blockDefenderId, boolean usingStab, String multiBlockDefenderId) {
			this(gameState, blockDefenderId, usingStab, false, false, multiBlockDefenderId);
		}

		public SequenceParams(GameState gameState, String blockDefenderId, boolean usingStab, boolean usingChainsaw, boolean usingVomit, String multiBlockDefenderId) {
			super(gameState);
			this.blockDefenderId = blockDefenderId;
			this.multiBlockDefenderId = multiBlockDefenderId;
			this.usingStab = usingStab;
			this.usingChainsaw = usingChainsaw;
			this.usingVomit = usingVomit;
		}

		public SequenceParams(GameState gameState, boolean usingChainsaw) {
			this(gameState, null, false, usingChainsaw, false, null);
		}

		public SequenceParams(GameState gameState) {
			this(gameState, null, false, false, false, null);
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

		public boolean isUsingVomit() {
			return usingVomit;
		}
	}
}
