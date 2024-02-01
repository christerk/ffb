package com.fumbbl.ffb.server.step.generator;

import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.server.GameState;

public abstract class BlackInk extends SequenceGenerator<BlackInk.SequenceParams> {
	public BlackInk() {
		super(Type.BlackInk);
	}

	public static class SequenceParams extends SequenceGenerator.SequenceParams {

		private final String goToLabelFailure;
		private final PlayerState oldPlayerState;

		public SequenceParams(GameState gameState, String goToLabelFailure, PlayerState oldPlayerState) {
			super(gameState);
			this.goToLabelFailure = goToLabelFailure;
			this.oldPlayerState = oldPlayerState;
		}

		public String getGoToLabelFailure() {
			return goToLabelFailure;
		}

		public PlayerState getOldPlayerState() {
			return oldPlayerState;
		}
	}
}
