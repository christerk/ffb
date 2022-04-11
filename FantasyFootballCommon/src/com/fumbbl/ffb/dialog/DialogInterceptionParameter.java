package com.fumbbl.ffb.dialog;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;

/**
 * 
 * @author Kalimar
 */
public class DialogInterceptionParameter implements IDialogParameter {

	private String fThrowerId;

	public DialogInterceptionParameter() {
		super();
	}

	public DialogInterceptionParameter(String pPlayerId) {
		fThrowerId = pPlayerId;
	}

	public DialogId getId() {
		return DialogId.INTERCEPTION;
	}

	public String getThrowerId() {
		return fThrowerId;
	}

	// transformation

	public IDialogParameter transform() {
		return new DialogInterceptionParameter(getThrowerId());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
		IJsonOption.THROWER_ID.addTo(jsonObject, fThrowerId);
		return jsonObject;
	}

	public DialogInterceptionParameter initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(source, jsonObject));
		fThrowerId = IJsonOption.THROWER_ID.getFrom(source, jsonObject);
		return this;
	}

}
