package com.balancedbytes.games.ffb.server.step.generator;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.server.GameState;

public abstract class BlitzMove extends SequenceGenerator<BlitzMove.SequenceParams> {

	protected BlitzMove(Type type) {
		super(type);
	}

	public static class SequenceParams extends SequenceGenerator.SequenceParams {
		private final FieldCoordinate[] pMoveStack;
		private final String pGazeVictimId;

		public SequenceParams(GameState gameState, FieldCoordinate[] pMoveStack, String pGazeVictimId) {
			super(gameState);
			this.pMoveStack = pMoveStack;
			this.pGazeVictimId = pGazeVictimId;
		}

		public SequenceParams(GameState gameState) {
			this(gameState, null, null);
		}

		public FieldCoordinate[] getMoveStack() {
			return pMoveStack;
		}

		public String getGazeVictimId() {
			return pGazeVictimId;
		}
	}
}
