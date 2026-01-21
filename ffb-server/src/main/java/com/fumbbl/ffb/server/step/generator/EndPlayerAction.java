package com.fumbbl.ffb.server.step.generator;

import com.fumbbl.ffb.server.GameState;

public abstract class EndPlayerAction extends SequenceGenerator<EndPlayerAction.SequenceParams> {

	public EndPlayerAction() {
		super(Type.EndPlayerAction);
	}

	public static class SequenceParams extends SequenceGenerator.SequenceParams {
		private final boolean feedingAllowed, endPlayerAction, endTurn, checkForgo;

		public SequenceParams(GameState gameState, boolean feedingAllowed, boolean endPlayerAction, boolean endTurn) {
			this(gameState, feedingAllowed, endPlayerAction, endTurn, false);
		}

		public SequenceParams(GameState gameState, boolean feedingAllowed, boolean endPlayerAction, boolean endTurn, boolean checkForgo) {
			super(gameState);
			this.feedingAllowed = feedingAllowed;
			this.endPlayerAction = endPlayerAction;
			this.endTurn = endTurn;
			this.checkForgo = checkForgo;
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

		public boolean isCheckForgo() {
			return checkForgo;
		}
	}
}
