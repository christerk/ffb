package com.fumbbl.ffb.server.step.bb2025.command;

import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.server.step.DeferredCommand;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;

public class AnimalSavageryControlCommand implements DeferredCommand {

	public void execute(IStep step) {
		step.publishParameter(new StepParameter(StepParameterKey.USE_ALTERNATE_LABEL, true));
		step.publishParameter(new StepParameter(StepParameterKey.THROWN_PLAYER_COORDINATE, null)); // avoid reset in end step
	}

	@Override
	public Object initFrom(IFactorySource source, JsonValue jsonValue) {
		//TODO
		return null;
	}

	@Override
	public JsonValue toJsonValue() {
		//TODO
		return null;
	}

}


