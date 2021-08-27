package com.fumbbl.ffb.server.step.generator;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.server.GameState;

public abstract class ThrowTeamMate extends SequenceGenerator<ThrowTeamMate.SequenceParams> {

	public ThrowTeamMate() {
		super(Type.ThrowTeamMate);
	}

	public static class SequenceParams extends SequenceGenerator.SequenceParams {
		private final String thrownPlayerId;
		private final FieldCoordinate targetCoordinate;
		private final boolean kicked;

		public SequenceParams(GameState gameState, String thrownPlayerId, FieldCoordinate targetCoordinate, boolean kicked) {
			super(gameState);
			this.thrownPlayerId = thrownPlayerId;
			this.targetCoordinate = targetCoordinate;
			this.kicked = kicked;
		}

		public SequenceParams(GameState gameState) {
			this(gameState, null, null, false);
		}

		public SequenceParams(GameState gameState, String thrownPlayerId, boolean kicked) {
			this(gameState, thrownPlayerId, null, kicked);
		}

		public String getThrownPlayerId() {
			return thrownPlayerId;
		}

		public boolean isKicked() {
			return kicked;
		}

		public FieldCoordinate getTargetCoordinate() {
			return targetCoordinate;
		}
	}
}
