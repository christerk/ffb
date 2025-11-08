package com.fumbbl.ffb.server.step;

import com.fumbbl.ffb.INamedObject;

public enum DeferredCommandId implements INamedObject {
	ANIMAL_SAVAGERY_CANCEL_ACTION, ANIMAL_SAVAGERY_CONTROL, DROP_PLAYER, STAND_UP;


	@Override
	public String getName() {
		return name();
	}
}
