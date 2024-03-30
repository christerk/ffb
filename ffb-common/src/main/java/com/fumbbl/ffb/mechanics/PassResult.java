package com.fumbbl.ffb.mechanics;

import com.fumbbl.ffb.INamedObject;

public enum PassResult implements INamedObject {
	FUMBLE, SAVED_FUMBLE, WILDLY_INACCURATE, INACCURATE, ACCURATE;

	@Override
	public String getName() {
		return name();
	}
}
