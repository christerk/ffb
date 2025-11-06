package com.fumbbl.ffb.server.model;

import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.server.InjuryResult;
import com.fumbbl.ffb.server.injury.injuryType.InjuryTypeServer;
import com.fumbbl.ffb.server.step.DeferredCommand;
import com.fumbbl.ffb.server.step.StepParameter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SteadyFootingContext implements IJsonSerializable {
	private DropPlayerContext dropPlayerContext;
	private InjuryTypeServer<?> injuryType;
	private InjuryResult injuryResult;
	private final List<StepParameter> stepParameters = new ArrayList<>();
	private final List<DeferredCommand> deferredCommands = new ArrayList<>();

	public SteadyFootingContext(DropPlayerContext dropPlayerContext) {
		this(dropPlayerContext, null, null);
	}

	public SteadyFootingContext(InjuryTypeServer<?> injuryType) {
		this(injuryType, null, null);
	}

	public SteadyFootingContext(InjuryResult injuryResult) {
		this(injuryResult, null, null);
	}

	public SteadyFootingContext(DropPlayerContext dropPlayerContext, List<StepParameter> stepParameters, List<DeferredCommand> deferredCommands) {
		this.dropPlayerContext = dropPlayerContext;
		if (stepParameters != null) {
			this.stepParameters.addAll(stepParameters);
		}
		if (deferredCommands != null) {
			this.deferredCommands.addAll(deferredCommands);
		}
	}

	public SteadyFootingContext(InjuryTypeServer<?> injuryType, List<StepParameter> stepParameters, List<DeferredCommand> deferredCommands) {
		this.injuryType = injuryType;
		if (stepParameters != null) {
			this.stepParameters.addAll(stepParameters);
		}
		if (deferredCommands != null) {
			this.deferredCommands.addAll(deferredCommands);
		}
	}

	public SteadyFootingContext(InjuryResult injuryResult, List<StepParameter> stepParameters, List<DeferredCommand> deferredCommands) {
		this.injuryResult = injuryResult;
		if (stepParameters != null) {
			this.stepParameters.addAll(stepParameters);
		}
		if (deferredCommands != null) {
			this.deferredCommands.addAll(deferredCommands);
		}
	}

	public DropPlayerContext getDropPlayerContext() {
		return dropPlayerContext;
	}

	public InjuryTypeServer<?> getInjuryType() {
		return injuryType;
	}

	public InjuryResult getInjuryResult() {
		return injuryResult;
	}

	public ApothecaryMode getApothecaryMode() {
		if (dropPlayerContext != null) {
			return dropPlayerContext.getApothecaryMode();
		}
		if (injuryResult != null) {
			return injuryResult.injuryContext().getApothecaryMode();
		}
		return ApothecaryMode.ATTACKER;
	}

	public List<StepParameter> getStepParameters() {
		return Collections.unmodifiableList(stepParameters);
	}

	public List<DeferredCommand> getDeferredCommands() {
		return Collections.unmodifiableList(deferredCommands);
	}

	@Override
	public Object initFrom(IFactorySource source, JsonValue jsonValue) {
		// TODO
		return null;
	}

	@Override
	public JsonValue toJsonValue() {
		// TODO
		return null;
	}
}
