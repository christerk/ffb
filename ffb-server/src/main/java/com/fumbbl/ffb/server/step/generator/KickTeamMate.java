package com.fumbbl.ffb.server.step.generator;

import com.fumbbl.ffb.server.GameState;

public abstract class KickTeamMate extends SequenceGenerator<KickTeamMate.SequenceParams> {

	public KickTeamMate() {
		super(Type.KickTeamMate);
	}

	public static class SequenceParams extends SequenceGenerator.SequenceParams {
		private final int numDice;
		private final String kickedPlayerId;

		public SequenceParams(GameState gameState, int numDice, String kickedPlayerId) {
			super(gameState);
			this.numDice = numDice;
			this.kickedPlayerId = kickedPlayerId;
		}

		public SequenceParams(GameState gameState) {
			this(gameState, 0, null);
		}

		public int getNumDice() {
			return numDice;
		}

		public String getKickedPlayerId() {
			return kickedPlayerId;
		}
	}
}
