package com.fumbbl.ffb.server.model;

import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.CatchScatterThrowInMode;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonSerializable;

public class SteadyFootingState implements IJsonSerializable {

	private CatchScatterThrowInMode catchScatterThrowInMode;
	private Boolean endTurn;
	private Boolean endPlayerAction;
	private String playerId;
	private ApothecaryMode apothecaryMode;

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


	public void clear()  {
		catchScatterThrowInMode = null;
		endTurn = null;
		endPlayerAction = null;
		playerId = null;
		apothecaryMode = null;
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
