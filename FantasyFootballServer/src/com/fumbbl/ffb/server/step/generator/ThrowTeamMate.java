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

		public SequenceParams(GameState gameState, String thrownPlayerId, FieldCoordinate targetCoordinate) {
			super(gameState);
			this.thrownPlayerId = thrownPlayerId;
			this.targetCoordinate = targetCoordinate;
		}

		public SequenceParams(GameState gameState) {
			this(gameState, null, null);
		}

		public String getThrownPlayerId() {
			return thrownPlayerId;
		}

		public FieldCoordinate getTargetCoordinate() {
			return targetCoordinate;
		}
	}
}
