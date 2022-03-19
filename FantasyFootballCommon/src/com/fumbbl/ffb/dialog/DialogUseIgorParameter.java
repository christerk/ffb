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
public class DialogUseIgorParameter implements IDialogParameter {

	private String fPlayerId;

	public DialogUseIgorParameter() {
		super();
	}

	public DialogUseIgorParameter(String pPlayerId) {
		fPlayerId = pPlayerId;
	}

	public DialogId getId() {
		return DialogId.USE_IGOR;
	}

	public String getPlayerId() {
		return fPlayerId;
	}

	// transformation

	public IDialogParameter transform() {
		return new DialogUseIgorParameter(getPlayerId());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
		IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
		return jsonObject;
	}

	public DialogUseIgorParameter initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(source, jsonObject));
		fPlayerId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		return this;
	}

}
