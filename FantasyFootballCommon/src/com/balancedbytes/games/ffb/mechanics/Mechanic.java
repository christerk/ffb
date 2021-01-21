package com.balancedbytes.games.ffb.mechanics;

import com.balancedbytes.games.ffb.INamedObject;

public interface Mechanic extends INamedObject {

	Type getType();

	@Override
	default String getName() {
		return getType().name();
	}

	enum Type {
		PASS;
	}
}
