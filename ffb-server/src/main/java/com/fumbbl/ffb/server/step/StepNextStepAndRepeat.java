package com.fumbbl.ffb.server.step;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.server.GameState;

@RulesCollection(RulesCollection.Rules.COMMON)
public class StepNextStepAndRepeat extends AbstractStep {

	public StepNextStepAndRepeat(GameState pGameState) {
		super(pGameState);
	}

	public StepId getId() {
		return StepId.NEXT_STEP_AND_REPEAT;
	}

	@Override
	public void start() {
		super.start();
		getResult().setNextAction(StepAction.NEXT_STEP_AND_REPEAT);
	}

}
