package com.balancedbytes.games.ffb.server.step.bb2020.state;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.IJsonSerializable;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.mechanics.PassResult;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

public class PassState implements IJsonSerializable {
	private String catcherId;
	private boolean passSkillUsed, bombMode, landingOutOfBounds;
	private PassResult result;
	private FieldCoordinate throwerCoordinate;

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

	public boolean isBombMode() {
		return bombMode;
	}

	public void setBombMode(boolean bombMode) {
		this.bombMode = bombMode;
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
		IServerJsonOption.BOMB_MODE.addTo(jsonObject, bombMode);
		IServerJsonOption.OUT_OF_BOUNDS.addTo(jsonObject, landingOutOfBounds);
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
		bombMode = IServerJsonOption.BOMB_MODE.getFrom(game, jsonObject);
		landingOutOfBounds = IServerJsonOption.OUT_OF_BOUNDS.getFrom(game, jsonObject);
		return this;
	}
}
