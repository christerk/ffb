package com.fumbbl.ffb.server.step.generator;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.server.GameState;

public abstract class Move extends SequenceGenerator<Move.SequenceParams> {

	public Move() {
		super(Type.Move);
	}

	public static class SequenceParams extends SequenceGenerator.SequenceParams {
		private final FieldCoordinate[] pMoveStack;
		private final String pGazeVictimId;
		private final FieldCoordinate moveStart;
		private final String ballAndChainRrSetting;
		private final PlayerAction bloodlustAction;

		public SequenceParams(GameState gameState, FieldCoordinate[] pMoveStack, String pGazeVictimId, FieldCoordinate moveStart) {
			this(gameState, pMoveStack, pGazeVictimId, moveStart, null);
		}

		public SequenceParams(GameState gameState, FieldCoordinate[] pMoveStack, String pGazeVictimId, FieldCoordinate moveStart, String ballAndChainRrSetting) {
			this(gameState, pMoveStack, pGazeVictimId, moveStart, ballAndChainRrSetting, null);
		}

		public SequenceParams(GameState gameState, FieldCoordinate[] pMoveStack, String pGazeVictimId, FieldCoordinate moveStart, String ballAndChainRrSetting, PlayerAction bloodlustAction) {
			super(gameState);
			this.pMoveStack = pMoveStack;
			this.pGazeVictimId = pGazeVictimId;
			this.moveStart = moveStart;
			this.ballAndChainRrSetting = ballAndChainRrSetting;
			this.bloodlustAction = bloodlustAction;
		}

		public SequenceParams(GameState gameState, PlayerAction bloodlustAction) {
			this(gameState, null, null, null, null, bloodlustAction);
		}

		public SequenceParams(GameState gameState) {
			this(gameState, null, null, null, null);
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

		public String getBallAndChainRrSetting() {
			return ballAndChainRrSetting;
		}

		public PlayerAction getBloodlustAction() {
			return bloodlustAction;
		}
	}
}
