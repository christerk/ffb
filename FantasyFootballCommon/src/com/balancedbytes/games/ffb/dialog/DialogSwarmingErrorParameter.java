package com.balancedbytes.games.ffb.dialog;

import com.balancedbytes.games.ffb.IDialogParameter;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

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
	public IDialogParameter initFrom(Game game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(game, jsonObject));
		allowed = IJsonOption.SWARMING_PLAYER_ALLOWED.getFrom(game, jsonObject);
		actual = IJsonOption.SWARMING_PLAYER_ACTUAL.getFrom(game, jsonObject);
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
