package com.balancedbytes.games.ffb.dialog;

import com.balancedbytes.games.ffb.IDialogParameter;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

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

	public DialogInterceptionParameter initFrom(Game game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(game, jsonObject));
		fThrowerId = IJsonOption.THROWER_ID.getFrom(game, jsonObject);
		return this;
	}

}
