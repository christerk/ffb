package com.balancedbytes.games.ffb.mechanics;

import com.balancedbytes.games.ffb.INamedObject;

public enum PassResult implements INamedObject {
	FUMBLE, SAVED_FUMBLE, WILDLY_INACCURATE, INACCURATE, ACCURATE;

	@Override
	public String getName() {
		return name();
	}
}
