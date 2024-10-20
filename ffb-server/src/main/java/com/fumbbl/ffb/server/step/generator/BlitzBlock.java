package com.fumbbl.ffb.server.step.generator;

import com.fumbbl.ffb.server.GameState;

public abstract class BlitzBlock extends SequenceGenerator<BlitzBlock.SequenceParams> {

	public BlitzBlock() {
		super(Type.BlitzBlock);
	}

	public static class SequenceParams extends SequenceGenerator.SequenceParams {
		private final String blockDefenderId;
		private final String multiBlockDefenderId;
		private final boolean usingStab, usingChainsaw, usingVomit, frenzyBlock, askForBlockKind, usingBreatheFire;
		private boolean publishDefender;

		public SequenceParams(GameState gameState, String blockDefenderId, boolean usingStab, boolean usingChainsaw, boolean usingVomit, boolean usingBreatheFire) {
			this(gameState, blockDefenderId, usingStab, usingChainsaw, usingVomit, false, null, false, usingBreatheFire);
		}

		public SequenceParams(GameState gameState, String blockDefenderId, boolean usingStab, String multiBlockDefenderId) {
			this(gameState, blockDefenderId, usingStab, false, false, false, multiBlockDefenderId, false, false);
		}

		public SequenceParams(GameState gameState, String blockDefenderId, boolean usingStab, boolean frenzyBlock, String multiBlockDefenderId) {
			this(gameState, blockDefenderId, usingStab, false, false, frenzyBlock, multiBlockDefenderId, false, false);
		}

		public SequenceParams(GameState gameState, String blockDefenderId, boolean usingStab, boolean frenzyBlock,
													String multiBlockDefenderId, boolean askForBlockKind) {
			this(gameState, blockDefenderId, usingStab, false, false, frenzyBlock, multiBlockDefenderId, askForBlockKind, false);
		}

		public SequenceParams(GameState gameState, String blockDefenderId, boolean usingStab, boolean usingChainsaw,
													boolean usingVomit, boolean frenzyBlock, String multiBlockDefenderId, boolean askForBlockKind,
													boolean usingBreatheFire) {
			super(gameState);
			this.blockDefenderId = blockDefenderId;
			this.multiBlockDefenderId = multiBlockDefenderId;
			this.usingStab = usingStab;
			this.usingChainsaw = usingChainsaw;
			this.usingVomit = usingVomit;
			this.frenzyBlock = frenzyBlock;
			this.askForBlockKind = askForBlockKind;
			this.usingBreatheFire = usingBreatheFire;
		}

		public SequenceParams(GameState gameState, boolean usingChainsaw) {
			this(gameState, null, false, usingChainsaw, false, false, null, false, false);
		}

		public SequenceParams(GameState gameState) {
			this(gameState, null, false, false, false, false, null, false, false);
		}

		public SequenceParams(GameState gameState, boolean usingChainsaw, boolean publishDefender) {
			this(gameState, usingChainsaw);
			this.publishDefender = publishDefender;
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

		public boolean isUsingBreatheFire() {
			return usingBreatheFire;
		}
	}
}
