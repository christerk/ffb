package com.fumbbl.ffb.dialog;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;

public class DialogSwarmingErrorParameter implements IDialogParameter {

	private int allowed, actual;

	public DialogSwarmingErrorParameter() {
	}

	public DialogSwarmingErrorParameter(int allowed, int actual) {
		this.allowed = allowed;
		this.actual = actual;
	}

	public int getAllowed() {
		return allowed;
	}

	public int getActual() {
		return actual;
	}

	@Override
	public DialogId getId() {
		return DialogId.SWARMING_ERROR;
	}

	@Override
	public IDialogParameter transform() {
		return new DialogSwarmingErrorParameter(allowed, actual);
	}

	@Override
	public IDialogParameter initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(source, jsonObject));
		allowed = IJsonOption.SWARMING_PLAYER_ALLOWED.getFrom(source, jsonObject);
		actual = IJsonOption.SWARMING_PLAYER_ACTUAL.getFrom(source, jsonObject);
		return this;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
		IJsonOption.SWARMING_PLAYER_ALLOWED.addTo(jsonObject, allowed);
		IJsonOption.SWARMING_PLAYER_ACTUAL.addTo(jsonObject, actual);
		return jsonObject;
	}

}
