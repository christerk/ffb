package com.balancedbytes.games.ffb.server.step.bb2020;

import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.step.AbstractStep;
import com.balancedbytes.games.ffb.server.step.StepAction;
import com.balancedbytes.games.ffb.server.step.StepId;

public class StepSelectBlitzTarget extends AbstractStep {

	protected StepSelectBlitzTarget(GameState pGameState) {
		super(pGameState);
	}

	protected StepSelectBlitzTarget(GameState pGameState, StepAction defaultStepResult) {
		super(pGameState, defaultStepResult);
	}

	@Override
	public StepId getId() {
		return StepId.SELECT_BLITZ_TARGET;
	}
}
