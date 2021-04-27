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
		AGILITY, GAME, JUMP, ON_THE_BALL, PASS, STAT,

		// Server
		ROLL
	}
}
