package com.fumbbl.ffb.server.step.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.AbstractStep;
import com.fumbbl.ffb.server.step.StepId;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepPlaceBall extends AbstractStep {

	public StepPlaceBall(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepId getId() {
		return StepId.PLACE_BALL;
	}
}
