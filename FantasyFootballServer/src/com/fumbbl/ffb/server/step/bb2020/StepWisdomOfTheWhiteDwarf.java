package com.fumbbl.ffb.server.step.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepId;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepWisdomOfTheWhiteDwarf extends AbstractStep {
	public StepWisdomOfTheWhiteDwarf(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepId getId() {
		return StepId.WISDOM_OF_THE_WHITE_DWARF;
	}
}
