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
		private final FieldCoordinate moveStart;

		public SequenceParams(GameState gameState, FieldCoordinate[] pMoveStack, String pGazeVictimId, FieldCoordinate moveStart) {
			super(gameState);
			this.pMoveStack = pMoveStack;
			this.pGazeVictimId = pGazeVictimId;
			this.moveStart = moveStart;
		}

		public SequenceParams(GameState gameState) {
			this(gameState, null, null, null);
		}

		public FieldCoordinate[] getMoveStack() {
			return pMoveStack;
		}

		public String getGazeVictimId() {
			return pGazeVictimId;
		}

		public FieldCoordinate getMoveStart() {
			return moveStart;
		}
	}
}
