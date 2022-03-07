package com.fumbbl.ffb.model.skill;

public enum SkillUsageType {
	REGULAR(false),
	ONCE_PER_TURN(true),
	ONCE_PER_GAME(true),
	ONCE_PER_HALF(true);

	private final boolean trackOutsideActivation;

	SkillUsageType(boolean trackOutsideActivation) {
		this.trackOutsideActivation = trackOutsideActivation;
	}

	public boolean isTrackOutsideActivation() {
		return trackOutsideActivation;
	}
}
