package com.fumbbl.ffb.server.step.bb2025.command;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.server.step.DeferredCommand;
import com.fumbbl.ffb.server.step.DeferredCommandId;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;

@RulesCollection(RulesCollection.Rules.BB2025)
public class AnimalSavageryControlCommand extends DeferredCommand {

	public void execute(IStep step) {
		step.publishParameter(new StepParameter(StepParameterKey.USE_ALTERNATE_LABEL, true));
		step.publishParameter(new StepParameter(StepParameterKey.THROWN_PLAYER_COORDINATE, null)); // avoid reset in end step
	}

	@Override
	public DeferredCommandId getId() {
		return DeferredCommandId.ANIMAL_SAVAGERY_CONTROL;
	}

}


