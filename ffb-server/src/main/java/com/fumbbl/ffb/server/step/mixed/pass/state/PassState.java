package com.fumbbl.ffb.server.step.mixed.pass.state;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.mechanics.PassResult;
import com.fumbbl.ffb.server.IServerJsonOption;

public class PassState implements IJsonSerializable {
	private String catcherId, interceptorId, originalBombardier;
	private boolean passSkillUsed, interceptorChosen, deflectionSuccessful, interceptionSuccessful;
	private PassResult result;
	private FieldCoordinate throwerCoordinate;
	private TurnMode oldTurnMode;
	private Boolean usingBlastIt, throwTwoBombs;

	public String getInterceptorId() {
		return interceptorId;
	}

	public void setInterceptorId(String interceptorId) {
		this.interceptorId = interceptorId;
	}

	public boolean isInterceptorChosen() {
		return interceptorChosen;
	}

	public void setInterceptorChosen(boolean interceptorChosen) {
		this.interceptorChosen = interceptorChosen;
	}

	public boolean isDeflectionSuccessful() {
		return deflectionSuccessful;
	}

	public void setDeflectionSuccessful(boolean deflectionSuccessful) {
		this.deflectionSuccessful = deflectionSuccessful;
	}

	public boolean isInterceptionSuccessful() {
		return interceptionSuccessful;
	}

	public void setInterceptionSuccessful(boolean interceptionSuccessful) {
		this.interceptionSuccessful = interceptionSuccessful;
	}

	public TurnMode getOldTurnMode() {
		return oldTurnMode;
	}

	public void setOldTurnMode(TurnMode oldTurnMode) {
		this.oldTurnMode = oldTurnMode;
	}

	public FieldCoordinate getThrowerCoordinate() {
		return throwerCoordinate;
	}

	public void setThrowerCoordinate(FieldCoordinate throwerCoordinate) {
		this.throwerCoordinate = throwerCoordinate;
	}

	public String getCatcherId() {
		return catcherId;
	}

	public void setCatcherId(String catcherId) {
		this.catcherId = catcherId;
	}

	public boolean isPassSkillUsed() {
		return passSkillUsed;
	}

	public void setPassSkillUsed(boolean passSkillUsed) {
		this.passSkillUsed = passSkillUsed;
	}

	public PassResult getResult() {
		return result;
	}

	public void setResult(PassResult result) {
		this.result = result;
	}

	public String getOriginalBombardier() {
		return originalBombardier;
	}

	public void setOriginalBombardier(String originalBombardier) {
		this.originalBombardier = originalBombardier;
	}

	public Boolean getUsingBlastIt() {
		return usingBlastIt;
	}

	public void setUsingBlastIt(Boolean usingBlastIt) {
		this.usingBlastIt = usingBlastIt;
	}

	public Boolean getThrowTwoBombs() {
		return throwTwoBombs;
	}

	public void setThrowTwoBombs(Boolean throwTwoBombs) {
		this.throwTwoBombs = throwTwoBombs;
	}

	public PassState populate(PassState passState) {
		if (passState != null) {
			originalBombardier = passState.originalBombardier;
			throwTwoBombs = passState.throwTwoBombs;
		}
		return this;
	}

	public void reset() {
		originalBombardier = null;
		throwTwoBombs = null;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IServerJsonOption.CATCHER_ID.addTo(jsonObject, catcherId);
		IServerJsonOption.PASS_RESULT.addTo(jsonObject, result);
		IServerJsonOption.PASS_SKILL_USED.addTo(jsonObject, passSkillUsed);
		if (throwerCoordinate != null) {
			IJsonOption.FIELD_COORDINATE_THROWER.addTo(jsonObject, throwerCoordinate.toJsonValue());
		}
		IServerJsonOption.INTERCEPTOR_ID.addTo(jsonObject, interceptorId);
		IServerJsonOption.INTERCEPTOR_CHOSEN.addTo(jsonObject, interceptorChosen);
		IServerJsonOption.OLD_TURN_MODE.addTo(jsonObject, oldTurnMode);
		IServerJsonOption.INTERCEPTION_SUCCESSFUL.addTo(jsonObject, deflectionSuccessful);
		IServerJsonOption.ORIGINAL_BOMBER.addTo(jsonObject, originalBombardier);
		IServerJsonOption.USING_BLAST_IT.addTo(jsonObject, usingBlastIt);
		IServerJsonOption.THROW_TWO_BOMBS.addTo(jsonObject, throwTwoBombs);
		return jsonObject;
	}

	@Override
	public PassState initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		catcherId = IServerJsonOption.CATCHER_ID.getFrom(source, jsonObject);
		result = (PassResult) IServerJsonOption.PASS_RESULT.getFrom(source, jsonObject);
		passSkillUsed = IServerJsonOption.PASS_SKILL_USED.getFrom(source, jsonObject);
		JsonObject throwerObject = IJsonOption.FIELD_COORDINATE_THROWER.getFrom(source, jsonObject);
		if (throwerObject != null) {
			throwerCoordinate = new FieldCoordinate().initFrom(source, throwerObject);
		}
		interceptorId = IServerJsonOption.INTERCEPTOR_ID.getFrom(source, jsonObject);
		interceptorChosen = IServerJsonOption.INTERCEPTOR_CHOSEN.getFrom(source, jsonObject);
		oldTurnMode = (TurnMode) IServerJsonOption.OLD_TURN_MODE.getFrom(source, jsonObject);
		deflectionSuccessful = IServerJsonOption.INTERCEPTION_SUCCESSFUL.getFrom(source, jsonObject);
		originalBombardier = IServerJsonOption.ORIGINAL_BOMBER.getFrom(source, jsonObject);
		usingBlastIt = IServerJsonOption.USING_BLAST_IT.getFrom(source, jsonObject);
		throwTwoBombs = IServerJsonOption.THROW_TWO_BOMBS.getFrom(source, jsonObject);
		return this;
	}
}
