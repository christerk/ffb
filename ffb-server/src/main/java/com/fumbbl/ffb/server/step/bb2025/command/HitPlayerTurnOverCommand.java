package com.fumbbl.ffb.server.step.bb2025.command;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.server.step.DeferredCommand;
import com.fumbbl.ffb.server.step.DeferredCommandId;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;

@RulesCollection(RulesCollection.Rules.BB2025)
public class HitPlayerTurnOverCommand extends DeferredCommand {

	@Override
	public void execute(IStep step) {
		step.publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
	}

	@Override
	public DeferredCommandId getId() {
		return DeferredCommandId.HIT_PLAYER;
	}

}
