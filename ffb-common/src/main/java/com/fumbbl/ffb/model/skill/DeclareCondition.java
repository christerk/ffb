package com.fumbbl.ffb.model.skill;

import com.fumbbl.ffb.PlayerState;

public enum DeclareCondition {
	NONE {
		@Override
		public boolean fulfilled(PlayerState playerState) {
			return true;
		}
	},
	STANDING {
		@Override
		public boolean fulfilled(PlayerState playerState) {
			return playerState != null && playerState.isStanding();
		}
	};

	public abstract boolean fulfilled(PlayerState playerState);
}
