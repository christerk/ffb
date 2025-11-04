package com.fumbbl.ffb.server.model;

import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.injury.InjuryType;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.server.InjuryResult;
import com.fumbbl.ffb.server.injury.injuryType.InjuryTypeServer;

public class SteadyFootingContext implements IJsonSerializable {
	private DropPlayerContext dropPlayerContext;
	private InjuryTypeServer<?> injuryType;
	private InjuryResult injuryResult;

	public SteadyFootingContext(DropPlayerContext dropPlayerContext) {
		this.dropPlayerContext = dropPlayerContext;
	}

	public SteadyFootingContext(InjuryTypeServer<?> injuryType) {
		this.injuryType = injuryType;
	}

	public SteadyFootingContext(InjuryResult injuryResult) {
		this.injuryResult = injuryResult;
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
