package com.fumbbl.ffb.mechanics;

import com.fumbbl.ffb.INamedObject;

public interface Mechanic extends INamedObject {

	Type getType();

	@Override
	default String getName() {
		return getType().name();
	}

	enum Type {
		// Common
		AGILITY, GAME, INJURY, JUMP, ON_THE_BALL, PASS, SKILL, STAT, TTM,

		// Server
		ROLL
	}
}
