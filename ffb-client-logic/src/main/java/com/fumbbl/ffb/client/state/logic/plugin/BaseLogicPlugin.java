package com.fumbbl.ffb.client.state.logic.plugin;

import com.fumbbl.ffb.PlayerState;

public abstract class BaseLogicPlugin implements LogicPlugin {
	@Override
	public Type getType() {
		return Type.BASE;
	}

	public abstract boolean playerCanNotMove(PlayerState playerState);

}
