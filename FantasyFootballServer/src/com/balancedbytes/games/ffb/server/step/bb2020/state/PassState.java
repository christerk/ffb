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
	private String goToLabelOnEnd, goToLabelOnSavedFumble, goToLabelOnMissedPass, catcherId, throwerId;
	private boolean passSkillUsed, bombMode, landingOutOfBounds;
	private PassResult result;
	private FieldCoordinate landingCoordinate, throwerCoordinate;

	public String getThrowerId() {
		return throwerId;
	}

	public void setThrowerId(String throwerId) {
		this.throwerId = throwerId;
	}

	public FieldCoordinate getThrowerCoordinate() {
		return throwerCoordinate;
	}

	public void setThrowerCoordinate(FieldCoordinate throwerCoordinate) {
		this.throwerCoordinate = throwerCoordinate;
	}

	public FieldCoordinate getLandingCoordinate() {
		return landingCoordinate;
	}

	public void setLandingCoordinate(FieldCoordinate landingCoordinate) {
		this.landingCoordinate = landingCoordinate;
	}

	public String getGoToLabelOnEnd() {
		return goToLabelOnEnd;
	}

	public void setGoToLabelOnEnd(String goToLabelOnEnd) {
		this.goToLabelOnEnd = goToLabelOnEnd;
	}

	public String getGoToLabelOnSavedFumble() {
		return goToLabelOnSavedFumble;
	}

	public void setGoToLabelOnSavedFumble(String goToLabelOnSavedFumble) {
		this.goToLabelOnSavedFumble = goToLabelOnSavedFumble;
	}

	public String getGoToLabelOnMissedPass() {
		return goToLabelOnMissedPass;
	}

	public void setGoToLabelOnMissedPass(String goToLabelOnMissedPass) {
		this.goToLabelOnMissedPass = goToLabelOnMissedPass;
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
		IServerJsonOption.GOTO_LABEL_ON_END.addTo(jsonObject, goToLabelOnEnd);
		IServerJsonOption.GOTO_LABEL_ON_MISSED_PASS.addTo(jsonObject, goToLabelOnMissedPass);
		IServerJsonOption.CATCHER_ID.addTo(jsonObject, catcherId);
		IServerJsonOption.PASS_RESULT.addTo(jsonObject, result);
		IServerJsonOption.PASS_SKILL_USED.addTo(jsonObject, passSkillUsed);
		IServerJsonOption.THROWER_ID.addTo(jsonObject, throwerId);
		if (landingCoordinate != null) {
			IJsonOption.FIELD_COORDINATE_LANDING.addTo(jsonObject, landingCoordinate.toJsonValue());
		}
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
		goToLabelOnEnd = IServerJsonOption.GOTO_LABEL_ON_END.getFrom(game, jsonObject);
		goToLabelOnMissedPass = IServerJsonOption.GOTO_LABEL_ON_MISSED_PASS.getFrom(game, jsonObject);
		catcherId = IServerJsonOption.CATCHER_ID.getFrom(game, jsonObject);
		result = (PassResult) IServerJsonOption.PASS_RESULT.getFrom(game, jsonObject);
		passSkillUsed = IServerJsonOption.PASS_SKILL_USED.getFrom(game, jsonObject);
		throwerId = IServerJsonOption.THROWER_ID.getFrom(game, jsonObject);
		JsonObject landingObject = IJsonOption.FIELD_COORDINATE_LANDING.getFrom(game, jsonObject);
		if (landingObject != null) {
			landingCoordinate = (FieldCoordinate) new FieldCoordinate(0).initFrom(game, landingObject);
		}
		JsonObject throwerObject = IJsonOption.FIELD_COORDINATE_THROWER.getFrom(game, jsonObject);
		if (throwerObject != null) {
			throwerCoordinate = (FieldCoordinate) new FieldCoordinate(0).initFrom(game, throwerObject);
		}
		bombMode = IServerJsonOption.BOMB_MODE.getFrom(game, jsonObject);
		landingOutOfBounds = IServerJsonOption.OUT_OF_BOUNDS.getFrom(game, jsonObject);
		return this;
	}
}
