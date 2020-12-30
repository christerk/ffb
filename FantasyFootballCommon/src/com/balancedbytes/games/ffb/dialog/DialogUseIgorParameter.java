package com.balancedbytes.games.ffb.dialog;

import com.balancedbytes.games.ffb.IDialogParameter;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

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

	public DialogUseIgorParameter initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(game, jsonObject));
		fPlayerId = IJsonOption.PLAYER_ID.getFrom(game, jsonObject);
		return this;
	}

}
