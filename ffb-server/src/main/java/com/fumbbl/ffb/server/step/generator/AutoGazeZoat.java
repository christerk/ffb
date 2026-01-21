package com.fumbbl.ffb.server.step.generator;

import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.server.GameState;

public abstract class AutoGazeZoat extends SequenceGenerator<AutoGazeZoat.SequenceParams> {
	public AutoGazeZoat() {
		super(Type.AutoGazeZoat);
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
