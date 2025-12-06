package com.fumbbl.ffb.server.step;

import com.fumbbl.ffb.INamedObject;

public enum DeferredCommandId implements INamedObject {
	ANIMAL_SAVAGERY_CANCEL_ACTION, ANIMAL_SAVAGERY_CONTROL, DROP_PLAYER, DROP_PLAYER_FROM_BOMB, HIT_PLAYER, RIGHT_STUFF,
	STAND_UP;


	@Override
	public String getName() {
		return name();
	}
}
