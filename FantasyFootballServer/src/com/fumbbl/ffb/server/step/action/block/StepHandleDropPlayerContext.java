package com.fumbbl.ffb.server.step.action.block;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.model.DropPlayerContext;
import com.fumbbl.ffb.server.step.AbstractStepWithReRoll;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;

@RulesCollection(RulesCollection.Rules.BB2020)
public class StepHandleDropPlayerContext extends AbstractStepWithReRoll {

	private DropPlayerContext dropPlayerContext;

	protected StepHandleDropPlayerContext(GameState pGameState) {
		super(pGameState);
	}

	@Override
	public StepId getId() {
		return StepId.HANDLE_DROP_PLAYER_CONTEXT;
	}

	@Override
	public boolean setParameter(StepParameter parameter) {
		return super.setParameter(parameter);
	}
}
