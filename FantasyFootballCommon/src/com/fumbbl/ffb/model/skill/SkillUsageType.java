package com.fumbbl.ffb.model.skill;

public enum SkillUsageType {
	REGULAR(false),
	ONCE_PER_TURN(true),
	ONCE_PER_GAME(true),
	ONCE_PER_HALF(true),
	ONCE_PER_DRIVE(true),
	ONCE_PER_TURN_BY_TEAM_MATE(true, false),
	SPECIAL(true, false);

	private final boolean trackOutsideActivation;
	private final boolean effectsRemovedAtEndOfTurn;

	SkillUsageType(boolean trackOutsideActivation) {
		this(trackOutsideActivation, true);
	}

	SkillUsageType(boolean trackOutsideActivation, boolean effectsRemovedAtEndOfTurn) {
		this.trackOutsideActivation = trackOutsideActivation;
		this.effectsRemovedAtEndOfTurn = effectsRemovedAtEndOfTurn;
	}

	public boolean removeEffectsAtEndOfTurn() {
		return effectsRemovedAtEndOfTurn;
	}

	public boolean isTrackOutsideActivation() {
		return trackOutsideActivation;
	}
}
