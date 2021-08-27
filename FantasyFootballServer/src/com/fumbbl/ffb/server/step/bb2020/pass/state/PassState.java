package com.fumbbl.ffb.server.step.bb2020.pass.state;

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
	private String catcherId, interceptorId;
	private boolean passSkillUsed, landingOutOfBounds, interceptorChosen, deflectionSuccessful, interceptionSuccessful;
	private PassResult result;
	private FieldCoordinate throwerCoordinate;
	private TurnMode oldTurnMode;

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

	public boolean isLandingOutOfBounds() {
		return landingOutOfBounds;
	}

	public void setLandingOutOfBounds(boolean landingOutOfBounds) {
		this.landingOutOfBounds = landingOutOfBounds;
	}

	public PassResult getResult() {
		return result;
	}

	public void setResult(PassResult result) {
		this.result = result;
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
		IServerJsonOption.OUT_OF_BOUNDS.addTo(jsonObject, landingOutOfBounds);
		IServerJsonOption.INTERCEPTOR_ID.addTo(jsonObject, interceptorId);
		IServerJsonOption.INTERCEPTOR_CHOSEN.addTo(jsonObject, interceptorChosen);
		IServerJsonOption.OLD_TURN_MODE.addTo(jsonObject, oldTurnMode);
		IServerJsonOption.INTERCEPTION_SUCCESSFUL.addTo(jsonObject, deflectionSuccessful);
		return jsonObject;
	}

	@Override
	public PassState initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		catcherId = IServerJsonOption.CATCHER_ID.getFrom(game, jsonObject);
		result = (PassResult) IServerJsonOption.PASS_RESULT.getFrom(game, jsonObject);
		passSkillUsed = IServerJsonOption.PASS_SKILL_USED.getFrom(game, jsonObject);
		JsonObject throwerObject = IJsonOption.FIELD_COORDINATE_THROWER.getFrom(game, jsonObject);
		if (throwerObject != null) {
			throwerCoordinate = (FieldCoordinate) new FieldCoordinate(0).initFrom(game, throwerObject);
		}
		landingOutOfBounds = IServerJsonOption.OUT_OF_BOUNDS.getFrom(game, jsonObject);
		interceptorId = IServerJsonOption.INTERCEPTOR_ID.getFrom(game, jsonObject);
		interceptorChosen = IServerJsonOption.INTERCEPTOR_CHOSEN.getFrom(game, jsonObject);
		oldTurnMode = (TurnMode) IServerJsonOption.OLD_TURN_MODE.getFrom(game, jsonObject);
		deflectionSuccessful = IServerJsonOption.INTERCEPTION_SUCCESSFUL.getFrom(game, jsonObject);
		return this;
	}
}
