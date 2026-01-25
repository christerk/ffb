package com.fumbbl.ffb.client.state.logic.plugin;

import com.fumbbl.ffb.INamedObject;

public interface LogicPlugin extends INamedObject {

	Type getType();

	@Override
	default String getName() {
		return getType().name();
	}

	enum Type {
		MOVE
	}
}
