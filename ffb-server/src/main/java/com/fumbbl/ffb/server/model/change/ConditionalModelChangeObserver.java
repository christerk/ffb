package com.fumbbl.ffb.server.model.change;

import com.fumbbl.ffb.INamedObject;
import com.fumbbl.ffb.model.change.ModelChange;
import com.fumbbl.ffb.server.GameState;

public interface ConditionalModelChangeObserver extends INamedObject {

	@Override
	default String getName() {
		return this.getClass().getSimpleName();
	}

	void next(GameState gameState, ModelChange modelChange);
}
