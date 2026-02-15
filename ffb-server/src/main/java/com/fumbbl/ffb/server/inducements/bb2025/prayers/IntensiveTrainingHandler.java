package com.fumbbl.ffb.server.inducements.bb2025.prayers;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.inducement.bb2025.Prayer;

@RulesCollection(RulesCollection.Rules.BB2025)
public class IntensiveTrainingHandler extends com.fumbbl.ffb.server.inducements.mixed.prayers.IntensiveTrainingHandler {

	@Override
	public Prayer handledPrayer() {
		return Prayer.INTENSIVE_TRAINING;
	}

	@Override
	public PlayerSelector selector() {
		return PlayerSelector.INSTANCE;
	}
}
