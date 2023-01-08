package com.fumbbl.ffb.server.step.generator;

import com.fumbbl.ffb.server.GameState;

public abstract class Block extends SequenceGenerator<Block.SequenceParams> {

	public Block() {
		super(Type.Block);
	}

	public static class Builder {
		private final SequenceParams params;

		public Builder(GameState gameState) {
			params = new SequenceParams(gameState);
		}

		public SequenceParams build() {
			return params;
		}

		public Builder withDefenderId(String blockDefenderId) {
			params.blockDefenderId = blockDefenderId;
			return this;
		}

		public Builder withMultiBlockDefenderId(String multiBlockDefenderId) {
			params.multiBlockDefenderId = multiBlockDefenderId;
			return this;
		}

		public Builder publishDefender(boolean publishDefender) {
			params.publishDefender = publishDefender;
			return this;
		}

		public Builder useStab(boolean usingStab) {
			params.usingStab = usingStab;
			return this;
		}

		public Builder useChainsaw(boolean usingChainsaw) {
			params.usingChainsaw = usingChainsaw;
			return this;
		}

		public Builder useVomit(boolean usingVomit) {
			params.usingVomit = usingVomit;
			return this;
		}

		public Builder isFrenzyBlock(boolean frenzyBlock) {
			params.frenzyBlock = frenzyBlock;
			return this;
		}

		public Builder askForBlockKind(boolean askForBlockKind) {
			params.askForBlockKind = askForBlockKind;
			return this;
		}
	}

	public static class SequenceParams extends SequenceGenerator.SequenceParams {
		private String blockDefenderId;
		private String multiBlockDefenderId;
		private boolean usingStab, usingChainsaw, usingVomit, frenzyBlock, askForBlockKind;
		private boolean publishDefender;

		private SequenceParams(GameState gameState, String blockDefenderId, boolean usingStab, boolean usingChainsaw,
													 boolean usingVomit, boolean frenzyBlock, String multiBlockDefenderId, boolean askForBlockKind) {
			super(gameState);
			this.blockDefenderId = blockDefenderId;
			this.multiBlockDefenderId = multiBlockDefenderId;
			this.usingStab = usingStab;
			this.usingChainsaw = usingChainsaw;
			this.usingVomit = usingVomit;
			this.frenzyBlock = frenzyBlock;
			this.askForBlockKind = askForBlockKind;
		}

		private SequenceParams(GameState gameState) {
			super(gameState);
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

		public boolean isAskForBlockKind() {
			return askForBlockKind;
		}

		public boolean isPublishDefender() {
			return publishDefender;
		}
	}
}
