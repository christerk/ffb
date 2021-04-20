package com.fumbbl.ffb.server.step.generator;

import com.fumbbl.ffb.server.GameState;

public abstract class EndPlayerAction extends SequenceGenerator<EndPlayerAction.SequenceParams> {

	public EndPlayerAction() {
		super(Type.EndPlayerAction);
	}

	public static class SequenceParams extends SequenceGenerator.SequenceParams {
		private final boolean feedingAllowed, endPlayerAction, endTurn;

		public SequenceParams(GameState gameState, boolean feedingAllowed, boolean endPlayerAction, boolean endTurn) {
			super(gameState);
			this.feedingAllowed = feedingAllowed;
			this.endPlayerAction = endPlayerAction;
			this.endTurn = endTurn;
		}

		public boolean isFeedingAllowed() {
			return feedingAllowed;
		}

		public boolean isEndPlayerAction() {
			return endPlayerAction;
		}

		public boolean isEndTurn() {
			return endTurn;
		}
	}
}
