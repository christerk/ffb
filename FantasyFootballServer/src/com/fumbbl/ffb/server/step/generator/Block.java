package com.fumbbl.ffb.server.step.generator;

import com.fumbbl.ffb.server.GameState;

public abstract class Block extends SequenceGenerator<Block.SequenceParams> {

	public Block() {
		super(Type.Block);
	}

	public static class SequenceParams extends SequenceGenerator.SequenceParams {
		private final String blockDefenderId;
		private final String multiBlockDefenderId;
		private final boolean usingStab, usingChainsaw, usingVomit, frenzyBlock;

		public SequenceParams(GameState gameState, String blockDefenderId, boolean usingStab, boolean usingChainsaw, boolean usingVomit) {
			this(gameState, blockDefenderId, usingStab, usingChainsaw, usingVomit, false, null);
		}

		public SequenceParams(GameState gameState, String blockDefenderId, boolean usingStab, String multiBlockDefenderId) {
			this(gameState, blockDefenderId, usingStab, false, false, false, multiBlockDefenderId);
		}

		public SequenceParams(GameState gameState, String blockDefenderId, boolean usingStab, boolean frenzyBlock,  String multiBlockDefenderId) {
			this(gameState, blockDefenderId, usingStab, false, false, frenzyBlock, multiBlockDefenderId);
		}

		private SequenceParams(GameState gameState, String blockDefenderId, boolean usingStab, boolean usingChainsaw,
		                       boolean usingVomit, boolean frenzyBlock, String multiBlockDefenderId) {
			super(gameState);
			this.blockDefenderId = blockDefenderId;
			this.multiBlockDefenderId = multiBlockDefenderId;
			this.usingStab = usingStab;
			this.usingChainsaw = usingChainsaw;
			this.usingVomit = usingVomit;
			this.frenzyBlock = frenzyBlock;
		}

		public SequenceParams(GameState gameState) {
			this(gameState, null, false, false, false, false, null);
		}

		public SequenceParams(GameState gameState, boolean usingChainsaw) {
			this(gameState, null, false, usingChainsaw, false, false, null);
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

		public boolean isFrenzyBlock() {
			return frenzyBlock;
		}
	}
}
