package com.fumbbl.ffb.dialog;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.IDialogParameter;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;

public class DialogSwarmingPlayersParameter implements IDialogParameter {

	private int amount;

	@Override
	public DialogId getId() {
		return DialogId.SWARMING;
	}

	public DialogSwarmingPlayersParameter() {
	}

	public DialogSwarmingPlayersParameter(int amount) {
		this.amount = amount;
	}

	@Override
	public IDialogParameter transform() {
		return new DialogSwarmingPlayersParameter(amount);
	}

	public int getAmount() {
		return amount;
	}

	@Override
	public IDialogParameter initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(game, jsonObject));
		amount = IJsonOption.SWARMING_PLAYER_AMOUNT.getFrom(game, jsonObject);
		return this;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
		IJsonOption.SWARMING_PLAYER_AMOUNT.addTo(jsonObject, amount);
		return jsonObject;
	}
}
