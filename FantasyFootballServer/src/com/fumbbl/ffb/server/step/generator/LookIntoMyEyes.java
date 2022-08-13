package com.fumbbl.ffb.server.step.generator;

import com.fumbbl.ffb.server.GameState;

public abstract class LookIntoMyEyes extends SequenceGenerator<LookIntoMyEyes.SequenceParams> {
	public LookIntoMyEyes() {
		super(Type.LookIntoMyEyes);
	}

	public static class SequenceParams extends SequenceGenerator.SequenceParams {
		private final boolean pushSelect;
		private final String gotoOnEnd;

		public SequenceParams(GameState gameState, boolean pushSelect, String gotoOnEnd) {
			super(gameState);
			this.pushSelect = pushSelect;
			this.gotoOnEnd = gotoOnEnd;
		}

		public boolean isPushSelect() {
			return pushSelect;
		}

		public String getGotoOnEnd() {
			return gotoOnEnd;
		}
	}
}
