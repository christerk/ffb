package com.fumbbl.ffb.server.step.generator;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.server.GameState;

public abstract class ScatterPlayer extends SequenceGenerator<ScatterPlayer.SequenceParams> {

	public ScatterPlayer() {
		super(Type.ScatterPlayer);
	}

	public static class SequenceParams extends SequenceGenerator.SequenceParams {
		private final String thrownPlayerId;
		private final PlayerState thrownPlayerState;
		private final FieldCoordinate thrownPlayerCoordinate;
		private final boolean hasSwoop, thrownPlayerHasBall, throwScatter, deviate, crashLanding;

		public SequenceParams(GameState gameState, String thrownPlayerId, PlayerState thrownPlayerState, boolean thrownPlayerHasBall, FieldCoordinate thrownPlayerCoordinate, boolean hasSwoop,  boolean throwScatter) {
			this(gameState, thrownPlayerId, thrownPlayerState, thrownPlayerHasBall, thrownPlayerCoordinate, hasSwoop, throwScatter, false, false);
		}

		public SequenceParams(GameState gameState, String thrownPlayerId, PlayerState thrownPlayerState, boolean thrownPlayerHasBall, FieldCoordinate thrownPlayerCoordinate, boolean hasSwoop,
		                      boolean throwScatter, boolean deviate, boolean crashLanding) {
			super(gameState);
			this.thrownPlayerId = thrownPlayerId;
			this.thrownPlayerState = thrownPlayerState;
			this.thrownPlayerHasBall = thrownPlayerHasBall;
			this.thrownPlayerCoordinate = thrownPlayerCoordinate;
			this.hasSwoop = hasSwoop;
			this.throwScatter = throwScatter;
			this.deviate = deviate;
			this.crashLanding = crashLanding;

		}

		public boolean isCrashLanding() {
			return crashLanding;
		}

		public boolean deviates() {
			return deviate;
		}

		public String getThrownPlayerId() {
			return thrownPlayerId;
		}

		public PlayerState getThrownPlayerState() {
			return thrownPlayerState;
		}

		public FieldCoordinate getThrownPlayerCoordinate() {
			return thrownPlayerCoordinate;
		}

		public boolean hasSwoop() {
			return hasSwoop;
		}

		public boolean isThrownPlayerHasBall() {
			return thrownPlayerHasBall;
		}

		public boolean isThrowScatter() {
			return throwScatter;
		}
	}
}
