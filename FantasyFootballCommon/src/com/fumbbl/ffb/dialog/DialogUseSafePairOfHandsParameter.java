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
public class DialogUseSafePairOfHandsParameter implements IDialogParameter {

	private String fPlayerId;

	public DialogUseSafePairOfHandsParameter() {
		super();
	}

	public DialogUseSafePairOfHandsParameter(String pPlayerId) {
		fPlayerId = pPlayerId;
	}

	public DialogId getId() {
		return DialogId.USE_SAFE_PAIR_OF_HANDS;
	}

	public String getPlayerId() {
		return fPlayerId;
	}

	// transformation

	public IDialogParameter transform() {
		return new DialogUseSafePairOfHandsParameter(getPlayerId());
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
		IJsonOption.PLAYER_ID.addTo(jsonObject, fPlayerId);
		return jsonObject;
	}

	public DialogUseSafePairOfHandsParameter initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(game, jsonObject));
		fPlayerId = IJsonOption.PLAYER_ID.getFrom(game, jsonObject);
		return this;
	}

}
