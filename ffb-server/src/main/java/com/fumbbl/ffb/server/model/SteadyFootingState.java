package com.fumbbl.ffb.server.model;

import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.CatchScatterThrowInMode;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.server.InjuryResult;
import com.fumbbl.ffb.server.injury.injuryType.InjuryTypeServer;

public class SteadyFootingState implements IJsonSerializable {

	private DropPlayerContext dropPlayerContext;
	private CatchScatterThrowInMode catchScatterThrowInMode;
	private Boolean endTurn;
	private Boolean endPlayerAction;
	private InjuryResult injuryResult;
	private String playerId;
	private ApothecaryMode apothecaryMode;
	private InjuryTypeServer<?> injuryType;

	public DropPlayerContext getDropPlayerContext() {
		return dropPlayerContext;
	}

	public void setDropPlayerContext(DropPlayerContext dropPlayerContext) {
		this.dropPlayerContext = dropPlayerContext;
	}

	public CatchScatterThrowInMode getCatchScatterThrowInMode() {
		return catchScatterThrowInMode;
	}

	public void setCatchScatterThrowInMode(CatchScatterThrowInMode catchScatterThrowInMode) {
		this.catchScatterThrowInMode = catchScatterThrowInMode;
	}

	public Boolean isEndTurn() {
		return endTurn;
	}

	public void setEndTurn(boolean endTurn) {
		this.endTurn = endTurn;
	}

	public Boolean isEndPlayerAction() {
		return endPlayerAction;
	}

	public void setEndPlayerAction(boolean endPlayerAction) {
		this.endPlayerAction = endPlayerAction;
	}

	public InjuryResult getInjuryResult() {
		return injuryResult;
	}

	public void setInjuryResult(InjuryResult injuryResult) {
		this.injuryResult = injuryResult;
	}

	public String getPlayerId() {
		return playerId;
	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}

	public ApothecaryMode getApothecaryMode() {
		return apothecaryMode;
	}

	public void setApothecaryMode(ApothecaryMode apothecaryMode) {
		this.apothecaryMode = apothecaryMode;
	}

	public InjuryTypeServer<?> getInjuryType() {
		return injuryType;
	}

	public void setInjuryType(InjuryTypeServer<?> injuryType) {
		this.injuryType = injuryType;
	}

	public void clear()  {
		dropPlayerContext = null;
		catchScatterThrowInMode = null;
		endTurn = null;
		endPlayerAction = null;
		injuryResult = null;
		playerId = null;
		apothecaryMode = null;
		injuryType = null;
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
